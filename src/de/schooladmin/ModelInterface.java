package de.schooladmin;

import java.util.ArrayList;

public interface ModelInterface {
	
	// input file location
	String FileConfig = "input/config.txt";
	String FileLehrer = "input/LehrerDaten.csv";
	String version = "0.9.2";
	
	/**
	 * observing view is registered
	 */
	void registerObserver(ObserverInterface o);
	
	/**
	 * observing view is removed
	 */
	void removeObserver(ObserverInterface o);
	
	/**
	 * observing views are notified after changes occured
	 */
	void notifyObservers();
	
	/**
	 * initializes the program's model, first method called
	 */
	void initialize();
	
	/**
	 * returns the teacher belonging to a certain abbreviation
	 * @return teacher
	 */
	Teacher getTeacherByAbbr(String abbr);
	
	/**
	 * @return the error
	 */
	public String getError();

	/**
	 * @param error the error to set
	 */
	public void setError(String error);
	
	/**
	 * sets selected teacher
	 * 
	 * @param selectedTeacher
	 */
	void setSelectedTeacher(Teacher selectedTeacher);

	/**
	 * 
	 * @return selected teacher
	 */
	Teacher getSelectedTeacher();

	/**
	 * initializes teacher list
	 * 
	 * @param teachers
	 */
	void setTeachers(ArrayList<Teacher> teachers);

	/**
	 * 
	 * @return list of teachers
	 */
	ArrayList<Teacher> getTeachers();

	String getName();


}
