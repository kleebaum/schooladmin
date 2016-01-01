package de.schooladmin;

import java.util.ArrayList;

/**
 * Class for school subjects
 * 
 * @author Anja Kleebaum
 *
 */
public class SchoolSubject {
	private String name;
	private ArrayList<SchoolGroup> schoolGroups = new ArrayList<SchoolGroup>();
	
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
	 * @param name the name to set
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
	 * @param schoolGroups the schoolGroups to set
	 */
	public void setSchoolGroups(ArrayList<SchoolGroup> schoolGroups) {
		this.schoolGroups = schoolGroups;
	}
	

}
