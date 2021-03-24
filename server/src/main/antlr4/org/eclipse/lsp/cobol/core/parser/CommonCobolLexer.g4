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
 *   Broadcom, Inc. - initial API and implementation
 */
 // 3/24 - Added common keywords (see CICSParser for details)

lexer grammar CommonCobolLexer;

@lexer::members {
    boolean sqlFlag = false;
    String lastTokenText = null;
    public void emit(Token token) {
        super.emit(token);
        lastTokenText = token.getText();
    }
}

ACTION : A C T I O N;
ACTIVITY : A C T I V I T Y {!sqlFlag}?;
ADD : A D D;
ADDRESS : A D D R E S S;
AFTER : A F T E R;
ALIAS : A L I A S;
ALLOCATE : A L L O C A T E;
ALTER : A L T E R;
ALTERNATE : A L T E R N A T E;
AND : A N D;
ANY : A N Y;
AS : A S;
ASSOCIATED_DATA : A S S O C I A T E D MINUSCHAR D A T A;
ASSOCIATED_DATA_LENGTH : A S S O C I A T E D MINUSCHAR D A T A MINUSCHAR L E N G T H;
AT : A T;
ATTRIBUTES : A T T R I B U T E S;
AUTHENTICATE : A U T H E N T I C A T E;
AUXILIARY : A U X I L I A R Y;
BINARY : B I N A R Y;
BIT : B I T;
BOUNDS : B O U N D S;
BY : B Y;
CAPABLE : C A P A B L E;
CCSID : C C S I D;
CCSVERSION : C C S V E R S I O N;
CHANGE : C H A N G E;
CHAR : C H A R;
CHECK : C H E C K;
CICS : C I C S;
CLIENT : C L I E N T;
CLOSE : C L O S E;
CLOSE_DISPOSITION : C L O S E MINUSCHAR D I S P O S I T I O N;
CODE : C O D E {!sqlFlag}?;
CODEPAGE : C O D E P A G E;
COLOR : C O L O R;
COMMITMENT : C O M M I T M E N T;
CONDITION : C O N D I T I O N;
CONNECT : C O N N E C T;
CONTROL : C O N T R O L;
CONVENTION : C O N V E N T I O N;
COPY : C O P Y;
CR : C R;
CREATE : C R E A T E;
CRUNCH : C R U N C H;
CS : C S;
CURRENT : C U R R E N T;
CURRENCY : C U R R E N C Y;
DATA : D A T A;
DATE : D A T E;
DAYOFMONTH : D A Y O F M O N T H;
DAYOFWEEK : D A Y O F W E E K;
DAYOFYEAR : D A Y O F Y E A R;
DAYS : D A Y S;
DB : D B;
DBCS : D B C S;
DEBUG : D E B U G;
DEFAULT_DISPLAY : D E F A U L T MINUSCHAR D I S P L A Y;
DEFINE : D E F I N E;
DELETE : D E L E T E;
DISK : D I S K;
DISPLAY : D I S P L A Y;
DOCUMENT : D O C U M E N T;
DUMP : D U M P;
DUPLICATE : D U P L I C A T E;
END : E N D;
ENDING : E N D I N G;
ENTER : E N T E R;
EQUAL : E Q U A L;
ERASE : E R A S E;
EVENT : E V E N T;
EXCEPTION : E X C E P T I O N;
EXTERNAL : E X T E R N A L;
EXTRACT : E X T R A C T;
FILE : F I L E;
FILLER : F I L L E R;
FOR : F O R;
FREE : F R E E;
FROM : F R O M;
FUNCTION_POINTER : F U N C T I O N MINUSCHAR P O I N T E R;
GENERIC : G E N E R I C;
GET : G E T;
HEX : H E X;
HOLD : H O L D;
HOURS : H O U R S;
IGNORE : I G N O R E;
IMMEDIATE : I M M E D I A T E;
IMPLICIT : I M P L I C I T;
INCREMENT : I N C R E M E N T;
INCLUDE : {if(lastTokenText.equals("EXEC SQL")) {sqlFlag = false;}} I N C L U D E;
INPUT : I N P U T;
INSERT : I N S E R T;
INTO : I N T O;
KEEP : K E E P;
KEPT : K E P T;
KEYBOARD : K E Y B O A R D;
LABEL : L A B E L;
LANGUAGE : L A N G U A G E;
LAST : L A S T;
LEADING : L E A D I N G;
LENGTH : L E N G T H;
LEVEL : L E V E L;
LINKAGE : L I N K A G E;
LIST : L I S T;
LOAD : L O A D;
LONG_DATE : L O N G MINUSCHAR D A T E;
LONG_TIME : L O N G MINUSCHAR T I M E;
MAIN : M A I N;
MINUTES : M I N U T E S;
MODE : M O D E;
MONTH : M O N T H;
MOVE : M O V E;
NAME : N A M E;
NATIONAL : N A T I O N A L;
NATIONAL_EDITED : N A T I O N A L MINUSCHAR E D I T E D;
NATLANG : N A T L A N G;
NETWORK : N E T W O R K;
NEXT : N E X T;
NO : N O;
NODE : N O D E;
NODUMP : N O D U M P;
NONE : N O N E;
NOWRITE : N O W R I T E;
NUMBER : N U M B E R;
NUMERIC_DATE : N U M E R I C MINUSCHAR D A T E;
NUMERIC_TIME : N U M E R I C MINUSCHAR T I M E;
OBJECT : O B J E C T;
ODT : O D T;
OF : O F;
OFF : O F F;
OFFSET : O F F S E T;
ON : O N;
OPEN : O P E N;
OPERATION : O P E R A T I O N;
OPTIMIZE : O P T I M I Z E;
OPTIONS : O P T I O N S;
OR : O R;
ORDERLY : O R D E R L Y;
OUT : O U T;
OUTPUT : O U T P U T;
OWN : O W N;
OWNER : O W N E R;
PAGE : P A G E;
PAGENUM : P A G E N U M;
PASSWORD : P A S S W O R D;
PATH : P A T H;
POINT : P O I N T;
POINTER_32 : P O I N T E R MINUSCHAR '3' '2';
PORT : P O R T;
PREPARE : P R E P A R E;
PRINTER : P R I N T E R;
PROCESS : P R O C E S S;
PROFILE : P R O F I L E;
PROGRAM : P R O G R A M;
QUOTE : Q U O T E;
QUERY : Q U E R Y;
READ : R E A D;
READER : R E A D E R;
RECEIVED : R E C E I V E D;
RECORD : R E C O R D;
RECORDS : R E C O R D S {!sqlFlag}?;
RECURSIVE : R E C U R S I V E;
RELEASE : R E L E A S E;
REMOTE : R E M O T E;
REMOVE : R E M O V E;
REPLACE : R E P L A C E;
REPLACING : R E P L A C I N G;
RESET : R E S E T;
RESTART : R E S T A R T;
RESULT : R E S U L T;
RESUME : R E S U M E {!sqlFlag}?;
RETAIN : R E T A I N;
RETURN : R E T U R N;
ROLE : R O L E;
ROLLBACK : R O L L B A C K;
RUN : R U N;
SAVE : S A V E;
SCHEME : S C H E M E;
SECONDS : S E C O N D S;
SECURITY : S E C U R I T Y;
SERVER : S E R V E R;
SESSION : S E S S I O N;
SET : S E T;
SHORT_DATE : S H O R T MINUSCHAR D A T E;
SIGNAL : S I G N A L;
START : S T A R T;
STATE : S T A T E;
STATUS : S T A T U S;
STORAGE : S T O R A G E;
THREAD_LOCAL : T H R E A D MINUSCHAR L O C A L;
TIME : T I M E;
TITLE : T I T L E;
TO : T O;
TODAYS_DATE : T O D A Y S MINUSCHAR D A T E;
TODAYS_NAME : T O D A Y S MINUSCHAR N A M E;
TOKEN : T O K E N;
TRACE : T R A C E;
TRAILING : T R A I L I N G;
TRANSACTION : T R A N S A C T I O N;
TRANSFORM : T R A N S F O R M;
TRIGGER : T R I G G E R;
TYPE : T Y P E;
TYPEDEF : T Y P E D E F;
UNTIL : U N T I L;
UTF_8 : U T F MINUSCHAR '8';
VIRTUAL : V I R T U A L;
ZERO : Z E R O;
ZEROES : Z E R O E S;
ZEROS : Z E R O S;
SQL : S Q L;
EXEC_SQL: EXEC WS SQL {sqlFlag = true;};
END_EXEC : E N D MINUSCHAR E X E C {sqlFlag = false;};
EXEC : E X E C;
EXECUTE : E X E C U T E;
EXIT : E X I T;
EXTEND : E X T E N D;
FULL : F U L L;
FUNCTION : F U N C T I O N;
GDS : G D S;
GRAPHIC : G R A P H I C;
UPDATE : U P D A T E;
URL : U R L;
USERID : U S E R I D;
USING : U S I N G;
VALIDATION : V A L I D A T I O N;
VALUE : V A L U E;
VALUES : V A L U E S;
VOLUME : V O L U M E;
WAIT : W A I T;
WRITE : W R I T E;
YEAR : Y E A R;

// symbols
AMPCHAR : '&';
ASTERISKCHAR : '*';
DOUBLEASTERISKCHAR : '**';
COLONCHAR : ':';
COMMA_EOF : ',' EOF {!sqlFlag}? ->skip;
COMMA_LB : ',' ('\r' | '\n' | '\f' | '\t' | ' ')+ {!sqlFlag}? -> channel(HIDDEN);
COMMACHAR : ',';
COMMENTTAG : '*>';
COMMENTENTRYTAG : '*>CE';
DOLLARCHAR : '$';
DOUBLEQUOTE : '"';
// period full stopPosition
DOT_FS : '.' ('\r' | '\n' | '\f' | '\t' | ' ')+ | '.' EOF;
DOT : '.';
EQUALCHAR : '=';
LESSTHANCHAR : '<';
LESSTHANOREQUAL : '<=';
LPARENCHAR : '(';
MAP : M A P;
MAX : M A X;
MINUSCHAR : '-';
MIXED : M I X E D;
MORETHANCHAR : '>';
MORETHANOREQUAL : '>=';
NOTEQUALCHAR : '<>';
PLUSCHAR : '+';
SEMICOLON : ';';
SEMICOLON_FS : ';' ('\r' | '\n' | '\f' | '\t' | ' ')+ | ';' EOF;
SINGLEQUOTE : '\'';
RPARENCHAR : ')';
SEPARATE : S E P A R A T E;
SEQUENCE : S E Q U E N C E;
SIZE : S I Z E;
SLASHCHAR : '/';
SOURCE : S O U R C E;
SPACE : S P A C E;
SQLLINECOMMENTCHAR: '--';
SUPPRESS : S U P P R E S S;
TERMINAL : T E R M I N A L;
TEST : T E S T;
THREAD : T H R E A D;
UPPER : U P P E R;

LEVEL_NUMBER : ([1-9])|([0][1-9])|([1234][0-9]);
LEVEL_NUMBER_66 : '66';
LEVEL_NUMBER_77 : '77';
LEVEL_NUMBER_88 : '88';

INTEGERLITERAL : (PLUSCHAR | MINUSCHAR)? DIGIT+ | LEVEL_NUMBER;

// DECIMAL_CONST : DIGIT DIGIT DIGIT DIGIT DOT DIGIT {sqlFlag}? ;
SINGLEDIGITLITERAL : DIGIT {sqlFlag}? ;

NUMERICLITERAL : (PLUSCHAR | MINUSCHAR)? DIGIT* (DOT | COMMACHAR) DIGIT+ (('e' | 'E') (PLUSCHAR | MINUSCHAR)? DIGIT+)?;

NONNUMERICLITERAL : UNTRMSTRINGLITERAL | STRINGLITERAL | DBCSLITERAL | HEXNUMBER | NULLTERMINATED;

//TXTLITERAL : STRINGLITERAL | IDENTIFIER;
CHAR_STRING_CONSTANT : HEXNUMBER | STRINGLITERAL;

IDENTIFIER : ([a-zA-Z0-9]+ ([-_]+ [a-zA-Z0-9]+)*);
COPYBOOK_IDENTIFIER : [a-zA-Z0-9#@$]+ ([-_]+ [a-zA-Z0-9#@$]+)*;
FILENAME : IDENTIFIER+ '.' IDENTIFIER+;

OCTDIGITS : OCT_DIGIT {sqlFlag}? ;
HEX_NUMBERS : HEXNUMBER {sqlFlag}? ;

// whitespace, line breaks, comments, ...
NEWLINE : '\r'? '\n' -> channel(HIDDEN);
COMMENTLINE : COMMENTTAG WS ~('\n' | '\r')* -> channel(HIDDEN);
COMMENTENTRYLINE : COMMENTENTRYTAG WS ~('\n' | '\r')*  -> channel(HIDDEN);
WS : [ \t\f;]+ -> channel(HIDDEN);
SEPARATOR : ', ' {!sqlFlag}? -> channel(HIDDEN);

//SQL comments
SQLLINECOMMENT
	:	SQLLINECOMMENTCHAR ~[\r\n]* NEWLINE {sqlFlag}? -> channel(HIDDEN)
	;

// treat all the non-processed tokens as errors
ERRORCHAR : . ;

ZERO_DIGIT: '0';

fragment HEXNUMBER :
	X '"' [0-9A-F]+ '"'
	| X '\'' [0-9A-F]+ '\''
;

fragment NULLTERMINATED :
	Z '"' (~["\n\r] | '""' | '\'')* '"'
	| Z '\'' (~['\n\r] | '\'\'' | '"')* '\''
;

fragment STRINGLITERAL :
	'"' (~["\n\r] | '""' | '\'')* '"'
	| '\'' (~['\n\r] | '\'\'' | '"')* '\''
;

fragment UNTRMSTRINGLITERAL :
	'"' (~["\n\r] | '""' | '\'')*
	| '\'' (~['\n\r] | '\'\'' | '"')*
;

fragment DBCSLITERAL :
	[GN] '"' (~["\n\r] | '""' | '\'')* '"'
	| [GN] '\'' (~['\n\r] | '\'\'' | '"')* '\''
;

fragment
OCT_DIGIT        : [0-8] ;
fragment DIGIT: OCT_DIGIT | [9];
// case insensitive chars
fragment A:('a'|'A');
fragment B:('b'|'B');
fragment C:('c'|'C');
fragment D:('d'|'D');
fragment E:('e'|'E');
fragment F:('f'|'F');
fragment G:('g'|'G');
fragment H:('h'|'H');
fragment I:('i'|'I');
fragment J:('j'|'J');
fragment K:('k'|'K');
fragment L:('l'|'L');
fragment M:('m'|'M');
fragment N:('n'|'N');
fragment O:('o'|'O');
fragment P:('p'|'P');
fragment Q:('q'|'Q');
fragment R:('r'|'R');
fragment S:('s'|'S');
fragment T:('t'|'T');
fragment U:('u'|'U');
fragment V:('v'|'V');
fragment W:('w'|'W');
fragment X:('x'|'X');
fragment Y:('y'|'Y');
fragment Z:('z'|'Z');
