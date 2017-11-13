import java.util.ArrayList;

/**
 * Class used to identify each symbol
 * @author Chandrashekar Singh.
 *
 */
public class SymbolTableItem {
	public static final int VAR = 1;
	public static final int ARRAY = 2;
	public static final int LABEL = 3;
	public static final int SWITCH_CASE = 4;
	public static final int FUNCTION = 5;

	/**
	 * Identifier or Name of the variable
	 */
	private String i;
	/**
	 * TK_VAR / TK_FUNCTION
	 */
	private int symbol_type;
	/**
	 * Int / float, char, etc
	 */
	private int type;
	/**
	 * Location in memory
	 */
	private int address;
	/**
	 * Size of variable / Array Element
	 */
	private int size;

	/**
	 * Array specific variables
	 */
	private int array_size;
	private int index_type;
	private int lo;
	private int hi;
	/**
	 * Used for labels
	 */
	private boolean seen = false;
	private ArrayList<Integer> goto_address;
	private String block = "";
	private String goto_block = "";

	/**
	 * Default Constructor
	 */
	public SymbolTableItem() {
		this.i = "";
		this.symbol_type = 0;
		this.type = 0;
		this.address = 0;
		this.size = 0;
		this.array_size = 0;
		this.index_type = 0;
		this.lo = 0;
		this.hi = 0;
		this.seen = false;
		this.goto_address = new ArrayList<>();
	}

	/**
	 * Creates a symbol table item. (used to create new variable symbols)
	 * 
	 * @param i
	 *            Name of the variable
	 * @param symbol_type
	 *            TK_VAR, TK_ARRAY or TK_FUNCTION
	 * @param type
	 *            Type of variable or array element (float, char, int)
	 * @param address
	 *            The address the variable will be allocated in memory
	 */
	public SymbolTableItem(String i, int symbol_type, int type, int address,
			int size) {
		this.setI(i);
		this.setSymbol_type(symbol_type);
		this.setType(type);
		this.setAddress(address);
		this.setSize(size);
		this.goto_address = new ArrayList<>();
	}

	/**
	 *	Creates a new symbol table item (Used when creating array symbol) 
	 * @param i
	 *            Name of the variable
	 * @param symbol_type
	 *            TK_VAR, TK_ARRAY or TK_FUNCTION
	 * @param type
	 *            Type of variable or array element (float, char, int)
	 * @param address
	 *            The address the variable will be allocated in memory
	 * @param array_size
	 *            Number of elements in the array
	 * @param index_type
	 *            Char / Int
	 * @param lo
	 *            Smallest index in the array
	 * @param hi
	 *            Largest index in the array
	 */
	public SymbolTableItem(String i, int symbol_type, int type, int address,
			int size, int array_size, int index_type, int lo, int hi) {
		this(i, symbol_type, index_type, address, size);
		this.array_size = array_size;
		this.index_type = index_type;
		this.lo = lo;
		this.hi = hi;
		this.goto_address = new ArrayList<>();
	}

	public String toString() {
		String ret = "Symbol " + i + " - ";
		switch (symbol_type) {
		case VAR:
			ret += "VAR";
			break;
		case ARRAY:
			ret += "Array";
			break;
		default:
			ret += "Function";
		}

		switch (type) {
		case Token.INTEGER:
			ret += " Integer ";
			break;
		case Token.CHAR:
			ret += " Char ";
			break;
		case Token.REAL:
			ret += " Real ";
			break;
		}

		ret += " - " + address;
		if (this.symbol_type == ARRAY) {
			ret += "\nArray Size: " + array_size;
			ret += " - Index Type: ";
			switch (index_type) {
			case Token.INTEGER:
				ret += " Integer ";
				break;
			case Token.CHAR:
				ret += " Char ";
				break;
			default:
				ret += " Unknown ";
			}
			ret += " - Low: " + lo;
			ret += " - Hi: " + hi;
		}
		return ret;
	}

	public int getAddress() {
		return address;
	}

	public void setAddress(int address) {
		this.address = address;
	}

	public int getType() {
		return this.type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public int getSymbol_type() {
		return symbol_type;
	}

	public void setSymbol_type(int symbol_type) {
		this.symbol_type = symbol_type;
	}

	public String getI() {
		return i;
	}

	public void setI(String i) {
		this.i = i;
	}

	public int getArray_size() {
		return array_size;
	}

	public void setArray_size(int array_size) {
		this.array_size = array_size;
	}

	public int getIndex_type() {
		return index_type;
	}

	public void setIndex_type(int index_type) {
		this.index_type = index_type;
	}

	public int getLo() {
		return lo;
	}

	public void setLo(int lo) {
		this.lo = lo;
	}

	public int getHi() {
		return hi;
	}

	public void setHi(int hi) {
		this.hi = hi;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int size) {
		this.size = size;
	}

	public boolean getSeen() {
		return seen;
	}

	public void setSeen(boolean seen) {
		this.seen = seen;
	}

	public ArrayList<Integer> getGoto_address() {
		return goto_address;
	}

	public void setGoto_address(int goto_address) {
		this.goto_address.add(goto_address);
	}

	public String getBlock() {
		return block;
	}

	public void setBlock(String block) {
		this.block = block;
	}

	public String getGoto_block() {
		return goto_block;
	}

	public void setGoto_block(String goto_block) {
		this.goto_block = goto_block;
	}

}
