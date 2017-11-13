import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Generic Memory class that is used for both code and memory storage in machine.
 * @author Chandrashekar Singh
 *
 */
public class Memory {
	private ArrayList<Byte> memory;
	public int ip; 
	/**
	 * Int size in bytes
	 */
	public int int_size = 4;
	
	public Memory(){
		this.memory = new ArrayList<Byte>();
	}
	
	public Memory(byte memory_contents[]){
		this.memory = new ArrayList<Byte>();
		this.setMemory(memory_contents);
	}
	
	public String toString(){
		Object[] memory_contents = memory.toArray();
		String memory = "";
		for (int i=0; i<memory_contents.length; i++){
			memory += memory_contents[i] + " ";	
		}
		return memory;
	}
	
	public ArrayList<Byte> getMemory(){
		return this.memory;
	}
	
	public void setMemory(byte value[]){
		for (int i=0; i<value.length; i++){
			this.memory.add(value[i]);
		}
	}
	
	public int readi(int pointer){
		byte intBytes[] = this.readM(pointer, this.int_size);
		return ByteBuffer.wrap(intBytes).getInt();
	}
	
	public byte[] readM(int pointer, int no_to_read){
		byte read[] = new byte[no_to_read];
		for (int i=0; i<no_to_read; i++){
			read[i] = this.memory.get(pointer);
			pointer++;
		}
		return read;
	}
	public void writeM(int pointer, byte data[]){
		if (this.memory.size() <= (pointer + data.length)){
			do{
				this.memory.add(new Byte((byte)0));
			}while(this.memory.size() < (pointer + data.length));
		}
		for (int i=0; i<data.length; i++){
			this.memory.set(pointer, new Byte(data[i]));
			pointer++;
		}
	}
}
