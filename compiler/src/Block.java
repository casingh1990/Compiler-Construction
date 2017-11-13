import java.util.ArrayList;


/**
 * This class is used to generate the ID for each block.
 * This is used in processing goto statements to make sure that they are legal
 * @author Chandrashekar Singh
 *
 */
public class Block {
	private int curDept;
	private ArrayList<Integer> block;
	
	public Block(){
		this.block = new ArrayList<>();
		this.curDept = 0;
	}
	
	public void startBlock(){
		this.curDept++;
		if (this.block.size() <= this.curDept){
			this.block.add(0);
		}
		else{
			//this.block.set(this.curDept, (this.block.get(this.curDept) + 1));
		}
	}
	
	public void endBlock(){
		this.block.set(this.curDept-1, (this.block.get(this.curDept-1) + 1));
		this.curDept--;
	}
	
	public String curBlock(){
		String cur_block = "";
		for (int i=0; i<curDept; i++){
			cur_block += this.block.get(i);
		}
		return cur_block;
	}
}
