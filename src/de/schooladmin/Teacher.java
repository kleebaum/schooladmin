package de.schooladmin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Date;

/**
 * Class for teachers
 * 
 * @author Anja Kleebaum
 *
 */
public class Teacher {

	private String surname;
	private String firstname;
	private String abbr;
	private Date birthday;
	private gender gender;
	private double toDo; // Sollstunden
	private double actDo; // aktueller Einsatz
	private String schoolType;
	private String subjects;
	private ArrayList<SchoolGroup> schoolGroups;
	
	private BufferedReader in;

	private enum gender {
		F, M
	};

	// private enum schooltype {R, G, H};
	private String[] teacherSpmText;
	private String timeTableText;
	private String[] teacherData;
	private String timestamp;

	public Teacher(String surname, String firstname, String abbr) {
		this.surname = surname;
		this.firstname = firstname;
		this.abbr = abbr;
		// this.gender = gender.F;
		//this.teacherData = teacherData;
		this.toDo = 0;
		this.actDo = 0;
		this.schoolType = "";		
		this.schoolType = "Schultyp fehlt";		
	}
	
	public Teacher(String surname, String firstname, String abbr, String fileName) {
		this(surname, firstname, abbr);
		this.timeTableText = readTeacherTimeTableTextFromFile(fileName);
	}
	
	private String readTeacherTimeTableTextFromFile(String fileName) {
		String timeTableText = "";
		boolean foundRoom = false;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName),
					StandardCharsets.ISO_8859_1));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {
				if (foundRoom == true) {
					if (zeile.equals(""))
						break;
					timeTableText += zeile + "\r\n";
				}
				String firstnameAbbr = "";
				String name = this.surname;
				String nameLonger = "";

				try {
					firstnameAbbr += this.firstname.substring(0, 2);
					nameLonger += name + " " + firstnameAbbr;
					name += " " + firstnameAbbr.substring(0,1) + ".";
				} catch (StringIndexOutOfBoundsException e) {
					
				}

				if (zeile.contains("Staatl.") && zeile.contains(nameLonger) || zeile.contains(name)) {
					foundRoom = true;
					zeile = in.readLine();
					this.timestamp = zeile;
					zeile = in.readLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (timeTableText.equals(""))
			return(timeTableText);
		return(this.timestamp +"\r\n" + timeTableText);
	}

	public String[] readTeacherSpmTextFromFile(String fileName)  throws FileNotFoundException  {
		boolean foundSpm = false;
		ArrayList<String> spmArrayList = new ArrayList<String>();
		try {
			in = new BufferedReader(new FileReader(fileName));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {
				if (foundSpm == true) {
					if (zeile.equals(""))
						break;
					spmArrayList.add(zeile);
				}
				if (zeile.startsWith("Spm++ Statistik: " + this.abbr)) {
					foundSpm = true;
					spmArrayList.add(zeile);
				}

			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return spmArrayList.toArray(new String[spmArrayList.size()]);
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String surname) {
		this.surname = surname;
	}

	/**
	 * @return the firstname
	 */
	public String getFirstname() {
		return firstname;
	}

	/**
	 * @param firstname
	 *            the firstname to set
	 */
	public void setFirstname(String firstname) {
		this.firstname = firstname;
	}

	/**
	 * @return the abbr
	 */
	public String getAbbr() {
		return abbr;
	}

	/**
	 * @param abbr
	 *            the abbr to set
	 */
	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	/**
	 * @return the teacherSpmText
	 */
	public String[] getTeacherSpmText() {
		return teacherSpmText;
	}

	/**
	 * @param teacherSpmText
	 *            the teacherSpmText to set
	 */
	public void setTeacherSpmText(String[] teacherSpmText) {
		this.teacherSpmText = teacherSpmText;
	}

	/**
	 * @return the gender
	 */
	public gender getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(gender gender) {
		this.gender = gender;
	}

	/**
	 * @return the birthday
	 */
	public Date getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday
	 *            the birthday to set
	 */
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}

	/**
	 * @return the timeTable
	 */
	public String getTimeTableText() {
		return timeTableText;
	}

	/**
	 * @param timeTableText
	 *            the timeTable to set
	 */
	public void setTimeTableText(String timeTableText) {
		this.timeTableText = timeTableText;
	}

	/**
	 * @return the teacherData
	 */
	public String[] getTeacherData() {
		return teacherData;
	}

	/**
	 * @param teacherData
	 *            the teacherData to set
	 */
	public void setTeacherData(String[] teacherData) {
		this.teacherData = teacherData;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	/**
	 * @return the toDo
	 */
	public double getToDo() {
		return toDo;
	}

	/**
	 * @param toDo the toDo to set
	 */
	public void setToDo(double toDo) {
		this.toDo = toDo;
	}

	/**
	 * @return the subjects
	 */
	public String getSubjects() {
		return subjects;
	}

	/**
	 * @param subjects the subjects to set
	 */
	public void setSubjects(String subjects) {
		this.subjects = subjects;
	}

	/**
	 * @return the schoolType
	 */
	public String getSchoolType() {
		return schoolType;
	}

	/**
	 * @param schoolType the schoolType to set
	 */
	public void setSchoolType(String schoolType) {
		this.schoolType = schoolType;
	}

	/**
	 * @return the actDo
	 */
	public double getActDo() {
		return actDo;
	}

	/**
	 * @param actDo the actDo to set
	 */
	public void setActDo(double actDo) {
		this.actDo = actDo;
	}
	
	/**
	 * @param subtract from actual done hours
	 */
	public void subtractFromActDo(double value) {
		this.actDo -= value;
	}
	
	/**
	 * @param add to actual done hours
	 */
	public void addToActDo(double value) {
		this.actDo += value;
	}

	/**
	 * @return the schoolGroups
	 */
	public ArrayList<SchoolGroup> getSchoolGroups() {
		return schoolGroups;
	}

	/**
	 * @param schoolGroups the schoolGroups to set
	 */
	public void setSchoolGroups(ArrayList<SchoolGroup> schoolGroups) {
		this.schoolGroups = schoolGroups;
	}
	
	public void initSchoolGroups() {
		this.schoolGroups = new ArrayList<SchoolGroup>();
	}

}
