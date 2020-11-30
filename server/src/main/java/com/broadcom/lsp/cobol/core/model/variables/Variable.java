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

/**
 * This interface describes a COBOL Variable. Variable has a name and a unique definition locality.
 */
public interface Variable extends Renameable {
  /**
   * Get name of the variable
   *
   * @return String variable name
   */
  String getName();

  /**
   * Get definition locality
   *
   * @return the definition locality
   */
  Locality getDefinition();

  /**
   * Get qualifier of this variable. Contains the variable hierarchy top-down string representation
   * and this variable name; separated by whitespace. May be not unique on definition, required to
   * be unique on referencing.
   *
   * @return string qualifier
   */
  String getQualifier();
}
