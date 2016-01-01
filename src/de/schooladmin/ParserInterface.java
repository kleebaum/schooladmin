package de.schooladmin;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * Interface for Parser for reading text files
 * 
 * @author Anja Kleebaum
 *
 */
public interface ParserInterface {
	
	public final Path filePath = null;
	public final String delimeter = " ";
	public ArrayList<String> variablen = new ArrayList<String>();
	public ArrayList<ArrayList<String>> table = null;

	public void processLineByLine() throws IOException;
	public void processLine(String nextLine);
	public ArrayList<ArrayList<String>> getTable();
}
