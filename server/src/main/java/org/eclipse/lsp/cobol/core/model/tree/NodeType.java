/*
 * Copyright (c) 2021 Broadcom.
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
package org.eclipse.lsp.cobol.core.model.tree;

/**
 * Enumeration of Node types.
 */
public enum NodeType {
  ROOT,
  PROGRAM,
  SECTION,
  VARIABLE_DEFINITION,
  VARIABLE_USAGE,
  ANTLR_VARIABLE_DEFINITION,
  PROGRAM_ID,
  PROGRAM_END,
}
