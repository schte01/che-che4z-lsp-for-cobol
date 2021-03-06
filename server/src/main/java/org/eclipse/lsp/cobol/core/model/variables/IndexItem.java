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
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.Value;

/**
 * This value class represents an Index item. It is defined using INDEXED BY statement in a {@link
 * TableDataName} declaration.
 */
@Value
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class IndexItem extends AbstractVariable {
  public IndexItem(int levelNumber, String name, Locality definition) {
    super(levelNumber, name, definition, null);
  }
  public IndexItem(int leverNumber, String name, Locality definition, Variable parent) {
    super(leverNumber, name, definition, parent);
  }

  /**
   * Create a new instance on IndexItem with updated parent link.
   *
   * @param parent a new parent
   * @return the new instance with updated parent
   */
  public IndexItem updateParent(Variable parent) {
    return new IndexItem(levelNumber, name, definition, parent);
  }

  @Override
  public String getFormattedDisplayLine() {
    return "INDEXED BY " + name;
  }
}
