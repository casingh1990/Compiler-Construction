import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Main Parser Class
 * @author Chandrashekar Singh
 *
 */
public class Parser {

	private final int OP_PUSH = 1;
	private final int OP_POP = 2;
	// private final int OP_POP1 = 3; //not used at the moment (For optimisation purposes)
	// private final int OP_POPS = 4; //not used at the moment (For String pop)

	private final int OP_PUSHI = 5;
	private final int OP_PUSHF = 6;
	// private final int OP_PUSH1 = 7; //not used at the moment (For optimisation purposes)
	// private final int OP_PUSHS = 8; //not used at the moment (For String Push
	
	private final int OP_PUSHSC = 9;
	private final int OP_XCH1 = 10;
	private final int OP_XCH2 = 11;
	private final int OP_CSTR = 12;
	private final int OP_GET = 13;
	private final int OP_PUT = 14;
	private final int OP_BOUND = 15;

	private final int OP_ADD = 50;
	private final int OP_SUB = 51;
	private final int OP_DIV = 52;
	private final int OP_MUL = 53;
	private final int OP_ADDF = 54;
	private final int OP_SUBF = 55;
	private final int OP_DIVF = 56;
	private final int OP_MULF = 57;
	private final int OP_INCR = 58;
	private final int OP_MOD = 59;
	private final int OP_MODF = 60;

	private final int OP_EQ = 20;
	private final int OP_LT = 21;
	private final int OP_LTEQ = 22;
	private final int OP_MTEQ = 23;
	private final int OP_MT = 24;
	private final int OP_NOTEQ = 25;

	private final int OP_JUMP = 41;
	private final int OP_JUMPF = 42;
	//private final int OP_JUMPT = 43; //not used
	private final int OP_JTAB = 44;

	private final int OP_WRITELN = 80;
	private final int OP_WRITE = 81;

	private final int STOP = 99;

	private ArrayList<Token> tokens;
	private int token_pointer;
	private ArrayList<Byte> parsed;
	private ArrayList<Integer> prev_operand_type;

	private SymbolTable symbolTable;
	private int dp = 0;
	
	private Block block;

	public Parser(ArrayList<Token> tokens_to_parse) {
		this.tokens = tokens_to_parse;
		this.token_pointer = 0;
		this.parsed = new ArrayList<>();
		this.symbolTable = new SymbolTable();
		this.prev_operand_type = new ArrayList<>();
		this.block = new Block();
	}

	public void parse() {

		Token cur = this.curToken();
		
		if (cur.isTokenType(Token.PROGRAM)){
			//If this is the program name just ignore it for now
			this.match("program");
			this.gettoken();
			cur = this.curToken();
		}
		
		if (cur.isTokenType(Token.VAR)) {
			this.gettoken();
			this.parseDeclaration();
		}

		cur = this.curToken();
		if (cur.isTokenType(Token.LABEL)) {
			this.parseLabel();
		}
		cur = this.curToken();
		do {
			parseStatement(cur);
			cur = this.curToken();
		} while (!cur.isTokenType(Token.END));
		// adding the stop instruction
		this.parsed.add(new Byte((byte) this.STOP));
		this.fixGoto();
		// this.printParsed();
	}

	public void parseStatement(Token cur) {
		switch (cur.getToken()) {
		case Token.VAR:
		case Token.LABEL:
			this.error(Token.getTokenTypeString(cur.getToken())
					+ " is not allowed at this point in the source");
			break;
		case Token.TK_ID:
			this.parseID();
			break;
		case Token.OP_L_BRACKET:
			System.out.println("Brace is here\n");
			break;
		case Token.IF:
			this.parseIF();
			break;
		case Token.REPEAT:
			this.parseRepeat();
			this.match(";");
			break;
		case Token.WHILE:
			this.parseWhile();
			break;
		case Token.FOR:
			this.parseFor();
			break;
		case Token.BEGIN:
			this.parseBegin();
			break;
		case Token.CASE:
			this.parseCase();
			break;
		case Token.GOTO:
			this.parseGoto();
			break;

		case Token.WRITE:
		case Token.WRITELN:
			this.parseWrite();
			break;

		default:
			System.out
					.println("!!!!!!!!!!!!!!!!!!!!!WARNING!!!!!!!!!!!!!!!!!!\n "
							+ cur + " is not recognised");
			System.out
					.println("!!!!!!!!!!!!!!!!!!!!!WARNING!!!!!!!!!!!!!!!!!!\n ");
			this.gettoken();

		}
	}

	public void parseCase() {
		this.block.startBlock();
		this.match("case");
		//parsing case condition
		this.T();
		//check to make sure that case is of the right type (char or int)
		int prev_type = this.prev_operand_type.get(this.prev_operand_type
				.size() - 1);
		if ((prev_type != Token.INTEGER) && (prev_type != Token.CHAR)) {
			this.error("Case type must be char or integer");
		}
		gen1(OP_JUMP);
		int jump_to_decide = this.parsed.size();
		gen4(0);

		this.match("of");
		Token cur = this.curToken();
		ArrayList<String> cases = new ArrayList<>();
		ArrayList<Integer> case_addresses = new ArrayList<>();
		ArrayList<Integer> case_ends = new ArrayList<>();

		//parse each possible case
		String cur_case = "";
		do {
			this.block.startBlock();
			cur_case = cur.getValue();
			cases.add(cur_case);
			case_addresses.add(this.parsed.size());
			this.gettoken();
			this.match(":");

			this.parseStatement(this.curToken());

			gen1(this.OP_JUMP);
			case_ends.add(this.parsed.size());
			gen4(0);

			this.match(";");
			cur = this.curToken();
			this.block.endBlock();
		} while (!cur.isTokenType(Token.END));
		this.match("end");
		this.match(";");

		// fill in jump to decide
		gen4(this.parsed.size(), jump_to_decide);

		gen1(OP_JTAB);
		gen4(cases.size());
		for (int i = 0; i < cases.size(); i++) {
			gen4(cases.get(i));
			gen4(case_addresses.get(i));
		}

		// fill in the end of each case jump to address
		Iterator<Integer> case_ends_iter = case_ends.iterator();
		while (case_ends_iter.hasNext()) {
			int end_case_addr = case_ends_iter.next();
			this.gen4(this.parsed.size(), end_case_addr);
		}
		this.block.endBlock();
	}

	public void parseWrite() {
		Token cur = this.curToken();
		boolean newln = (cur.isTokenType(Token.WRITELN)) ? true : false;
		this.match((newln) ? "writeln" : "write");
		this.match("(");
		this.L();
		this.match(")");
		this.match(";");
		int prev_type = this.prev_operand_type.get(this.prev_operand_type
				.size() - 1);

		if (prev_type != Token.STRING) {
			switch (prev_type) {
			case Token.BOOLEAN:
			case Token.INTEGER:
				this.gen1(this.OP_XCH1);
			case Token.REAL:
				this.gen1(OP_CSTR);
				break;
			}
		}

		this.gen1((newln) ? this.OP_WRITELN : this.OP_WRITE);
	}

	public void parseGoto() {
		this.match("goto");
		Token cur = this.curToken();
		this.gettoken(); //get the next token after the label ID.

		SymbolTableItem symbol = this.symbolTable.getSymbolByValue(cur
				.getValue());
		gen1(this.OP_JUMP);
		symbol.setGoto_address(this.parsed.size());
		symbol.setGoto_block(this.block.curBlock());
		if (symbol.getSeen()) {
			gen4(symbol.getAddress());
		} else {
			gen4(0);
		}

		this.match(";");
	}

	public void parseLabel() {
		this.gettoken();
		Token cur = this.curToken();
		ArrayList<String> labels = new ArrayList<>();
		while (!cur.isTokenType(Token.OP_EOL)) {
			if (cur.isTokenType(Token.TK_ID)) {
				if (!labels.contains(cur.getValue())) {
					labels.add(cur.getValue());
				}
			}
			this.gettoken();
			cur = this.curToken();
		}
		Iterator<String> lblitr = labels.iterator();
		while (lblitr.hasNext()) {
			String cur_lbl = lblitr.next();
			if (this.symbolTable.containsSymbol(cur_lbl)) {
				this.error("labels and variables must be unique");
			} else {
				SymbolTableItem new_symbol = new SymbolTableItem(cur_lbl,
						SymbolTableItem.LABEL, 0, 0, 0);
				new_symbol.setSeen(false);
				this.symbolTable.add(new_symbol);
			}
		}
		this.match(";");
	}

	public void parseIF() {
		this.block.startBlock();
		boolean isElse = false;
		int hole2 = 0;
		this.match("if");
		this.L();
		this.match("then");
		this.gen1(this.OP_JUMPF);
		int hole = this.parsed.size();
		this.gen4("0");

		this.parseStatement(this.curToken());

		this.curToken();
		if (this.curToken().equals(new Token("else"))) {
			isElse = true;
			this.gen1(this.OP_JUMP);
			hole2 = this.parsed.size();
			this.gen4("0");
		}
		Integer save_ip = (this.parsed.size());
		gen4(save_ip.toString(), hole);
		if (isElse) {
			this.parseStatement(this.curToken());
			Integer save_ip2 = (this.parsed.size());
			gen4(save_ip2.toString(), hole2);
		}
		this.block.startBlock();
	}

	/*
	 * 
	 * do {
	 * 
	 * 
	 * }while();
	 */

	public void parseRepeat() {
		this.block.startBlock();
		this.match("repeat");
		Integer target = new Integer(this.parsed.size());

		this.parseStatement(this.curToken());
		this.match(";");
		this.match("until");

		this.L();
		this.gen1(this.OP_JUMPF);
		this.gen4(target.toString());
		this.block.endBlock();
	}

	public void parseFor() {
		this.block.startBlock();
		// Parse the for statement. Initialize var.
		this.match("for");
		Token var = this.curToken();
		this.parseID();
		SymbolTableItem varSym = this.symbolTable.getSymbolByValue(var
				.getValue());
		this.match("to");
		Token toToken = this.curToken();
		this.gettoken();
		this.match("do");

		// Set start of for loop.
		Integer target = new Integer(this.parsed.size());
		// Conditional check
		this.gen1(this.OP_PUSH);
		this.gen4(new Integer(varSym.getAddress()).toString());
		this.parseNumLit(Token.INTEGER, this.OP_PUSHI, toToken);
		this.gen1(this.OP_LTEQ);

		// exit loop on false
		this.gen1(this.OP_JUMPF);
		int hole = this.parsed.size();
		this.gen4("0");

		// Execute body of the loop
		this.parseStatement(this.curToken());
		this.match(";");

		// Increment and jump back to the start of the loop
		this.gen1(this.OP_PUSH);
		this.gen4(new Integer(varSym.getAddress()).toString());
		this.gen1(this.OP_INCR);
		this.gen1(OP_POP);
		this.gen4(new Integer(varSym.getAddress()).toString());
		gen1(this.OP_JUMP);
		gen4(target.toString());

		// fill in the address of the end of the loop using the space left @hole
		Integer save_ip = (this.parsed.size());
		gen4(save_ip.toString(), hole);
		this.block.endBlock();
	}

	public void parseWhile() {
		this.block.startBlock();
		this.match("while");
		Integer target = new Integer(this.parsed.size());
		this.L();											//Parse while condition
		this.gen1(this.OP_JUMPF);
		int hole = this.parsed.size();
		this.gen4("0");
		this.match("do");
		this.parseStatement(this.curToken());
		gen1(this.OP_JUMP);
		gen4(target.toString());
		Integer save_ip = (this.parsed.size());
		gen4(save_ip.toString(), hole);
		this.block.endBlock();;
	}

	/**
	 * Parses a begin ... end block.
	 */
	public void parseBegin() {
		this.block.startBlock();
		this.match("begin");
		Token cur = this.curToken();
		do {
			this.parseStatement(cur);
			cur = this.curToken();
		} while (!cur.isTokenType(Token.END));
		this.match("end");
		this.block.endBlock();
	}

	/**
	 * Goes through the symbol table and attempt to fix the goto addresses
	 */
	private void fixGoto() {
		Iterator<SymbolTableItem> itr = this.symbolTable.iterator();
		while (itr.hasNext()) {
			SymbolTableItem cur = itr.next();
			if (cur.getSymbol_type() == SymbolTableItem.LABEL) {				//Making sure this is a label
				Iterator<Integer> gotos = cur.getGoto_address().iterator();
				while (gotos.hasNext()) {								//Only processes those labels that have a goto statement
					int curGotoAddr = gotos.next();
					if (cur.getSeen() == false) {								//If goto exist but lable is not used report error
						this.error("label " + cur.getI() + " not found ");
					} else {
						//Making sure that the goto block matches the label block
						if (this.isLegalGoto(cur)){
							this.gen4(cur.getAddress(), curGotoAddr);
						}
						else{
							this.error("Illegal goto " + cur.getI());
						}
					}
				}
			}
		}
	}
	
	
	/**
	 * Checks if the current goto Operation is legal
	 * @param gotoSymbol
	 * @return 
	 */
	private boolean isLegalGoto(SymbolTableItem gotoSymbol){
		String from = gotoSymbol.getGoto_block();
		String to = gotoSymbol.getBlock();
		if (from.equals(to)){
			return true;
		}
		else if (to.length() > from.length()){
			return false;
		}
		else{
			for (int i=0; i<to.length(); i++){
				System.out.println(to.charAt(i) + " -> " + from.charAt(i));
				if (to.charAt(i) > from.charAt(i)){
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Parses ID that appears on the left hand side
	 */
	public void parseID() {
		Token cur = this.curToken();
		SymbolTableItem symbol = this.symbolTable.getSymbolByValue(cur
				.getValue());
		if (symbol == null) {
			this.error("Undefined Variable " + cur.getValue());
			this.gettoken();
		} else {
			switch (symbol.getSymbol_type()) {
			case SymbolTableItem.VAR:
			case SymbolTableItem.ARRAY:
				this.gettoken();
				this.parseAssignment(symbol);
				this.match(";");
				break;
			case SymbolTableItem.LABEL:
				this.gettoken();
				symbol.setAddress(this.parsed.size());
				symbol.setSeen(true);
				symbol.setBlock(this.block.curBlock());
				this.symbolTable.setSymbolTableByValue(symbol.getI(), symbol);
				this.match(":");
				break;
			default:
				this.error("Unrecognised symbol " + symbol.toString());
			}
		}
	}

	/**
	 * Parses ID that appears on the right hand side of assignment.
	 */
	public void parseIDrhs() {
		Token cur = this.curToken();
		SymbolTableItem symbol = this.symbolTable.getSymbolByValue(cur
				.getValue());
		if (symbol == null) {
			this.error("Undefined Variable " + cur.getValue());
			this.gettoken();
		} else {
			if ((symbol.getSymbol_type() == SymbolTableItem.VAR)
					|| (symbol.getSymbol_type() == SymbolTableItem.ARRAY)) {
				this.gettoken();
				if ((symbol.getSymbol_type() == SymbolTableItem.ARRAY)) {
					this.match("[");
					this.L();
					this.match("]");
					int prev_type = this.prev_operand_type
							.get(this.prev_operand_type.size() - 1);
					if (prev_type != symbol.getType()) {
						this.error("Wrong type " + prev_type + " != "
								+ symbol.getType());
					} else {
						if (symbol.getLo() != 0) {
							gen1(this.OP_PUSHI);
							gen4(symbol.getLo());
							gen1(this.OP_SUB);
						}
						gen1(this.OP_PUSHI);
						gen4(size(symbol.getType()));
						gen1(this.OP_MUL);
						gen1(this.OP_PUSHI);
						gen4(symbol.getAddress());
						gen1(this.OP_ADD);
						gen1(this.OP_GET);
					}
				} else {
					this.prev_operand_type.add(symbol.getType());
					this.gen1(this.OP_PUSH);
					this.gen4(symbol.getAddress());
				}
			}
		}
	}

	public void parseAssignment(SymbolTableItem symbol) {
		if ((symbol.getSymbol_type() == SymbolTableItem.ARRAY)) {
			this.match("[");
			this.L();
			this.match("]");
			int index_type = this.prev_operand_type.get(this.prev_operand_type
					.size() - 1);
			// Check that element type is the correct type
			if (index_type != symbol.getIndex_type()) {
				this.error(index_type + " != " + symbol.getIndex_type());
			}
			// Check for bound
			Token line_token = this.curToken();
			gen1(OP_BOUND);
			gen4(symbol.getLo());
			gen4(symbol.getHi());
			gen4(line_token.getLine());
			// Low offset if lo is not zero
			if (symbol.getLo() != 0) {
				gen1(OP_PUSHI);
				gen4(symbol.getLo());
				gen1(OP_SUB);
			}
			// Multiply by array element size to get right offset
			gen1(OP_PUSHI);
			gen4(symbol.getSize());
			gen1(OP_MUL);
			// Calculate the actual address of the element
			gen1(OP_PUSHI);
			gen4(symbol.getAddress());
			gen1(OP_ADD);
		}
		this.gettoken();
		this.L(); // Find RHS of assignment
		int rhs = this.prev_operand_type.get(this.prev_operand_type.size() - 1);
		if (isLegalAssignment(symbol.getType(), rhs)) {
			if ((symbol.getSymbol_type() == SymbolTableItem.ARRAY)) {
				this.gen1(this.OP_PUT);
			} else {
				gen1(this.OP_POP);
				gen4(symbol.getAddress());
			}
		} else {
			this.error("Wrong Asignment Types "
					+ Token.getTokenTypeString(symbol.getType()) + " "
					+ Token.getTokenTypeString(rhs));
		}
	}

	public boolean isLegalAssignment(int lhs, int rhs) {

		if (lhs == Token.INTEGER) {
			if (rhs == Token.INTEGER)
				return true;
			else
				return false;
		}
		if (lhs == Token.REAL) {
			if ((rhs == Token.REAL) || (rhs == Token.INTEGER))
				return true;
			else
				return false;
		}
		if (lhs == Token.BOOLEAN) {
			if (rhs == Token.BOOLEAN)
				return true;
			else
				return false;
		}
		if ((lhs == Token.CHAR) && (rhs == Token.CHAR)){
			return true;
		}
		return false;
	}
	/**
	 * Parses all declarations
	 */
	public void parseDeclaration() {
		do {
			/**
			 * Get the list of all variables declarated in this line
			 */
			ArrayList<String> curList = this.getCurVarList();
			/**
			 * Since there are no more items in the list, we should find a comma
			 */
			this.match(":");
			/**
			 * Now we need to find out the type
			 */
			this.allocate(curList);
			this.match(";");
		} while (this.isDeclaration());
		// this.gettoken();
	}

	/**
	 * Used to check if the current line is a declaration or not.
	 * @return Boolean 
	 */
	private boolean isDeclaration() {
		int cur_tk_ptr = this.token_pointer;
		Token cur = this.curToken();
		do {
			this.gettoken();
			cur = this.curToken();
			if (cur.isTokenType(Token.COMMA)) {
				this.gettoken();
			}
			cur = this.curToken();
		} while ((!cur.isTokenType(Token.COLON))
				&& (!cur.isTokenType(Token.OP_EOL)));

		if (cur.isTokenType(Token.COLON)) {
			this.gettoken();
			cur = this.curToken();
			if (cur.isVarToken()) {
				this.token_pointer = cur_tk_ptr;
				return true;
			}
		}
		this.token_pointer = cur_tk_ptr;
		return false;
	}

	/**
	 * Returns the size of the given type.
	 * @param token_type (Must be one of Token.INTEGER, Token.REAl, Token.CHAR, Token.BOOLEAN, or Token.STRING
	 * @return
	 */
	private int size(int token_type) {
		switch (token_type) {
		case Token.INTEGER:
		case Token.REAL:
		case Token.CHAR:
		case Token.BOOLEAN:
			return 4;
		case Token.STRING:
			return this.curToken().getValue().length();
		default:
			this.error("Unknown Type");
			break;
		}
		return 4;
	}

	/**
	 * Allocate the required space in memory for each variable
	 * @param curList
	 */
	private void allocate(ArrayList<String> curList) {
		SymbolTableItem new_var;
		int size, hi = 0, lo = 0, index_type = Token.INTEGER, array_size = 0;
		Token var_type = this.curToken();
		if (var_type.isTokenType(Token.ARRAY)) {
			this.gettoken();
			this.match("[");
			Token tk_lo = this.curToken();
			this.gettoken();
			this.match(".");
			this.match(".");
			Token tk_hi = this.curToken();
			this.gettoken();
			this.match("]");
			try {
				lo = Integer.parseInt(tk_lo.getValue());
				hi = Integer.parseInt(tk_hi.getValue());
			} catch (Exception e) {
				lo = (int) tk_lo.getValue().charAt(0);
				hi = (int) tk_hi.getValue().charAt(0);
				index_type = Token.CHAR;
			}
			this.match("of");
			size = this.size(this.curToken().getToken());
			array_size = size * ((hi + 1) - lo);
		} else {
			size = this.size(var_type.getToken());
		}
		Iterator<String> curListIter = curList.iterator();
		while (curListIter.hasNext()) {
			String cur = curListIter.next();
			if (var_type.isTokenType(Token.ARRAY)) {
				new_var = new SymbolTableItem(cur, SymbolTableItem.ARRAY,
						var_type.getToken(), this.dp, size, array_size,
						index_type, lo, hi);
				size = array_size;
			} else {
				new_var = new SymbolTableItem(cur, SymbolTableItem.VAR,
						var_type.getToken(), this.dp, size);
			}
			this.symbolTable.add(new_var);
			this.dp = this.dp + size;
		}
		this.gettoken();
	}

	/**
	 * Gets the list of variables in the current line.
	 * @return list of variables
	 */
	private ArrayList<String> getCurVarList() {
		ArrayList<String> curList = new ArrayList<>();
		Token cur = this.curToken();
		do {
			/**
			 * At this point we should have a TK_ID, else it is a syntax error
			 */
			if (!cur.isTokenType(Token.TK_ID))
				this.error("TK_ID Expected. Found " + cur.toString());

			/**
			 * check if the current token is in the global symbol table
			 */
			if (this.symbolTable.containsSymbol(cur.getValue())) {
				this.error("Previously defined variable " + cur.getValue());
			}

			/**
			 * Check if current list contains current token
			 */
			if (curList.contains(cur.getValue())) {
				this.error("Variable declared twice " + cur.getValue());
			}

			/**
			 * Add item to current list
			 */
			curList.add(cur.getValue());

			this.gettoken();
			cur = this.curToken();
			if (cur.isTokenType(Token.COMMA)) {
				this.gettoken();
			}
			cur = this.curToken();
		} while (!cur.isTokenType(Token.COLON));
		return curList;
	}

	public void printParsed() {
		System.out.println("Parsed ");
		for (int i = 0; i < parsed.size(); i++) {
			System.out.println(parsed.get(i) + " ");
		}
	}

	public Token curToken() {
		//System.out.println("Cur Token\n" + this.tokens.get(token_pointer));
		return tokens.get(token_pointer);
	}

	public void match(String token_str) {
		this.match(new Token(token_str));
	}

	public void match(Token curop) {
		if (!curop.equals(this.curToken())) {
			this.error("Expected " + curop.toString() + " found " + this.curToken().toString());
		}
		this.gettoken();
	}

	public void gen1(int curop) {
		this.parsed.add(new Byte((byte) curop));
	}

	public void gen4(int value) {
		this.gen4(new Integer(value).toString());
	}

	public void gen4(String value) {
		this.gen4(value, -1);
	}

	public void gen4(int value, int position) {
		this.gen4(new Integer(value).toString(), position);
	}

	public void gen4(String value, int position) {
		byte[] num_val = null;
		try {
			num_val = ByteBuffer.allocate(4).putInt(Integer.parseInt(value))
					.array();
		} catch (NumberFormatException ei) {
			try {
				num_val = ByteBuffer.allocate(4)
						.putFloat(Float.parseFloat(value)).array();
			} catch (NumberFormatException ef) {
			}

		}
		if (position < 0) {
			for (byte cur_val : num_val) {
				this.parsed.add(cur_val);
			}
		} else if (position <= (this.parsed.size() - 4)) {
			for (byte cur_val : num_val) {
				this.parsed.set(position++, cur_val);
			}
		} else {
			this.error(position + " is greather than parsed content size. "
					+ this.parsed.size());
		}
	}

	public int getOpType(int curop) {
		switch (curop) {
		case OP_ADD:
			return (this.getArithmeticOperatorType() == Token.INTEGER) ? OP_ADD
					: OP_ADDF;
		case OP_SUB:
			return (this.getArithmeticOperatorType() == Token.INTEGER) ? OP_SUB
					: OP_SUBF;
		case OP_DIV:
			return (this.getArithmeticOperatorType() == Token.INTEGER) ? OP_DIV
					: OP_DIVF;
		case OP_MUL:
			return (this.getArithmeticOperatorType() == Token.INTEGER) ? OP_MUL
					: OP_MULF;
		case OP_MOD:
			return (this.getArithmeticOperatorType() == Token.INTEGER) ? OP_MOD
					: OP_MODF;
		default:
			return 0;
		}
	}

	public int getArithmeticOperatorType() {
		int op1 = prev_operand_type.get(prev_operand_type.size() - 1);
		int op2 = prev_operand_type.get(prev_operand_type.size() - 2);
		if ((op1 == Token.INTEGER) && (op2 == Token.INTEGER)) {
			this.prev_operand_type.add(Token.INTEGER);
			return Token.INTEGER;
		}
		if ((op1 == Token.REAL) && (op2 == Token.REAL)) {
			this.prev_operand_type.add(Token.REAL);
			return Token.REAL;
		}
		if (((op1 == Token.REAL) && (op2 == Token.INTEGER))
				|| ((op1 == Token.INTEGER) && (op2 == Token.REAL))) {

			if (op1 == Token.REAL)
				this.gen1(this.OP_XCH2);
			else
				this.gen1(this.OP_XCH1);

			this.prev_operand_type.add(Token.REAL);
			return Token.REAL;
		}
		this.error("Illegal Type for arithmetic operation ");
		return 0;
	}

	/**
	 * Increments token pointer by one
	 */
	public void gettoken() {
		this.token_pointer++;
	}

	void E() {

		T();
		Token curop = curToken();
		while ((curop.isTokenType(Token.OP_ADD))
				|| (curop.isTokenType(Token.OP_SUB))) {
			match(curop);
			T();
			if (curop.isTokenType(Token.OP_ADD))
				gen1(getOpType(this.OP_ADD));
			else
				gen1(getOpType(this.OP_SUB));
			curop = curToken();
		}
	}

	void T() {
		F();
		Token curop = curToken();
		while (curop.isTokenType(Token.OP_MUL)
				|| curop.isTokenType(Token.OP_DIV)
				|| (curop.isTokenType(Token.MOD))) {
			match(curop);
			F();
			switch (curop.getToken()) {
			case Token.OP_MUL:
				gen1(getOpType(this.OP_MUL));
			case Token.OP_DIV:
				gen1(getOpType(this.OP_DIV));
			case Token.MOD:
			default:
				gen1(getOpType(this.OP_MOD));
			}
			curop = curToken();
		}
	}

	void L() {
		E();
		Token curop = curToken();
		if (curop.isCompareToken()) {
			this.gettoken();
			E();
			this.prev_operand_type.add(Token.BOOLEAN);
			switch (curop.getToken()) {
			case Token.OP_EQ:
				gen1(this.OP_EQ);
				break;
			case Token.OP_LESS_THAN:
				gen1(this.OP_LT);
				break;
			case Token.OP_LESS_THAN_EQ:
				gen1(this.OP_LTEQ);
				break;
			case Token.OP_GREATER_THAN:
				gen1(this.OP_MT);
				break;
			case Token.OP_GREATER_THAN_EQ:
				gen1(this.OP_MTEQ);
				break;
			case Token.OP_NOT_EQ:
				gen1(this.OP_NOTEQ);
				break;
			}
		}
	}

	void F() {
		Token curop = curToken();
		switch (curop.getToken()) {
		case Token.TK_INTLIT:
			// push it
			this.parseNumLit(Token.INTEGER, this.OP_PUSHI, curop);
			gettoken();
			break;
		case Token.TK_REALLIT:
			this.parseNumLit(Token.REAL, this.OP_PUSHF, curop);
			gettoken();
			break;
		case Token.TK_STRLIT:
			this.parseStringLit(curop.getValue());
			gettoken();
			break;
		case Token.TRUE:
		case Token.FALSE:
			this.prev_operand_type.add(Token.BOOLEAN);
			gen1(this.OP_PUSHI);
			gen4((curop.isTokenType(Token.TRUE) ? 1 : 0));
			gettoken();
			break;
		case Token.TK_ID:
			this.parseIDrhs();
			break;
		case Token.OP_L_BRACKET:
			this.match("(");
			this.L();
			this.match(")");
			break;
		default:
			this.error("Expected INT, REAL or Varialble. Found "
					+ curop.toString());
			gettoken();
		}
	}

	private void parseNumLit(int type, int OP, Token token) {
		this.prev_operand_type.add(type);
		gen1(OP);
		gen4(token.getValue());
	}

	private void parseStringLit(String value) {
		this.prev_operand_type.add(Token.STRING);
		byte[] str = value.getBytes();
		int i;
		gen1(this.OP_PUSHSC);
		if (str.length > 255) {
			this.error("Strings should be 255 or less characters long | Length of "
					+ value + " is " + str.length);
		}
		gen1(str.length);
		for (i = 0; i < str.length; i++) {
			gen1(str[i]);
		}
		for (i = str.length; i < 256; i++) {
			gen1(0);
		}
	}

	public ArrayList<Byte> getParsed() {
		return this.parsed;
	}

	public void error() {
		this.error("Unknown Error");
	}
	
	public void error(String error) {
		int line = this.curToken().getLine() + 1;
		int col = this.curToken().getCol();
		this.error(error, line, col);
	}

	public void error(String error, int line, int col) {
		System.out.println("Error has occured near " + line + ":" + col + " | "
				+ error);
		Thread.dumpStack();
		System.exit(1);
	}
}
