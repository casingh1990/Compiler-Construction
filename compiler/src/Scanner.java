import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Scanner Class used to tokenize the source file
 * @author Chandrashekar Singh
 * 
 */

public class Scanner {
	private Source source;

	private final int NOT_FOUND = 0;
	private final int LETTER = 1;
	private final int DIGIT = 2;
	private final int LITERAL = 3;
	private final int OPERATOR = 4;
	private final int COMMENT = 5;
	private final int INCLUDE = 6;

	private ArrayList<Token> tokens;

	public ArrayList<Token> getTokens() {
		return tokens;
	}

	public void setTokens(ArrayList<Token> tokens) {
		this.tokens = tokens;
	}

	private String keywords_array[] = { "absolute", "abstract", "all", "and",
			"and_then", "array", "asm", "begin", "bindable", "case", "class",
			"const", "constructor", "destructor", "div", "do", "downto",
			"else", "end", "export", "file", "for", "function", "goto", "if",
			"import", "implementation", "inherited", "in", "inline",
			"interface", "is", "label", "mod", "module", "nil", "not",
			"object", "of", "only", "operator", "or", "or_else", "otherwise",
			"packed", "pow", "procedure", "program", "property", "protected",
			"qualified", "record", "repeat", "restricted", "set", "shl", "shr",
			"then", "to", "type", "unit", "until", "uses", "value", "var",
			"view", "virtual", "while", "with", "xor", "integer", "real",
			"boolean", "byte", "char", "string", "true", "false", "write", "writeln" };
	private String operators_array[] = { "+", "-", "/", "*", "~", "&", "|",
			"!", "<", ">", "<>", "<=", ">=", "=", ":=", ";", "(", ")", ":",
			",", "[", "]", "." };

	private Map<String, Integer> KEYWORDS;

	public Scanner(String filename) {
		this.source = new Source(filename);

		this.tokens = new ArrayList<Token>();

		this.KEYWORDS = new HashMap<String, Integer>();
		for (int i = 0; i < keywords_array.length; i++) {
			this.KEYWORDS.put(keywords_array[i], i);
		}
	}

	public byte getSourceChar() {
		byte ret = this.source.getSourceChar();
		return ret;
	}

	public void findTokens() {
		Token token;
		boolean not_at_end = true;
		while ((!this.source.EOF()) && (not_at_end)) {
			byte cur = this.getSourceChar();
			if ((cur != 32) && (cur != 10) && (cur != 13)) {

				if (this.isInclude()) {
					this.tokens.addAll(processIncludes());
				}

				token = gettoken(); // scanner
				if (token != null) {
					this.tokens.add(token);
					token.setCol(this.source.getCurCol());
					token.setLine(this.source.getCurline());

					if ((token.isTokenType(Token.END))
							&& (this.getSourceChar() == 46)) {
						not_at_end = false;
					}
				}
			} else {
				this.source.advance();
			}

		}
	}

	public Token gettoken() {

		// restart:

		switch (catchcode(this.getSourceChar())) {

		case LETTER:
			return getLetter();
		case COMMENT:
			return getComment();
		case OPERATOR:
			return getOperator();
		case LITERAL:
			return getLiteral();
		case DIGIT:
			return getDigit();
		default:
			this.source.advance();
			break;

		}
		//debug("null char is " + (char) this.getSourceChar());
		return null;
	}

	/**
	 * Gets the string literal that begins at current location
	 * 
	 * @return Token
	 */
	public Token getLiteral() {
		Token cur_token = new Token();
		String curname = "";
		curname = this.source.getStringWithBound("'");
		cur_token.setToken(Token.getTokenTypeID("TK_STRLIT"));
		cur_token.setValue(curname);
		return cur_token;
	}

	public Token getDigit() {
		String curname = "";
		Token cur_token = new Token();
		/**
		 * Extracting all letters and digits to curname
		 */
		String type = "TK_INTLIT";
		while ((Character.isDigit((char) this.getSourceChar()))
				|| (this.getSourceChar() == 46)) {
			if (this.getSourceChar() == 46) {
				this.source.advance();
				if (Character.isDigit((char) this.getSourceChar())) {
					type = "TK_REALLIT";
				} else {
					this.source.retreat();
					break;
				}
			}
			curname += (char) this.getSourceChar();
			this.source.advance();
		}

		cur_token.setToken(Token.getTokenTypeID(type));
		cur_token.setValue(curname);

		return cur_token;
	}

	public Token getComment() {
		String curname = "";
		Token cur_token = new Token();

		if (this.source.notAtBound("(*")) {
			this.source.advance();
			curname = this.recursiveComment("}");
		} else {
			this.source.advance();
			curname = this.recursiveComment("*)");
			this.source.advance();
		}
		cur_token.setValue(curname);
		cur_token.setToken(Token.getTokenTypeID("TK_COMMENT"));
		return cur_token;
	}

	private String recursiveComment(String end) {
		String str = "";

		while (this.source.notAtBound(end)) {
			/**
			 * In this case we need to recursively go through the comments
			 */
			if (!this.source.notAtBound("{")) {
				this.source.advance();
				str += this.recursiveComment("}");
			} else if (!this.source.notAtBound("(*")) {
				this.source.advance();
				str += this.recursiveComment("*)");
				this.source.advance();
			} else {
				/**
				 * In this case we process the current comment to return through
				 * str
				 */
				str += (char) this.source.getSourceChar();
			}
			this.source.advance();
		}
		this.source.advance();
		return str;
	}

	/**
	 * Returns operator token. Also checks for Comment token.
	 * 
	 * @return Token
	 */
	public Token getOperator() {
		String curname = "";
		Token cur_token = new Token();
		/**
		 * Extracting all letters and digits to curname
		 */
		switch (this.getSourceChar()) {
		case 46:
			curname = ".";
			this.source.advance();
			break;
		case 40:
			curname = "(";
			this.source.advance();
			break;
		case 41:
			curname = ")";
			this.source.advance();
			break;
		case 91:
			curname = "[";
			this.source.advance();
			break;
		case 93:
			curname = "]";
			this.source.advance();
			break;
		default:
			while (this.isOperator(this.getSourceChar())) {
				curname += (char) this.getSourceChar();
				this.source.advance();
			}
		}
		for (int i = 0; i < this.operators_array.length; i++) {
			if (curname.equals(this.operators_array[i])) {

				cur_token.setToken(Token.getTokenTypeID(curname));
				return cur_token;
			}
		}

		/**
		 * This would be an error case. TK_NOTFOUND
		 */
		return null;
	}

	public void ProcessIncludes() {

	}

	public Token getLetter() {
		String curname = "";
		Token cur_token = new Token();
		/**
		 * Extracting all letters and digits to curname
		 */
		// debug("-----------------------------------------------------------");

		while ((this.isLetter((char) this.getSourceChar()))
				|| (Character.isDigit((char) this.getSourceChar()))) {
			curname += (char) this.getSourceChar();
			this.source.advance();
		}

		if (this.KEYWORDS.containsKey(curname)) {
			cur_token.setToken(Token.getTokenTypeID(curname));
		} else {
			cur_token.setToken(Token.getTokenTypeID("TK_ID"));
			cur_token.setValue(curname);
		}

		return cur_token;
	}

	private int catchcode(byte cur) {
		char cur_char = (char) cur;

		if (this.isLetter(cur_char))
			return LETTER;
		else if (this.isInclude())
			return INCLUDE;
		else if (this.isComment(cur))
			return COMMENT;
		else if (this.isOperator(cur))
			return OPERATOR;
		else if (this.isLiteral(cur))
			return LITERAL;
		else if (Character.isDigit(cur))
			return DIGIT;
		else {
			return NOT_FOUND;
		}
	}

	private boolean isInclude() {
		/**
		 * Includes are of the form {I filename} {$i filename}
		 */

		if ((!this.source.notAtBound("{$I"))
				|| (!this.source.notAtBound("{$i"))) {
			this.source.advance(); // move past {
			this.source.advance(); // move past $
			this.source.advance(); // move past I / i
			return true;
		} else {
			// debug("not include");
		}
		return false;
	}

	private boolean isLiteral(byte ch) {
		if (ch == 39)
			return true;
		return false;
	}

	private boolean isComment(byte ch) {
		if (ch == 123)
			return true;
		if (ch == 40) {
			this.source.advance();
			if (this.getSourceChar() == 42) {
				this.source.retreat();
				return true;
			}
			this.source.retreat();
		}
		return false;
	}

	private boolean isOperator(byte ch) {

		for (int i = 0; i < operators_array.length; i++) {
			byte c[] = operators_array[i].getBytes();
			for (int j = 0; j < c.length; j++) {
				if (c[j] == ch){
					return true;
				}
			}
		}
		return false;
	}

	private boolean isLetter(char ch) {
		if (Character.isLetter(ch))
			return true;
		if (ch == '_')
			return true;
		return false;
	}

	public ArrayList<Token> processIncludes() {
		String new_file_name = "";
		while (this.source.notAtBound("}")) {
			new_file_name += (char) this.source.getSourceChar();
			this.source.advance();
		}
		this.source.advance();
		Scanner new_file_search = new Scanner(new_file_name.trim());
		new_file_search.findTokens();
		return new_file_search.getTokens();
	}

	public void printtoken(Token token) {
		System.out.println(token
				+ "\n-------------------------------------------------");
	}

}
