import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Class used to implement the stack for the Machine
 * @author Chandrashekar Singh
 *
 */
public class Stack {

	private ArrayList<Byte> stack;
	public int sp; 
	
	public Stack(){
		this.sp = 0;
		stack = new ArrayList<Byte>();
	}
	
	public String toString(){
		Object[] memory_contents = stack.toArray();
		String memory = "";
		for (int i=0; i<memory_contents.length; i++){
			memory += memory_contents[i] + " ";	
		}
		return memory;
	}
	
	public ArrayList<Byte> getStack(){
		return this.stack;
	}
	
	public int popi(){
		byte poped[] = this.popMultiple(4);
		/**
		 * 	int val = 0;
			for (int i=0; i<poped.length; i++){
				val += Math.pow(256, (i)) * (int)poped[i];
			}
		*/
		return ByteBuffer.wrap(poped).getInt();
	}
	
	public void pushi(int value_to_push){
		byte values_to_push[] = ByteBuffer.allocate(4).putInt(value_to_push).array();
		pushMultiple(values_to_push);
	}
	
	public float popf(){
		byte poped[] = this.popMultiple(4);
		return ByteBuffer.wrap(poped).getFloat();
	}
	
	public void pushf(float value_to_push){
		byte val[] = ByteBuffer.allocate(4).putFloat(value_to_push).array();
		this.pushMultiple(val);
	}
	
	public void pushMultiple(byte values_to_push[]){
		for (int i=(values_to_push.length-1); i>=0; i--){
			this.stack.add(values_to_push[i]);
			this.sp++;
		}
	}
	
	public byte[] popMultiple(int num_to_pop){
		byte poped[] = new byte[num_to_pop];
		for (int i=0; i<num_to_pop; i++){
			this.sp--;
			poped[i] = (byte)this.stack.get(this.sp);
			this.stack.remove(this.sp);
		}
		return poped;
	}
	
	public void add(){
		
	}
}
