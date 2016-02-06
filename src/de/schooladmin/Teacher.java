package de.schooladmin;

import java.io.BufferedReader;
import java.io.FileInputStream;
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
	private String subjects;
	private ArrayList<SchoolGroup> schoolGroups;
	private SchoolType schoolType;

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
		this.schoolType = new SchoolType("", "Schultyp fehlt", 0.0);
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
		this(teacherData.get(0), teacherData.get(1), teacherData.get(2), timeTableFile);
		this.teacherData = teacherData;
		this.teacherSpmText = readTeacherSpmTextFromFile(statisticsFile);
		this.birthday = teacherData.get(4);
	}

	public Teacher(ArrayList<String> teacherData, String statisticsFile) {
		this(teacherData.get(0), teacherData.get(1), teacherData.get(2));
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
			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.ISO_8859_1));
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
				this.actDo = Double.parseDouble(
						lastLine.substring(lastLineLength - 2, lastLineLength).replaceAll("(^\\s+)|.", "0"));
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
		if (gender.substring(0, 1).equals("m"))
			return "m\u00e4nnlich";
		else if (gender.substring(0, 1).equals("w"))
			return "weiblich";
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
		if(teacherData!=null) {
			this.surname = teacherData.get(0);
			this.firstname = teacherData.get(1);
			this.abbr = teacherData.get(2);
			this.gender = teacherData.get(3);
			this.birthday = teacherData.get(4);
		}
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
		Date now = new Date();

		DateFormat formatMonth = new SimpleDateFormat("MM");
		DateFormat formatYear = new SimpleDateFormat("yyyy");

		if (!this.birthday.equals("")) {

			Date birthdayDate = new Date();

			try {
				birthdayDate = format.parse(this.birthday);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			int monthNow = Integer.parseInt(formatMonth.format(now));
			int monthBirthday = Integer.parseInt(formatMonth.format(birthdayDate));
			int yearNow = Integer.parseInt(formatYear.format(now));
			int yearBirthday = Integer.parseInt(formatYear.format(birthdayDate));
			if (monthNow > 1 && monthNow < 8 && monthBirthday > 1 && monthBirthday < 8
					&& yearNow == yearBirthday + age) {
				age--;
			}
		}
		return age;
	}

	public SchoolType getSchoolType() {
		if (schoolType != null)
			return schoolType;
		return new SchoolType("", "Schultyp fehlt", 0.0);
	}

	public void setSchoolType(SchoolType schoolType) {
		this.schoolType = schoolType;
	}

	public double getScientificLectureTime() {
		String scientificLessonsString = this.teacherData.get(6);
		if (!scientificLessonsString.isEmpty() && !scientificLessonsString.startsWith(" ")
				&& !scientificLessonsString.equals("-")) {
			return (Double.parseDouble(scientificLessonsString));
		}
		return 0.0;
	}

	public double getPartTime(double inputValue) {
		String partTimeString = this.getTeacherData().get(7).trim();
		if (partTimeString.equals("V") || partTimeString.equals("")) {
			return inputValue;
		} else {
			return Double.parseDouble(partTimeString.substring(1));
		}
	}

	public String getPartTimeText() {
		String partTimeString = this.getTeacherData().get(7).trim();
		if (partTimeString.equals("V") || partTimeString.equals("")) {
			return "Vollzeit \r\n";
		} else {
			return "Teilzeit " + partTimeString + "\r\n";
		}
	}

	public double getMobHours() {
		try {
			double mob = Double.parseDouble(this.getTeacherData().get(8));
			return mob;
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	public String getMobHoursText() {
		double mob = this.getMobHours();
		if (mob == 0) {
			return "";
		}
		return "Mob: " + mob + " h \r\n";
	}

	public String getPlusMinusText() {
		return this.getTeacherData().get(13) + "\r\n";
	}

	public double getToDo(int semester) {
		try {
			if (semester == 1) {
				return Double.parseDouble(this.getTeacherData().get(9));
			}
			return Double.parseDouble(this.getTeacherData().get(11));
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	public String getToDoText(int semester) {
		double toDo = this.getToDo(semester);
		if (semester == 2 && toDo == 0)
			return "";
		return "Soll " + semester + ". HJ: " + toDo + " h \r\n";
	}

	public double getActDo(int semester) {
		try {
			if (semester == 1) {
				return Double.parseDouble(this.getTeacherData().get(10));
			}
			return Double.parseDouble(this.getTeacherData().get(12));
		} catch (NumberFormatException e) {
			return 0.0;
		}
	}

	public String getActDoText(int semester) {
		double actDo = this.getActDo(semester);
		if (semester == 2 && actDo == 0)
			return "";
		return "Einsatz " + semester + ". HJ: " + actDo + " h \r\n";
	}
	
	public Double getSeniorReduction() {
		return this.schoolType.getPartialRetirement(this.getSchoolAge());
	}
	
	public Double getTeachingTimePerScLes() {
		return this.schoolType.getTeachingTimePerScLes(this.getScientificLectureTime());
	}

	// public ArrayList<String> getSchoolTypeList() {
	// ArrayList<String> schoolTypeList = new ArrayList<String>();
	// for (String schoolType : this.schoolType.split(" "))
	// schoolTypeList.add(schoolType);
	// return schoolTypeList;
	// }

}
