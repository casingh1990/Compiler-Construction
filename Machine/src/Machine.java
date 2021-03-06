import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Main Machine class. 
 * 
 * This class implements the hardware that the compiled code can run on
 * @author Chandrashekar Singh
 *
 */
public class Machine {

	public int MAX_INSTR = 5000;
	private final int MAX_STR_LEN = 256;

	private final int OP_PUSH = 1;
	private final int OP_POP = 2;
	// private final int OP_POP1 = 3; //not used at the moment (For optimisation
	// purposes)
	private final int OP_POPS = 4;

	private final int OP_PUSHI = 5;
	private final int OP_PUSHF = 6;
	// private final int OP_PUSH1 = 7; //not used at the moment (For
	// optimisation purposes)
	private final int OP_PUSHS = 8;
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
	private final int OP_JUMPT = 43;
	private final int OP_JTAB = 44;

	private final int OP_WRITELN = 80;
	private final int OP_WRITE = 81;

	private final int STOP = 99;

	private final int address_size = 4;

	private Stack stack;
	private Memory code;
	private Memory data;

	int ip = 0;
	int sp = 0;

	/**
	 * For Debugging
	 **/
	private String cur_log_output;

	public Machine(byte code[]) {
		this.code = new Memory(code);
		this.data = new Memory();
		this.stack = new Stack();
	}

	public void run() {
		byte val[];
		boolean running = true;
		String log_output = "";
		String cur_inst_str = "";
		String cur_operands = "";
		int i = 0;
		while (running) {

			int cur_inst = this.readInstruction();

			cur_log_output = "===========================================================================\n";
			cur_log_output += "Stack " + this.stack.toString() + " SP "
					+ this.stack.sp + "\n";
			cur_log_output += "Code " + this.code.toString() + "\nIP "
					+ this.ip;
			if ((cur_inst != 99) && ((this.ip + 4) < this.code.getMemory().size()) ){
				cur_operands = new Byte(this.code.readM(this.ip + 0, 1)[0])
						.toString() + " ";
				cur_operands += new Byte(this.code.readM(this.ip + 1, 1)[0])
						.toString() + " ";
				cur_operands += new Byte(this.code.readM(this.ip + 2, 1)[0])
						.toString() + " ";
				cur_operands += new Byte(this.code.readM(this.ip + 3, 1)[0])
						.toString() + " ";
			}

			cur_log_output += "\n" + "Data " + this.data.toString() + "\n";

			int a;

			switch (cur_inst) {
			case OP_PUSH:
				a = getAddress();
				this.stack.pushMultiple(this.data.readM(a, 4));
				cur_inst_str = "OP_PUSH";
				break;
			case OP_PUSHI:
				val = this.code.readM(ip, address_size);
				ip += address_size;
				int value_to_push = ByteBuffer.wrap(val).getInt();
				this.stack.pushi(value_to_push);
				cur_inst_str = "OP_PUSHI";
				break;
			case OP_PUSHF:
				val = this.code.readM(ip, address_size);
				ip += address_size;
				float float_to_push = ByteBuffer.wrap(val).getFloat();
				this.stack.pushf(float_to_push);
				cur_inst_str = "OP_PUSHF";
				break;
			case OP_POP:
				a = getAddress();
				val = this.stack.popMultiple(address_size);
				this.data.writeM(a, val);
				cur_inst_str = "OP_POP";
				break;

			case OP_POPS:
				this.popString();
				break;

			case OP_PUSHS:
				this.pushString();
				break;

			case OP_PUSHSC:
				this.pushStringConstant();
				break;

			case OP_GET:
				cur_inst_str = "OP_GET";
				this.doGet();
				break;
			case OP_PUT:
				cur_inst_str = "OP_PUT";
				this.doPut();
				break;
			case OP_BOUND:
				cur_inst_str = "OP_BOUND";
				this.doBound();
				break;

			case OP_ADD:
				this.arithmetic_op(false, OP_ADD);
				cur_inst_str = "OP_ADD";
				break;
			case OP_SUB:
				this.arithmetic_op(false, OP_SUB);
				cur_inst_str = "OP_SUB";
				break;
			case OP_DIV:
				this.arithmetic_op(false, OP_DIV);
				cur_inst_str = "OP_DIV";
				break;
			case OP_MUL:
				this.arithmetic_op(false, OP_MUL);
				cur_inst_str = "OP_MUL";
				break;
			case OP_ADDF:
				this.arithmetic_op(true, OP_ADD);
				cur_inst_str = "OP_ADDF";
				break;
			case OP_SUBF:
				this.arithmetic_op(true, OP_SUB);
				cur_inst_str = "OP_SUBF";
				break;
			case OP_DIVF:
				this.arithmetic_op(true, OP_DIV);
				cur_inst_str = "OP_DIVF";
				break;
			case OP_MULF:
				this.arithmetic_op(true, OP_MUL);
				cur_inst_str = "OP_MULF";
				break;

			case OP_MOD:
				this.arithmetic_op(false, OP_MOD);
				cur_inst_str = "OP_MOD";
				break;
			case OP_MODF:
				this.arithmetic_op(true, OP_MOD);
				cur_inst_str = "OP_MODF";
				break;

			case OP_INCR:
				this.increment();
				cur_inst_str = "OP_INCR";
				break;

			case OP_EQ:
				cur_inst_str = "OP_EQ";
			case OP_LT:
				cur_inst_str = "OP_LT";
			case OP_LTEQ:
				cur_inst_str = "OP_LTEQ";
			case OP_MTEQ:
				cur_inst_str = "OP_MTEQ";
			case OP_MT:
				cur_inst_str = "OP_MT";
			case OP_NOTEQ:
				cur_inst_str = "OP_NOTEQ";
				this.boolean_op(cur_inst);
				break;

			case OP_JUMP:
				cur_inst_str = "OP_JUMP";
				this.jump();
				break;
			case OP_JUMPF:
				cur_inst_str = "OP_JUMPF";
				this.jump(false);
				break;
			case OP_JUMPT:
				cur_inst_str = "OP_JUMPT";
				this.jump(true);
				break;

			case OP_JTAB:
				cur_inst_str = "OP_JTAB";
				this.jtab();
				break;

			case OP_XCH1:
				cur_inst_str = "OP_XCH1";
				this.xch1();
				break;
			case OP_XCH2:
				cur_inst_str = "OP_XCH2";
				this.xch2();
				break;
			case OP_CSTR:
				cur_inst_str = "OP_CSTR";
				this.cstr();
				break;

			case OP_WRITE:
			case OP_WRITELN:
				cur_inst_str = "OP_WRITE";
				this.write(cur_inst);
				break;
			/**
			 * ========================================================================
			 * Operations not implemented
			 * ========================================================================
			 * and, or,xor,shl,shr not and neg (unary)
			 * fneg,//convert  neg, ess, 
			 * fneg (3. Control)
			 **/
			case STOP:
				cur_inst_str = "OP_STOP";
				running = false;
				break;
			}
			cur_log_output += cur_inst_str + " " + cur_operands + "\n";
			cur_log_output += "---------------------------------------\n";
			cur_log_output += "Stack " + this.stack.toString() + " SP "
					+ this.stack.sp + "\n";
			cur_log_output += "Data " + this.data.toString() + "\n";
			cur_log_output += "===========================================================================\n";
			//System.out.println(cur_log_output); //debug statement
			log_output += cur_log_output;

			/**
			 * Debug code to catch possible infinite loop situation
			 */
			if (i++ > this.MAX_INSTR) {
				running = false;
				System.out.println("\n\nDEBUG\n\nMaximum of " + this.MAX_INSTR
						+ " instructions reached\n\n");
			}

		}
		File log = new File("log.txt");
		FileWriter fw;
		try {
			fw = new FileWriter(log);
			fw.write(log_output);
			fw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void popString() {
		int a = this.getAddress();
		byte data_towrite[] = this.stack.popMultiple(MAX_STR_LEN);
		this.data.writeM(a, data_towrite);
	}

	private void pushString() {
		int a = this.getAddress();
		byte str[] = this.data.readM(a, MAX_STR_LEN);
		this.stack.pushMultiple(str);
	}

	private void pushStringConstant() {
		byte str[] = this.code.readM(ip, MAX_STR_LEN);
		this.ip += MAX_STR_LEN;
		this.stack.pushMultiple(str);
	}

	private int readInstruction() {
		byte inst[] = this.code.readM(this.ip, 1);
		this.ip++;
		return (int) inst[0];
	}

	private int getAddress() {
		int address = this.code.readi(this.ip);
		this.ip += address_size;
		return address;
	}

	private void xch1() {
		int a = this.stack.popi();
		this.stack.pushf((float) (a));
	}

	private void xch2() {
		float b = this.stack.popf();
		this.xch1();
		this.stack.pushf(b);
	}

	private void cstr() {
		int i = 0;
		float b = this.stack.popf();
		byte str[] = new Float(b).toString().getBytes();
		byte str_to_push[] = new byte[MAX_STR_LEN];
		str_to_push[0] = (byte) str.length;
		for (i = 1; i <= str.length; i++) {
			str_to_push[i] = str[i - 1];
		}
		for (i = (str.length + 1); i < MAX_STR_LEN; i++) {
			str_to_push[i] = 0;
		}
		this.stack.pushMultiple(str_to_push);
	}

	private void doGet() {
		int address = this.stack.popi();
		byte a[] = this.data.readM(address, address_size);
		this.stack.pushMultiple(a);
	}

	private void doPut() {
		byte data[] = this.stack.popMultiple(address_size);
		int address = this.stack.popi();
		this.data.writeM(address, data);
	}

	private void doBound() {
		int index = this.stack.popi();
		int low = this.code.readi(this.ip);
		this.ip += this.code.int_size;
		int high = this.code.readi(this.ip);
		this.ip += this.code.int_size;
		int line = this.code.readi(this.ip);
		this.ip += this.code.int_size;
		if (!((low <= index) && (index <= high))) {
			this.error("Trying to access index " + index
					+ " in array index range [" + low + ".." + high + "] |"
					+ " Line " + line + " in source");
		}
		this.stack.pushi(index);
	}

	public void jtab() {
		int num_cases = this.getAddress();

		int value = this.stack.popi();
		int cur, cur_addr;
		for (int i = 0; i < num_cases; i++) {
			cur = this.getAddress();
			cur_addr = this.getAddress();
			if (cur == value) {
				this.ip = cur_addr;
				break;
			}
		}
	}

	private void jump() {
		int address = this.getAddress();
		this.ip = address;
	}

	private void jump(boolean condition) {
		if (condition) {
			if (this.stack.popi() == 1) {
				this.jump();
			} else {
				this.ip += 4;
			}
		} else {
			if (this.stack.popi() == 0) {
				this.jump();
			} else {
				this.ip += 4;
			}
		}
	}

	private void boolean_op(int op) {
		int b = this.stack.popi();
		int a = this.stack.popi();
		int result = 0;
		switch (op) {
		case OP_EQ:
			result = (a == b) ? 1 : 0;
			break;
		case OP_LT:
			result = (a < b) ? 1 : 0;
			break;
		case OP_LTEQ:
			result = (a <= b) ? 1 : 0;
			break;
		case OP_MTEQ:
			result = (a >= b) ? 1 : 0;
			break;
		case OP_MT:
			result = (a > b) ? 1 : 0;
			break;
		case OP_NOTEQ:
			result = (a != b) ? 1 : 0;
			break;
		}
		this.stack.pushi((int) result);
	}

	private void arithmetic_op(boolean float_op, int op) {
		float b = (float_op) ? this.stack.popf() : this.stack.popi();
		float a = (float_op) ? this.stack.popf() : this.stack.popi();

		float result = 0;

		switch (op) {
		case OP_ADD:
			result = a + b;
			break;
		case OP_SUB:
			result = a - b;
			break;
		case OP_DIV:
			result = a / b;
			break;
		case OP_MUL:
			result = a * b;
			break;
		case OP_MOD:
			result = a % b;
			this.stack.pushi((int) result);
			return;
		}

		// System.out.println(a + " " + float_op + " " + b + " result is " +
		// result + "\n");

		if (float_op) {
			this.stack.pushf(result);
		} else {
			this.stack.pushi((int) result);
		}
	}

	private void write(int cur_str) {
		byte str[] = this.stack.popMultiple(this.MAX_STR_LEN);
		int i = 0;

		// String output =
		// "\n|----------------------------------------Standard Output Begin----------------------------------------\n|    ";
		String output = "";
		for (i = 1; i <= str[0]; i++) {
			output += (char) str[i];
		}

		switch (cur_str) {
		case OP_WRITE:
			break;
		case OP_WRITELN:
			output += "\n";
		}
		// output +=
		// "\n-------------------------------------------Standard Output End----------------------------------------\n";
		System.out.print(output);
		this.cur_log_output += output;
	}

	private void increment() {
		int a = this.stack.popi();
		this.stack.pushi(a + 1);
	}

	public void error() {
		this.error("Unknown Error");
	}

	public void error(String error) {
		System.out.println("Error has occured near " + this.ip + " | " + error);
		System.exit(1);
	}

	public static void main(String[] args) {
		if (args.length == 0) {
			System.out.println("Usage");
			System.out.println("machine.jar <program>");
			System.out
					.println("<program> required - The output file from the compiler");
			System.exit(1);
		}

		byte test_code[];
		Path path = Paths.get(args[0]);

		try {
			test_code = Files.readAllBytes(path);
			Machine m = new Machine(test_code);
			m.run();
		} catch (IOException e) {
			System.out.println("Can't read code_file");
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
}
