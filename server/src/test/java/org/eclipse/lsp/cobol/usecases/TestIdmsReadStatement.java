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

package org.eclipse.lsp.cobol.usecases;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.lsp.cobol.usecases.engine.UseCaseEngine;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

/** Test IDMS Read DML statement */
public class TestIdmsReadStatement {

  private static final String BOILERPLATE =
      "        IDENTIFICATION DIVISION. \r\n"
          + "        PROGRAM-ID. test1. \r\n"
          + "        DATA DIVISION. \r\n"
          + "        WORKING-STORAGE SECTION. \r\n"
          + "        01 {$*WK_1} PIC X(80).\n"
          + "        01 {$*WK_2} PIC X(80).\n"
          + "        01 {$*WK_POSITION} PIC S9(4) COMP SYNC.\n"
          + "        01 {$*WK_LENGTH} PIC S9(8) COMP SYNC.\n"
          + "        PROCEDURE DIVISION. \r\n";

  private static final String READ_TERMINAL =
      "            READ TERMINAL INTO {$WK_1} MAX LENGTH 30.\r\n";

  private static final String READ_TERMINAL_ALL_PARMS_LITERALS =
      "            READ TERMINAL WAIT MODIFIED FROM POSITION '0020' GET STORAGE\r\n"
          + "            INTO {$WK_1} MAX LENGTH 30 RETURN LENGTH {$WK_2}.\r\n";

  private static final String READ_TERMINAL_ALL_PARMS_VARIABLES =
      "            MOVE 20 TO {$WK_POSITION}. MOVE 80 TO {$WK_LENGTH}\r\n"
          + "            READ TERMINAL WAIT MODIFIED FROM POSITION {$WK_POSITION} GET\r\n"
          + "            STORAGE INTO {$WK_1} MAX LENGTH {$WK_LENGTH} RETURN LENGTH\r\n"
          + "            INTO {$WK_2}.\r\n";

  private static Stream<String> textsToTest() {
    return Stream.of(
        BOILERPLATE + READ_TERMINAL,
        BOILERPLATE + READ_TERMINAL_ALL_PARMS_LITERALS,
        BOILERPLATE + READ_TERMINAL_ALL_PARMS_VARIABLES);
  }

  @ParameterizedTest
  @MethodSource("textsToTest")
  @DisplayName("Parameterized - IDMS read tests")
  void test(String text) {
    UseCaseEngine.runTest(text, ImmutableList.of(), ImmutableMap.of());
  }
}
