package de.schooladmin;

/**
 * Parser for reading text files
 * 
 * @author Anja Kleebaum
 *
 */
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

public class Parser implements ParserInterface {
	private final Path filePath;
	private final String delimeter;
	private Charset ENCODING;
	private ArrayList<String> variablen = new ArrayList<String>();
	private ArrayList<ArrayList<String>> table = new ArrayList<ArrayList<String>>();
	private Integer skipLine;
	private Scanner scanner;

	/**
	 * Constructors
	 * 
	 * @param fileName
	 *            full name of an existing, readable file.
	 * @param delimeter
	 *            separation between tokens
	 * @param encoding
	 *            speciefies encoding charset
	 */
	public Parser(String fileName, String delimeter, Charset encoding, Integer skipLine, ArrayList<String> variablen) {
		this.filePath = Paths.get(fileName);
		this.delimeter = delimeter;
		this.ENCODING = encoding;
		if (skipLine >= 0) {
			this.skipLine = skipLine;
		} else {
			this.skipLine = 0;
		}
		this.variablen = variablen;
	}

	public Parser(String fileName, String delimeter, Charset encoding, Integer skipLine) {
		this(fileName, delimeter, encoding, skipLine, null);
	}

	public Parser(String fileName, String delimeter) {
		this(fileName, delimeter, StandardCharsets.UTF_8, 1);
	}

	/** Template method that calls {@link #processLine(String)}. */
	public final void processLineByLine() throws IOException {
		table.add(this.variablen);
		try (Scanner scanner = new Scanner(filePath, this.ENCODING.name())) {
			while (this.skipLine > 0) {
				scanner.nextLine();
				this.skipLine--;
			}
			while (scanner.hasNextLine()) {
				processLine(scanner.nextLine());
			}
		}
	}

	private static void log(Object aObject) {
		System.out.print(String.valueOf(aObject));
	}

	@Override
	public void processLine(String nextLine) {
		scanner = new Scanner(nextLine);
		scanner.useDelimiter(this.delimeter);
		ArrayList<String> result = new ArrayList<String>();
		if (scanner.hasNext()) {
			ArrayList<String> variablen = this.variablen;
			for (int i = 0; i < variablen.size(); i++) {
				result.add(scanner.next());
			}
			table.add(result);
			// log("\r\n");
		} else {
			log("Empty or invalid line. Unable to process.");
		}
	}

	public ArrayList<ArrayList<String>> getTable() {
		return this.table;
	}

}