import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class Source {

	private byte[] source;
	private String source_file;
	private int scanp = 0;
	private int curline = 0;
	private int curCol = 0;

	public Source(String filename) {
		this.scanp = 0;
		this.curCol = 0;
		this.curline = 0;
		this.source_file = filename;
		this.readFile();
	}

	public void advance() {
		this.scanp++;
		if (this.scanp < this.source.length) {
			if ((this.source[scanp] == 10)) {
				this.setCurline(this.getCurline() + 1);
				this.setCurCol(0);
			} else {
				this.setCurCol(this.getCurCol() + 1);
			}
		}
	}

	public void retreat() {
		this.scanp--;
	}

	/**
	 * @TODO need to cater for escape string
	 * @param bound
	 * @return
	 */
	public String getStringWithBound(String bound) {
		String str = "";
		this.advance();
		while ((notAtBound(bound)) || (!notAtBound("''"))) {
			/**
			 * If the current character is the escape '' then skip the following
			 * character
			 */
			if (this.getSourceChar() == 39) {
				this.advance();
			}
			str += (char) this.getSourceChar();
			this.advance();
		}
		this.scanp += bound.length();
		return str;
	}

	/**
	 * Returns true if bound is not found from the current position, false
	 * otherwise
	 * 
	 * @param String
	 *            bound
	 * @return boolean
	 */
	public boolean notAtBound(String bound) {
		int cur_pos = this.scanp;
		byte bound_b[] = bound.getBytes();
		int i = 0;
		while ((i < bound.length())) {
			// System.out.println("not at bound " + i + " | "
			// +this.getSourceChar() + "  " + bound_b[i]);
			if (this.getSourceChar() != bound_b[i]) {
				this.scanp = cur_pos;
				return true;
			}
			this.advance();
			i++;
		}
		this.scanp = cur_pos;
		return false;
	}

	public byte getSourceChar() {
		/*
		 * I am thinking that this if condition should be handled by an
		 * exception handler.
		 */
		if (this.EOF()) {
			System.out.println("Unexpected end of file!");
			System.out.println("Line: " + (this.curline + 1) + "   Col: "
					+ (this.curCol + 1));
			System.exit(0);
		}
		return this.source[this.scanp];
	}

	/**
	 * If the scan pointer is greater than the length of the source then we are
	 * at the end of the file
	 * 
	 * @return boolean
	 */
	public boolean EOF() {
		if (this.source.length > this.scanp)
			return false;
		return true;
	}

	public void readFile() {
		this.scanp = 0;
		this.curCol = 0;
		this.curline = 0;
		try {
			File input = new File(this.source_file);
			FileInputStream source_file = new FileInputStream(input);
			try {
				source = new byte[(int) input.length()];
				source_file.read(source);
				source_file.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

	public byte[] getSource() {
		return source;
	}

	public void setSource(byte[] source) {
		// This cannot be used because it is assumed that this would allways
		// take input from source file
		// this.source = source;
	}

	public int getCurline() {
		return curline;
	}

	public void setCurline(int curline) {
		this.curline = curline;
	}

	public int getCurCol() {
		return curCol;
	}

	public void setCurCol(int curCol) {
		this.curCol = curCol;
	}
}
