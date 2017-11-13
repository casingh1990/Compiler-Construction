import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Compiler class this is the main class for the compiler
 * 
 * It first loads the file to be compiled
 * Scanner is then invoked to tokenize the source file
 * Parser is then used to generate the output
 * Finally output is written to output file
 * @author Chandrashekar Singh
 *
 */
public class Compiler {

	public static String filename = "";
	public static String outputFile = "";

	public static void main(String[] args) {
		handleArgs(args);

		System.out.println("Scanning");
		Scanner tk_search = new Scanner(filename);
		tk_search.findTokens();
		Parser parser = new Parser(tk_search.getTokens());
		System.out.println("Parsing....");
		parser.parse();
		Compiler.outPutCompiledFile(parser.getParsed());
	}

	public static void handleArgs(String[] args) {

		int FILENAME = 0;
		int OUTPUTFILE = 1;

		switch (args.length) {

		case 1:
			setFileName(args[FILENAME]);
			break;
		case 2:
			setFileName(args[FILENAME]);
			setOutputFile(args[OUTPUTFILE]);
			break;
		default:
		case 0:
			help();
		}
	}

	public static void setFileName(String name) {
		filename = name;
		outputFile = name.replace(".pas", ".cas");
	}

	public static void setOutputFile(String output) {
		outputFile = output;
	}

	public static void help() {
		System.out
				.println("Compiles Pascal programs for the CAS_01 virtual machine");
		System.out.println("compiler.jar <source_file>");
		System.out.println("compiler.jar <source_file> <output_file>");
		System.out
				.println("<source_file> Required - The pascal source file path");
		System.out.println("<output_file> Optional - The output file path");
		System.out.println("Sample Input");
		System.out.println("compiler.jar test.pas");
		System.out.println("compiler.jar test.pas test.out");
		System.exit(1);
	}

	public static void outPutCompiledFile(ArrayList<Byte> output) {
		// File log = new File("code");
		// FileWriter fw;
		System.out.println("Writing to output file "  + outputFile);
		try {
			// fw = new FileWriter(log);
			Iterator<Byte> itr = output.iterator();
			byte[] aBytes = new byte[output.size()];
			int i = 0;
			while (itr.hasNext()) {
				// fw.write(itr.next().toString());
				aBytes[i++] = itr.next();
			}
			// fw.close();

			Path path = Paths.get(outputFile);
			Files.write(path, aBytes);

		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Compiled Successfully");
	}
}