package de.schooladmin;

import java.util.ArrayList;

/**
 * Class for school subjects
 * 
 * @author Anja Kleebaum
 *
 */
public class SchoolSubject implements Comparable<SchoolSubject> {
	private String name;
	private ArrayList<SchoolGroup> schoolGroups = new ArrayList<SchoolGroup>();
	private ArrayList<Teacher> teachersOnSubject = new ArrayList<Teacher>();

	public SchoolSubject(String name) {
		this.name = name;
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

	@Override
	public int compareTo(SchoolSubject o) {
		if (this.getName().charAt(0) > o.getName().charAt(0))
			return 1;
		else if (this.getName().charAt(0) < o.getName().charAt(0))
			return -1;
		else
			return 0;

	}

	public ArrayList<Teacher> getTeachersOnSubject() {
		return teachersOnSubject;
	}

	public void setTeachersOnSubject(ArrayList<Teacher> teachersOnSubject) {
		this.teachersOnSubject = teachersOnSubject;
	}

}
