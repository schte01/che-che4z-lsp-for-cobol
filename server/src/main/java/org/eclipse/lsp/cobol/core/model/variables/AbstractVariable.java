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

package org.eclipse.lsp.cobol.core.model.variables;

import org.eclipse.lsp.cobol.core.model.Locality;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/** This abstract class implements the functionality that is common for all the variables. */
@AllArgsConstructor
public abstract class AbstractVariable implements Variable {
  @Getter protected final int levelNumber;
  @Getter protected final String name;
  @Getter protected final Locality definition;
  private final List<Locality> usages = new ArrayList<>();
  private final List<ConditionDataName> conditionChildren = new ArrayList<>();
  private Variable parent;

  @Override
  public @NonNull List<Locality> getUsages() {
    return usages;
  }

  @Override
  public void addUsage(@NonNull Locality usage) {
    usages.add(usage);
  }

  public Variable getParent() {
    return parent;
  }

  @Override
  public boolean isConditional() {
    return true;
  }

  @Override
  public void addConditionName(ConditionDataName variable) {
    conditionChildren.add(variable);
  }

  @Override
  public String getFormattedDisplayLine() {
    return String.format("%1$02d %2$s.", levelNumber, name);
  }

  @Override
  public List<ConditionDataName> getConditionNames() {
    return conditionChildren;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    AbstractVariable that = (AbstractVariable) o;
    return levelNumber == that.levelNumber
        && Objects.equals(name, that.name)
        && Objects.equals(definition, that.definition)
        && Objects.equals(usages, that.usages)
        && Objects.equals(conditionChildren, that.conditionChildren)
        && Objects.equals(parent, that.parent);
  }

  @Override
  public int hashCode() {
    return Objects.hash(levelNumber, name, definition, usages, conditionChildren, parent);
  }
}
