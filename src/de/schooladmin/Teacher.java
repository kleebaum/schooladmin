package de.schooladmin;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
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
	private String birthday;
	private String gender;
	private double toDo; // Sollstunden
	private double actDo; // aktueller Einsatz
	private String schoolType;
	// public ArrayList<String> schoolTypeList;
	private String subjects;
	private ArrayList<SchoolGroup> schoolGroups;

	private BufferedReader in;

	// private enum schooltype {R, G, H};
	private String teacherSpmText;
	private String timeTableText;
	private ArrayList<String> teacherData;
	private String timestamp;

	public double partTime;
	public double workAccount;
	public double scientificTime;
	public double handicapTime;
	public double seniorReduction;
	public double compensateMidTerm;
	public double compensateYear;
	public double plusMinus;
	public double directorTime;
	public double committeTime;
	public double functionTime;
	public String teachingTimeText;
	public double otherTimeDif;

	public Teacher(String surname, String firstname, String abbr) {
		this.surname = surname;
		this.firstname = firstname;
		this.abbr = abbr;
		this.toDo = 0;
		this.actDo = 0;
		this.schoolType = "Schultyp fehlt";
		this.teacherData = new ArrayList<String>();
	}

	public Teacher(String surname, String firstname, String abbr, String gender, String birthday, int toDo) {
		this(surname, firstname, abbr);
		this.gender = gender;
		this.birthday = birthday;
		this.toDo = toDo;
	}

	public Teacher(String surname, String firstname, String abbr, String timeTableFile) {
		this(surname, firstname, abbr);
		this.timeTableText = readTeacherTimeTableTextFromFile(timeTableFile);
	}
	
	public Teacher(String surname, String firstname, String abbr, String timeTableFile, String statisticsFile) {
		this(surname, firstname, abbr, timeTableFile);
		this.teacherSpmText = readTeacherSpmTextFromFile(statisticsFile);
	}
	
	public Teacher(ArrayList<String> teacherData) {
		this.teacherData = teacherData;
	}
	
	public Teacher(ArrayList<String> teacherData, String timeTableFile, String statisticsFile) {
		this(teacherData.get(0), teacherData.get(1) ,teacherData.get(2), timeTableFile);
		this.teacherData = teacherData;
		this.teacherSpmText = readTeacherSpmTextFromFile(statisticsFile);
		this.birthday = teacherData.get(4);
	}

	private String readTeacherTimeTableTextFromFile(String fileName) {
		String timeTableText = "";
		boolean foundRoom = false;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.ISO_8859_1));
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
					name += " " + firstnameAbbr.substring(0, 1) + ".";
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
			return (timeTableText);
		return (this.timestamp + "\r\n" + timeTableText);
	}

	public String readTeacherSpmTextFromFile(String fileName) {
		String spmText = "";
		boolean foundSpm = false;
		try {
			in = new BufferedReader(new FileReader(fileName));
			String line = null;
			String lastLine = null;
			while ((line = in.readLine()) != null) {
				if (foundSpm == true) {
					if (line.equals(""))
						break;
					spmText += line + "\r\n";
				}
				if (line.startsWith("Spm++ Statistik: " + this.abbr)) {
					foundSpm = true;
					spmText += line + "\r\n";
				}
			lastLine = line;			
			}
			int lastLineLength = lastLine.trim().length();
			if (lastLine != null & lastLineLength > 1) {				
				this.actDo = Double.parseDouble(lastLine.substring(lastLineLength-2, lastLineLength).replaceAll("(^\\s+)|.", "0"));
			}			
		} catch (IOException | java.lang.NullPointerException e) {
			e.printStackTrace();
			return null;
		}
		
		return spmText;
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
	public String getTeacherSpmText() {
		return teacherSpmText;
	}

	/**
	 * @param teacherSpmText
	 *            the teacherSpmText to set
	 */
	public void setTeacherSpmText(String teacherSpmText) {
		this.teacherSpmText = teacherSpmText;
	}

	/**
	 * @return the gender
	 */
	public String getGender() {
		return gender;
	}

	/**
	 * @param gender
	 *            the gender to set
	 */
	public void setGender(String gender) {
		this.gender = gender;
	}

	/**
	 * @return the birthday
	 */
	public String getBirthday() {
		return birthday;
	}

	/**
	 * @param birthday
	 *            the birthday to set
	 */
	public void setBirthday(String birthday) {
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
	public ArrayList<String> getTeacherData() {
		return teacherData;
	}

	/**
	 * @param teacherData
	 *            the teacherData to set
	 */
	public void setTeacherData(ArrayList<String> teacherData) {
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
	 * @param toDo
	 *            the toDo to set
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
	 * @param subjects
	 *            the subjects to set
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
	 * @param schoolType
	 *            the schoolType to set
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
	 * @param actDo
	 *            the actDo to set
	 */
	public void setActDo(double actDo) {
		this.actDo = actDo;
	}

	/**
	 * @param subtract
	 *            from actual done hours
	 */
	public void subtractFromActDo(double value) {
		this.actDo -= value;
	}

	/**
	 * @param add
	 *            to actual done hours
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
	 * @param schoolGroups
	 *            the schoolGroups to set
	 */
	public void setSchoolGroups(ArrayList<SchoolGroup> schoolGroups) {
		this.schoolGroups = schoolGroups;
	}

	public void initSchoolGroups() {
		this.schoolGroups = new ArrayList<SchoolGroup>();
	}

	public int getAge() {
		Date date = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(date);

		Calendar born = Calendar.getInstance();
		if (!this.birthday.equals("")) {

			Date birthdayDate = new Date();
			DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
			try {
				birthdayDate = format.parse(this.birthday);
				born.setTime(birthdayDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int age = now.get(Calendar.YEAR) - born.get(Calendar.YEAR);
			if (born.get(Calendar.MONTH) > now.get(Calendar.MONTH)
					|| (born.get(Calendar.MONTH) == now.get(Calendar.MONTH)
							&& born.get(Calendar.DATE) > now.get(Calendar.DATE))) {
				age--;
			}
			return age;
		}
		return 0;
	}

	public int getSchoolAge() {
		int age = this.getAge();

		DateFormat format = new SimpleDateFormat("dd.MM.yyyy");
		Date date = new Date();
		Calendar now = Calendar.getInstance();
		now.setTime(date);	

		Calendar born = Calendar.getInstance();
		if (!this.birthday.equals("")) {

			Date birthdayDate = new Date();
			
			try {
				birthdayDate = format.parse(this.birthday);
				born.setTime(birthdayDate);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			if (now.get(Calendar.MONTH) > 1 && now.get(Calendar.MONTH) < 8 && born.get(Calendar.MONTH) > 1
					&& born.get(Calendar.MONTH) < 8) {
				System.out.println("hier");
				age--;
			}			
		}
		return age;
	}

	// public ArrayList<String> getSchoolTypeList() {
	// ArrayList<String> schoolTypeList = new ArrayList<String>();
	// for (String schoolType : this.schoolType.split(" "))
	// schoolTypeList.add(schoolType);
	// return schoolTypeList;
	// }

}
