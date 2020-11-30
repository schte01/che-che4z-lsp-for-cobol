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

package com.broadcom.lsp.cobol.core.model.variables;

import com.broadcom.lsp.cobol.core.model.Locality;
import com.broadcom.lsp.cobol.core.preprocessor.delegates.util.VariableUtils;
import lombok.Value;

/**
 * This value class represents an independent element item COBOL variable, that has a level number
 * 77. It should always have a PIC clause representing its type, and a VALUE clause that stores an
 * explicitly defined value; both as Strings. They cannot produce a structure in any way.
 */
@Value
public class IndependentDataItem implements Variable {
  private String name;
  private String qualifier;
  private Locality definition;
  private String picClause;
  private String value;

  @Override
  public Variable rename(String renameItemName) {
    return new IndependentDataItem(
        name,
        VariableUtils.renameQualifier(qualifier, renameItemName),
        definition,
        picClause,
        value);
  }
  @Override
  public boolean isRenameable() {
    return false;
  }
}
