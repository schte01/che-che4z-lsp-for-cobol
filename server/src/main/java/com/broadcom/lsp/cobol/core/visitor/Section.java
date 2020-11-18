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

import java.util.List;

import static com.broadcom.lsp.cobol.core.CobolParser.*;

public enum Section {
  NOT_INITIALIZED(List.of()),
  FILE(
      List.of(
          DataDescriptionEntryFormat1Context.class,
          DataDescriptionEntryFormat2Context.class,
          DataDescriptionEntryFormat3Context.class)),
  WORKING_STORAGE(
      List.of(
          DataDescriptionEntryFormat1Context.class,
          DataDescriptionEntryFormat2Context.class,
          DataDescriptionEntryFormat3Context.class,
          DataDescriptionEntryFormat1Level77Context.class)),
  LINKAGE(
      List.of(
          DataDescriptionEntryFormat1Context.class,
          DataDescriptionEntryFormat2Context.class,
          DataDescriptionEntryFormat3Context.class,
          DataDescriptionEntryFormat1Level77Context.class)),
  LOCAL_STORAGE(
      List.of(
          DataDescriptionEntryFormat1Context.class,
          DataDescriptionEntryFormat2Context.class,
          DataDescriptionEntryFormat3Context.class));
  List<Class> allowedDefinitions;

  Section(List<Class> allowedTypes) {
    this.allowedDefinitions = allowedTypes;
  }

  public boolean allowsVariableType(Class variableType) {
    return allowedDefinitions.contains(variableType);
  }
}
