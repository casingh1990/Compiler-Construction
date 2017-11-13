import java.util.HashMap;
import java.util.Map;

/**
 * Class for token.
 * The constants from this class is used through the compiler to ID tokens.
 * This class stores all relevant information about tokens.
 * @author Chandrashekar Singh
 *
 */
public class Token {
	private int token;
	private String value;
	private int line = 0;
	private int col = 0;

	/**
	 * 
	 private static final int NOT_FOUND = 0; private static final int LETTER =
	 * 1; private static final int DIGIT = 2; private static final int LITERAL =
	 * 3; private static final int OPERATOR = 4; private static final int
	 * COMMENT = 5; private static final int INCLUDE = 6;
	 */

	public static final int ABSOLUTE = 0;
	public static final int ABSTRACT = 1;
	public static final int ALL = 2;
	public static final int AND = 3;
	public static final int AND_THEN = 4;
	public static final int ARRAY = 5;
	public static final int ASM = 6;
	public static final int BEGIN = 7;
	public static final int BINDABLE = 8;
	public static final int CASE = 9;
	public static final int CLASS = 10;
	public static final int CONST = 11;
	public static final int CONSTRUCTOR = 12;
	public static final int DESTRUCTOR = 13;
	public static final int DIV = 14;
	public static final int DO = 15;
	public static final int DOWNTO = 16;
	public static final int ELSE = 17;
	public static final int END = 18;
	public static final int EXPORT = 19;
	public static final int FILE = 20;
	public static final int FOR = 21;
	public static final int FUNCTION = 22;
	public static final int GOTO = 23;
	public static final int IF = 24;
	public static final int IMPORT = 25;
	public static final int IMPLEMENTATION = 26;
	public static final int INHERITED = 27;
	public static final int IN = 28;
	public static final int INLINE = 29;
	public static final int INTERFACE = 30;
	public static final int IS = 31;
	public static final int LABEL = 32;
	public static final int MOD = 33;
	public static final int MODULE = 34;
	public static final int NIL = 35;
	public static final int NOT = 36;
	public static final int OBJECT = 37;
	public static final int OF = 38;
	public static final int ONLY = 39;
	public static final int OPERATOR = 40;
	public static final int OR = 41;
	public static final int OR_ELSE = 42;
	public static final int OTHERWISE = 43;
	public static final int PACKED = 44;
	public static final int POW = 45;
	public static final int PROCEDURE = 46;
	public static final int PROGRAM = 47;
	public static final int PROPERTY = 48;
	public static final int PROTECTED = 49;
	public static final int QUALIFIED = 50;
	public static final int RECORD = 51;
	public static final int REPEAT = 52;
	public static final int RESTRICTED = 53;
	public static final int SET = 54;
	public static final int SHL = 55;
	public static final int SHR = 56;
	public static final int THEN = 57;
	public static final int TO = 58;
	public static final int TYPE = 59;
	public static final int UNIT = 60;
	public static final int UNTIL = 61;
	public static final int USES = 62;
	public static final int VALUE = 63;
	public static final int VAR = 64;
	public static final int VIEW = 65;
	public static final int VIRTUAL = 66;
	public static final int WHILE = 67;
	public static final int WITH = 68;
	public static final int XOR = 69;
	public static final int TK_ID = 70;
	public static final int TK_INTLIT = 71;
	public static final int TK_REALLIT = 72;
	public static final int TK_STRLIT = 73;
	public static final int TK_COMMENT = 74;
	public static final int OP_ADD = 75;
	public static final int OP_SUB = 76;
	public static final int OP_DIV = 77;
	public static final int OP_MUL = 78;
	public static final int OP_BIT_NOT = 79;
	public static final int OP_BIT_AND = 80;
	public static final int OP_BIT_OR = 81;
	public static final int OP_BIT_OR_EXCLAMATION = 82;
	public static final int OP_LESS_THAN = 83;
	public static final int OP_GREATER_THAN = 84;
	public static final int OP_NOT_EQ = 85;
	public static final int OP_LESS_THAN_EQ = 86;
	public static final int OP_GREATER_THAN_EQ = 87;
	public static final int OP_EQ = 88;
	public static final int OP_ASSIGN = 89;
	public static final int OP_EOL = 90;
	public static final int OP_L_BRACKET = 91;
	public static final int OP_R_BRAKET = 92;
	public static final int COMMA = 93;
	public static final int COLON = 94;
	public static final int INTEGER = 95;
	public static final int REAL = 96;
	public static final int BOOLEAN = 97;
	public static final int BYTE = 98;
	public static final int CHAR = 99;
	public static final int STRING = 100;
	public static final int TRUE = 101;
	public static final int FALSE = 102;
	public static final int L_SQ_BRACE = 103;
	public static final int R_SQ_BRACE = 104;
	public static final int PERIOD = 105;
	public static final int WRITE = 106;
	public static final int WRITELN = 107;
	public static final int OP_MOD = 108;

	public static final int VAR_TOKENS[] = { Token.ARRAY, Token.INTEGER, Token.REAL,
			Token.BOOLEAN, Token.CHAR, Token.STRING};
	
	public static final int COMPARE_TOKENS[] = { 
		OP_BIT_NOT,OP_BIT_AND,OP_BIT_OR,OP_BIT_OR_EXCLAMATION,OP_LESS_THAN,OP_GREATER_THAN,OP_NOT_EQ,
		OP_LESS_THAN_EQ,OP_GREATER_THAN_EQ,OP_EQ
	};

	private int token_types_int_array[] = new int[] { ABSOLUTE, ABSTRACT, ALL,
			AND, AND_THEN, ARRAY, ASM, BEGIN, BINDABLE, CASE, CLASS, CONST,
			CONSTRUCTOR, DESTRUCTOR, DIV, DO, DOWNTO, ELSE, END, EXPORT, FILE,
			FOR, FUNCTION, GOTO, IF, IMPORT, IMPLEMENTATION, INHERITED, IN,
			INLINE, INTERFACE, IS, LABEL, MOD, MODULE, NIL, NOT, OBJECT, OF,
			ONLY, OPERATOR, OR, OR_ELSE, OTHERWISE, PACKED, POW, PROCEDURE,
			PROGRAM, PROPERTY, PROTECTED, QUALIFIED, RECORD, REPEAT,
			RESTRICTED, SET, SHL, SHR, THEN, TO, TYPE, UNIT, UNTIL, USES,
			VALUE, VAR, VIEW, VIRTUAL, WHILE, WITH, XOR, TK_ID, TK_INTLIT,
			TK_REALLIT, TK_STRLIT, TK_COMMENT, OP_ADD, OP_SUB, OP_DIV, OP_MUL,
			OP_BIT_NOT, OP_BIT_AND, OP_BIT_OR, OP_BIT_OR_EXCLAMATION,
			OP_LESS_THAN, OP_GREATER_THAN, OP_NOT_EQ, OP_LESS_THAN_EQ,
			OP_GREATER_THAN_EQ, OP_EQ, OP_ASSIGN, OP_EOL, OP_L_BRACKET,
			OP_R_BRAKET, COMMA, COLON, INTEGER, REAL, BOOLEAN, BYTE, CHAR,
			STRING, TRUE, FALSE,L_SQ_BRACE,R_SQ_BRACE, PERIOD, WRITE, WRITELN, OP_MOD};

	private static 	String token_types_array[] = { "absolute", "abstract", "all",
			"and", "and_then", "array", "asm", "begin", "bindable", "case",
			"class", "const", "constructor", "destructor", "div", "do",
			"downto", "else", "end", "export", "file", "for", "function",
			"goto", "if", "import", "implementation", "inherited", "in",
			"inline", "interface", "is", "label", "mod", "module", "nil",
			"not", "object", "of", "only", "operator", "or", "or_else",
			"otherwise", "packed", "pow", "procedure", "program", "property",
			"protected", "qualified", "record", "repeat", "restricted", "set",
			"shl", "shr", "then", "to", "type", "unit", "until", "uses",
			"value", "var", "view", "virtual", "while", "with", "xor", "TK_ID",
			"TK_INTLIT", "TK_REALLIT", "TK_STRLIT", "TK_COMMENT", "+", "-",
			"/", "*", "~", "&", "|", "!", "<", ">", "<>", "<=", ">=", "=",
			":=", ";", "(", ")", ",", ":", "integer", "real", "boolean",
			"byte", "char", "string", "true", "false","[", "]",".", "write", "writeln", "%" };

	private static Map<String, Integer> TOKEN_TYPES = new HashMap<String, Integer>();
	{
		for (int i = 0; i < token_types_array.length; i++) {
			Token.TOKEN_TYPES.put(token_types_array[i],
					token_types_int_array[i]);
		}
	}

	public Token() {

	}

	public Token(String token) {
		this.token = Token.getTokenTypeID(token);
	}

	public String toString() {
		return "Token " + Token.token_types_array[this.token] + "  |  Value "
				+ this.value + "\n";
	}

	public static int getTokenTypeID(String token_type) {
		if (Token.TOKEN_TYPES.containsKey(token_type)) {
			return Token.TOKEN_TYPES.get(token_type);
		}
		/*
		 * for (int i = 0; i < token_types_array.length; i++) { if
		 * (token_type.equals(this.token_types_array[i])) return i; }
		 */
		return 0;
	}

	public static String getTokenTypeString(int token_type) {
		return Token.token_types_array[token_type];
	}

	public int getToken() {
		return token;
	}

	public void setToken(int token) {
		this.token = token;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isTokenType(String token_type_string) {
		int token_type_int = Token.getTokenTypeID(token_type_string);
		return (token_type_int == this.getToken()) ? true : false;
	}

	public boolean equals(Token t) {
		if (t == null)
			return false;
		boolean ret_val = false;
		if (this.token == t.token) {
			if (this.value == t.value) {
				ret_val = true;
			}
		}
		return ret_val;
	}

	public boolean isTokenType(int token_type) {
		return (token_type == this.getToken()) ? true : false;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getCol() {
		return col;
	}

	public void setCol(int col) {
		this.col = col;
	}

	public boolean isVarToken() {
		for (int i = 0; i < (Token.VAR_TOKENS.length); i++) {
			if (this.token == Token.VAR_TOKENS[i])
				return true;
		}
		return false;
	}

	public boolean isCompareToken() {
		for (int i = 0; i < (Token.COMPARE_TOKENS.length); i++) {
			if (this.token == Token.COMPARE_TOKENS[i])
				return true;
		}
		return false;
	}
}
