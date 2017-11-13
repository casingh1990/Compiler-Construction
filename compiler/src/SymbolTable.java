import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class used to store each symbol
 * @author Chandrashekar Singh
 *
 */
public class SymbolTable extends ArrayList<SymbolTableItem> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2427108052657869701L;

	/**
	 * 
	 * @param id
	 * @return
	 */
	public boolean containsSymbol(String id) {
		if (this.getSymbolByValue(id) == null){
			return false;
		}
		return true;
	}
	
	public SymbolTableItem getSymbolByValue(String value){
		Iterator<SymbolTableItem> iter = this.iterator();
		while(iter.hasNext()){
			SymbolTableItem cur = iter.next();
			if (cur.getI().equals(value))
				return cur;
		}
		return null;
	}
	
	/**
	 * 
	 * @param name			The value of the symbol. (Variable, Label, etc name)
	 * @param item_to_set	The SymbolTableItem of the symbol referenced by name;
	 */
	public void setSymbolTableByValue(String name, SymbolTableItem item_to_set){
		Iterator<SymbolTableItem> iter = this.iterator();
		int i =0;
		while(iter.hasNext()){
			SymbolTableItem cur = iter.next();
			if (cur.getI().equals(name)){
				this.set(i, item_to_set);
				break;
			}
			i++;
		}
	}
}
