package de.schooladmin;

/**
 * Class for school classes
 * 
 * @author Anja Kleebaum
 *
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class SchoolClass {
	private String name;
	private String timeTableText;
	private BufferedReader in;
	private String timestamp;
	private ArrayList<SchoolSubject> subjects = new ArrayList<SchoolSubject>();

	public SchoolClass(String name) {
		this.name = name;
	}
	
	public SchoolClass(String name, String fileName) {
		this(name);
		this.timeTableText = readClassTimeTableTextFromFile(fileName);
	}
	
	public SchoolClass(String name, ArrayList<SchoolSubject> subjects) {
		this(name);
		this.subjects = subjects;	
	}

	private String readClassTimeTableTextFromFile(String fileName) {
		String timeTableText = "";
		boolean foundClass = false;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(
					fileName),
					StandardCharsets.ISO_8859_1));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {
				if (foundClass == true) {
					if (zeile.equals(""))
						break;
					timeTableText += zeile + "\r\n";
				}
				if (zeile.contains("Staatl.") && zeile.contains(this.name)) {
					foundClass = true;
					this.timestamp = zeile;
					while (!zeile.startsWith("+")) {						
						zeile = in.readLine();
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (timeTableText.equals(""))
			return "";
		return this.timestamp + "\r\n" + timeTableText;
	}

	/**
	 * @return the timeTableText
	 */
	public String getTimeTableText() {
		return timeTableText;
	}

	/**
	 * @param timeTableText
	 *            the timeTableText to set
	 */
	public void setTimeTableText(String timeTableText) {
		this.timeTableText = timeTableText;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the subjects
	 */
	public ArrayList<SchoolSubject> getSubjects() {
		return subjects;
	}

	/**
	 * @param subjects the subjects to set
	 */
	public void setSubjects(ArrayList<SchoolSubject> subjects) {
		this.subjects = subjects;
	}

}
