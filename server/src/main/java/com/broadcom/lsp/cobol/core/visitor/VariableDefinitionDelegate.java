/*
 * Copyright (c) 2020 Broadcom.
 * The term "Broadcom" refers to Broadcom Inc. and/or its subsidiaries.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 *    Broadcom, Inc. - initial API and implementation
 *
 */
package com.broadcom.lsp.cobol.core.visitor;

import com.broadcom.lsp.cobol.core.CobolParser.*;
import com.broadcom.lsp.cobol.core.model.ErrorSeverity;
import com.broadcom.lsp.cobol.core.model.Locality;
import com.broadcom.lsp.cobol.core.model.ResultWithErrors;
import com.broadcom.lsp.cobol.core.model.SyntaxError;
import com.broadcom.lsp.cobol.core.model.variables.*;
import com.broadcom.lsp.cobol.core.preprocessor.delegates.util.VariableUtils;
import com.broadcom.lsp.cobol.core.semantics.outline.OutlineNodeNames;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.antlr.v4.runtime.ParserRuleContext;
import org.antlr.v4.runtime.RuleContext;
import org.antlr.v4.runtime.Token;
import org.eclipse.lsp4j.Range;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Function;

import static com.broadcom.lsp.cobol.core.model.ErrorSeverity.ERROR;
import static com.broadcom.lsp.cobol.core.model.ErrorSeverity.WARNING;
import static com.broadcom.lsp.cobol.core.visitor.Section.NOT_INITIALIZED;
import static java.lang.String.format;
import static java.util.Optional.*;
import static java.util.function.Predicate.not;
import static java.util.stream.Collectors.toList;

/**
 * This class processes the variable definition contexts. It accumulates the variable structures to
 * track the nesting, and the qualifiers that also rely on the structure. Pay attention, that the
 * <code>create*</code> methods are NOT pure, they share a state and rely on the context.
 *
 * <p>Qualifiers used to check the variable usages and may be used to check the uniqueness of the
 * reference using a regex, e.g. PARENT.*CHILD may check this usage even if there are intermediate
 * levels in the given structure. Qualifiers stored in top-down order.
 */
@Slf4j
@RequiredArgsConstructor
class VariableDefinitionDelegate {
  // TODO: externalize messages
  private static final String EMPTY_STRUCTURE_MSG =
      "A \"PICTURE\" clause was not found for elementary item %s";
  private static final String TOO_MANY_CLAUSES_MSG =
      "A duplicate %s clause was found in a data description entry";
  private static final String PREVIOUS_WITHOUT_PIC_FOR_88 =
      "An elementary item for the conditional data name %s is not found";
  private static final String AREA_A_WARNING = "CobolVisitor.AreaAWarningMsg";
  private static final String DEFINITION_NOT_ALLOWED_IN_SECTION =
      "This section doesn't allow a data definition of that type";
  private static final String NUMBER_NOT_ALLOWED_AT_TOP =
      "Only 01, 66 and 77 are allowed at the highest level";
  private static final String NO_STRUCTURE_BEFORE_RENAME =
      "No data definition entry found for RENAME";
  private static final String CHILDREN_TO_RENAME_NOT_FOUND =
      "The data entry with the name %s not found";
  private static final String INCORRECT_CHILDREN_ORDER =
      "The data entries to rename are specified in an incorrect order";
  private static final String CANNOT_BE_RENAMED = "The data entry %s cannot be renamed";

  private static final ErrorSeverity SEVERITY = ERROR;

  // TODO: make Value a separate class that accepts:
  // TODO: 1. literal
  // TODO: 2. literal THRU literal
  // TODO: 3. REFERENCE data-name
  // TODO: Make PIC a separate class that may validate the given value
  private Deque<StructuredVariable> structureStack = new ArrayDeque<>();
  // TODO: Check variable uniquity on usage
  private Deque<Variable> variables = new ArrayDeque<>();
  private List<SyntaxError> errors = new ArrayList<>();
  private Section section = NOT_INITIALIZED;

  private final Map<Token, Locality> positionMapping;

  void switchSection(@NonNull Section section) {
    this.section = section;
    closePreviousStructure();
  }

  @NonNull
  ResultWithErrors<Collection<Variable>> finishDefinitionAnalysis() {
    closePreviousStructure();
    section = NOT_INITIALIZED;
    return new ResultWithErrors<>(variables, errors);
  }

  /**
   * Create and accumulate a variable out of the given context and positions. May return semantic
   * errors for this definition if found, as well as errors for previous ones.
   *
   * @param ctx - a DataDescriptionEntryFormat1Context to retrieve the variable
   */
  void defineVariable(@NonNull DataDescriptionEntryFormat1Context ctx) {
    // TODO: add support for the REDEFINES clause:
    // TODO: 1. redefined variable defined
    // TODO: 2. add variable usage for REDEFINES
    // TODO: 3. _redefining_ cannot be redefined
    // TODO: 4. redefining level number should be the same
    // TODO: 5. 66 and 88 cannot be redefined
    // TODO: 6. the redefined element should belong to the same group
    // TODO: 7. redefining element should immediately follow
    // TODO: 8. redefining one may be group when RE is element
    // TODO: 9. cannot contain VALUE

    int number = Integer.parseInt(ctx.LEVEL_NUMBER().getText());

    // TODO: Add check that name does not present in the predefined variables list (? - to check)
    String name = retrieveName(ctx.dataName1());

    Locality definition = retrieveDefinition(ctx.dataName1());

    checkVariableTypeAllowed(ctx.getClass(), definition);
    checkStartingArea(number, positionMapping.get(ctx.LEVEL_NUMBER().getSymbol()));
    closePreviousStructureIfNeeded(number);
    checkTopElementNumber(number, definition);
    String qualifier = VariableUtils.createQualifier(structureStack, name);

    Optional<DataPictureClauseContext> picClause = retrieveClause(ctx.dataPictureClause());
    Optional<DataOccursClauseContext> occursClause = retrieveClause(ctx.dataOccursClause());
    // TODO: check the same way that the other clauses are singular or absent

    Variable variable;
    if (picClause.isEmpty()) {
      StructuredVariable structuredVariable =
          occursClause
              .<StructuredVariable>map(
                  it ->
                      new MultiTableDataName(
                          number,
                          name,
                          qualifier,
                          definition,
                          retrieveOccursTimes(occursClause.get()),
                          retrieveIndexItem(occursClause.get())))
              .orElse(new GroupItem(number, qualifier, name, definition));

      structureStack.push(structuredVariable);
      variable = structuredVariable;
    } else {
      // TODO: add check that the following items do not have VALUE:
      // TODO: 1. JUSTIFIED
      // TODO: 2. SYNCHRONIZED
      // TODO: 3. USAGE (not USAGE IN DISPLAY)
      String valueClauseText =
          retrieveClause(ctx.dataValueClause())
              .map(DataValueClauseContext::dataValueClauseLiteral)
              .map(ParserRuleContext::getText)
              .orElse("");

      String picClauseText = retrievePicText(picClause.get());
      variable =
          occursClause
              .map(
                  occurs ->
                      new TableDataName(
                          name,
                          qualifier,
                          definition,
                          picClauseText,
                          valueClauseText,
                          retrieveOccursTimes(occurs),
                          retrieveIndexItem(occurs)))
              .map(storeIndexVariables())
              .orElse(new ElementItem(name, qualifier, definition, picClauseText, valueClauseText));

      // TODO: add check that value does not exceed PIC length
      ofNullable(structureStack.peek()).ifPresent(it -> it.addChild(variable));
    }
    variables.push(variable);
  }

  void defineVariable(@NonNull DataDescriptionEntryFormat1Level77Context ctx) {
    String name = retrieveName(ctx.dataName1());
    Locality definition = retrieveDefinition(ctx.dataName1());

    checkVariableTypeAllowed(ctx.getClass(), definition);
    checkStartingArea(77, positionMapping.get(ctx.LEVEL_NUMBER_77().getSymbol()));
    closePreviousStructure();
    checkTopElementNumber(77, definition);

    String qualifier = VariableUtils.createQualifier(structureStack, name);
    String picClause =
        retrieveClause(ctx.dataPictureClause()).map(this::retrievePicText).orElse("");

    if (picClause.isEmpty()) {
      addError(format(EMPTY_STRUCTURE_MSG, name), definition);
    }

    String valueClauseText =
        retrieveClause(ctx.dataValueClause())
            .map(DataValueClauseContext::dataValueClauseLiteral)
            .map(ParserRuleContext::getText)
            .orElse("");

    variables.push(
        new IndependentDataItem(name, qualifier, definition, picClause, valueClauseText));
  }

  void defineVariable(@NonNull DataDescriptionEntryFormat2Context ctx) {
    String name = retrieveName(ctx.dataName1());
    Locality definition = retrieveDefinition(ctx.dataName1());
    String qualifier = VariableUtils.createQualifier(structureStack, name);
    checkVariableTypeAllowed(ctx.getClass(), definition);
    checkTopElementNumber(66, definition);
    StructuredVariable structure = structureStack.getFirst();
    Variable previousVariable = variables.peek();
    if (structure == null || previousVariable == null) {
      addError(NO_STRUCTURE_BEFORE_RENAME, definition);
      variables.push(new RenameItem(name, qualifier, definition, List.of()));
      return;
    }
    closePreviousStructure();

    List<Variable> childrenToRename;
    List<Variable> previousChildren = structure.getChildren();
    DataRenamesClauseContext renamesClause = ctx.dataRenamesClause();
    String startName = renamesClause.qualifiedDataName().getText();
    Optional<Variable> start = retrieveChild(previousChildren, startName);
    if (start.isEmpty()) {
      childrenToRename = List.of();
      reportChildrenNotFound(renamesClause, startName);
    } else if (renamesClause.thruDataName() != null) {
      String stopName = renamesClause.thruDataName().qualifiedDataName().getText();
      Optional<Variable> stop = retrieveChild(previousChildren, stopName);
      if (stop.isEmpty()) {
        reportChildrenNotFound(renamesClause, stopName);
        childrenToRename = List.of(start.get());
      } else {
        int fromIndex = previousChildren.indexOf(start.get());
        int toIndex = previousChildren.indexOf(stop.get());
        if (toIndex <= fromIndex) {
          addError(INCORRECT_CHILDREN_ORDER, definition);
          childrenToRename = List.of(start.get());
        } else childrenToRename = previousChildren.subList(fromIndex, toIndex);
      }
    } else {
      childrenToRename = List.of(start.get());
    }
    List<Variable> renamedVariables = renameVariables(name, childrenToRename);

    variables.push(new RenameItem(name, qualifier, definition, renamedVariables));
    renamedVariables.forEach(variables::push);
  }

  void defineVariable(@NonNull DataDescriptionEntryFormat3Context ctx) {
    String name = retrieveName(ctx.dataName1());
    Locality definition = retrieveDefinition(ctx.dataName1());
    String valueClauseText = ctx.dataValueClause().dataValueClauseLiteral().getText();
    ElementItem container = getConditionalContainer();

    checkVariableTypeAllowed(ctx.getClass(), definition);
    checkTopElementNumber(88, definition);

    ConditionalDataName variable;
    if (container == null) {
      addError(format(PREVIOUS_WITHOUT_PIC_FOR_88, name), definition);
      String qualifier = VariableUtils.createQualifier(structureStack, name);
      variable = new ConditionalDataName(name, qualifier, definition, valueClauseText);
    } else {
      String qualifier = container.getQualifier() + " " + name;
      variable = new ConditionalDataName(name, qualifier, definition, valueClauseText);
      container.addConditionalChild(variable);
    }
    variables.push(variable);
  }

  private String retrieveName(DataName1Context context) {
    return ofNullable(context).map(RuleContext::getText).orElse(OutlineNodeNames.FILLER_NAME);
  }

  private Optional<Variable> retrieveChild(List<Variable> childrenToRename, String stopName) {
    return childrenToRename.stream().filter(it -> it.getName().equals(stopName)).findFirst();
  }

  private List<Variable> renameVariables(String renameVariableName, List<Variable> children) {
    List<Variable> renamedVariables =
        children.stream().map(it -> it.rename(renameVariableName)).collect(toList());
    renamedVariables.stream()
        .filter(not(Renameable::isRenameable))
        .forEach(it -> addError(format(CANNOT_BE_RENAMED, it.getName()), it.getDefinition()));
    return renamedVariables;
  }

  private void reportChildrenNotFound(DataRenamesClauseContext renamesClause, String stopName) {
    addError(
        format(CHILDREN_TO_RENAME_NOT_FOUND, stopName),
        positionMapping.get(renamesClause.qualifiedDataName().getStart()));
  }

  private Function<TableDataName, Variable> storeIndexVariables() {
    return table -> {
      table.getIndexes().forEach(variables::push);
      return table;
    };
  }

  private void checkTopElementNumber(int number, Locality definition) {
    if (number == 1 || number == 66 || number == 77) return;
    addError(NUMBER_NOT_ALLOWED_AT_TOP, definition);
  }

  private void checkVariableTypeAllowed(Class variableType, Locality definition) {
    if (!section.allowsVariableType(variableType)) {
      addError(DEFINITION_NOT_ALLOWED_IN_SECTION, definition);
    }
  }

  @Nullable
  private ElementItem getConditionalContainer() {
    return (ElementItem)
        ofNullable(variables.peek())
            .filter(it -> it instanceof ElementItem)
            .filter(it -> ((ElementItem) it).getValue().isEmpty())
            .orElse(null);
  }

  @NonNull
  private Locality retrieveDefinition(@NonNull DataName1Context context) {
    return positionMapping.get(context.start);
  }

  @NonNull
  private String retrievePicText(@NonNull DataPictureClauseContext picClause) {
    return picClause.getText().replaceAll(picClause.getStart().getText(), "");
  }

  @NonNull
  private <T extends ParserRuleContext> Optional<T> retrieveClause(@NonNull List<T> clauses) {
    if (clauses.isEmpty()) return empty();
    if (clauses.size() > 1) {
      addError(
          format(TOO_MANY_CLAUSES_MSG, clauses.get(0).getStart().getText()),
          retrieveRangeLocality(clauses));
    }
    return of(clauses.get(0));
  }

  @NonNull
  private <T extends ParserRuleContext> Locality retrieveRangeLocality(@NonNull List<T> clauses) {
    Locality start = positionMapping.get(clauses.get(0).getStart());
    Locality end = positionMapping.get(clauses.get(clauses.size() - 1).getStop());
    Range range = new Range(start.getRange().getStart(), end.getRange().getEnd());
    return Locality.builder().uri(start.getUri()).range(range).build();
  }

  private int retrieveOccursTimes(@NonNull DataOccursClauseContext dataOccursClauseContexts) {
    return Integer.parseInt(dataOccursClauseContexts.integerLiteral().toString());
  }

  @NonNull
  private List<IndexItem> retrieveIndexItem(@NonNull DataOccursClauseContext clause) {
    return clause.indexName().stream()
        .map(it -> new IndexItem(it.getText(), it.getText(), positionMapping.get(it.start)))
        .collect(toList());
  }

  private void closePreviousStructure() {
    closePreviousStructureIfNeeded(0);
  }

  private void closePreviousStructureIfNeeded(int number) {
    ofNullable(structureStack.peek())
        .filter(it -> it.getChildren().isEmpty())
        .ifPresent(it -> addError(format(EMPTY_STRUCTURE_MSG, it.getName()), it.getDefinition()));
    structureStack.removeIf(it -> it.getLevelNumber() >= number);
  }

  private void checkStartingArea(int number, @NonNull Locality locality) {
    if ((number == 1 || number == 77) && locality.getRange().getStart().getCharacter() > 10) {
      addError(AREA_A_WARNING, locality, WARNING);
    }
  }

  private void addError(String suggestion, Locality locality) {
    addError(suggestion, locality, SEVERITY);
  }

  private void addError(String suggestion, Locality locality, ErrorSeverity severity) {
    SyntaxError error =
        SyntaxError.syntaxError()
            .locality(locality)
            .severity(severity)
            .suggestion(suggestion)
            .build();
    errors.add(error);
    LOG.debug(format("Syntax error found by %s: %s", getClass().getSimpleName(), error.toString()));
  }
}
