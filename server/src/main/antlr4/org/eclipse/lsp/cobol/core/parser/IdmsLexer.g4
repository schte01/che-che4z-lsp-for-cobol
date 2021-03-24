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
 *   Broadcom, Inc. - initial API and implementation
 */
// 3/24 - newly created file to contain IDMS keywords
lexer grammar IdmsLexer;
import CICSLexer;

// idms keywords
CALC : C A L C;
DB_KEY : D B MINUSCHAR K E Y;
DBNAME : D B N A M E;
DBNODE : D B N O D E;
DICTNAME : D I C T N A M E;
DICTNODE : D I C T N O D E;
DUPLICATE : D U P L I C A T E;
ENDPAGE : E N D P A G E;
EXITS : E X I T S;
FINISH : F I N I S H;
FIND : F I N D;
IDMS : I D M S;
IDMS_CONTROL : I D M S MINUSCHAR C O N T R O L;
IDMS_RECORDS : I D M S MINUSCHAR R E C O R D S;
IGNORED : I G N O R E D;
INVOKED : I N V O K E D;
LEVELS : L E V E L S;
LTERM : L T E R M;
LR : L R;
MAPS : M A P S;
MAP_BINDS : MAP MINUSCHAR B I N D S;
MANUAL : M A N U A L;
MEMBERS : M E M B E R S;
MODIFY : M O D I F Y;
NODENAME : N O D E N A M E;
OBTAIN : O B T A I N;
PAGE_INFO : P A G E MINUSCHAR I N F O;
PERMANENT: P E R M A N E N T;
PROTECTED : P R O T E C T E D;
PTERM : P T E R M;
READY : R E A D Y;
RETRIEVAL : R E T R I E V A L;
RUN_UNIT : R U N MINUSCHAR U N I T;
SCRATCH : S C R A T C H;
SCREENSIZE : S C R E E N S I Z E;
SELECTIVE : S E L E C T I V E;
STORE : S T O R E;
SUBSCHEMA_AREANAMES : SSMinusChar A R E A N A M E S;
SUBSCHEMA_BINDS : SSMinusChar B I N D S;
SUBSCHEMA_CONTROL : SSMinusChar C O N T R O L;
SUBSCHEMA_CTRL : SSMinusChar C T R L;
SUBSCHEMA_DESCRIPTION : SSMinusChar D E S C R I P T I O N;
SUBSCHEMA_DML_LR_DESCRIPTION : SSMinusChar D M L MINUSCHAR L R MINUSCHAR D E S C R I P T I O N;
SUBSCHEMA_LR_CONTROL : SSMinusChar L R MINUSCHAR C O N T R O L;
SUBSCHEMA_LR_CTRL : SSMinusChar L R MINUSCHAR C T R L;
SUBSCHEMA_LR_DESCRIPTION : SSMinusChar L R MINUSCHAR D E S C R I P T I O N;
SUBSCHEMA_LR_NAMES : SSMinusChar L R MINUSCHAR N A M E S;
SUBSCHEMA_LR_RECORDS : SSMinusChar L R MINUSCHAR R E C O R D S;
SUBSCHEMA_NAMES : SSMinusChar N A M E S;
SUBSCHEMA_RECNAMES : SSMinusChar R E C N A M E S;
SUBSCHEMA_RECORDS : SSMinusChar R E C O R D S;
SUBSCHEMA_RECORD_BINDS : SSMinusChar R E C O R D MINUSCHAR B I N D S;
SUBSCHEMA_SETNAMES : SSMinusChar S E T N A M E S;
SUBSCHEMA_NAME : SSMinusChar N A M E;
SSNAMES : S U B S C H E M A MINUSCHAR N A M E S;
SYSVERSION : S Y S V E R S I O N;
USAGE_MODE : U S A G E  MINUSCHAR M O D E;
WITHIN : W I T H I N;
fragment SSMinusChar: S U B S C H E M A MINUSCHAR;