package de.schooladmin;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface ModelInterface {
	
	// input file location
	final String ConfigFile = "input/config.txt";
	final String version = "1.0.4";
	
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
	 * @throws FileNotFoundException 
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
	 * sets selected teacher by abbr
	 * 
	 * @param selectedTeacher
	 */
	void setSelectedTeacher(String abbr);

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

	/**
	 * 
	 * @return program name
	 */
	String getName();
	
	/**
	 * sets selected class
	 * 
	 * @param selectedClass
	 */
	void setSelectedClass(SchoolClass selectedClass);
	
	/**
	 * sets selected class
	 * 
	 * @param selectedClass
	 */
	void setSelectedClass(String selectedClassName);		

	/**
	 * 
	 * @return selected class
	 */
	SchoolClass getSelectedClass();

	/**
	 * 
	 * @param teacher
	 * @param value
	 * 
	 * adds value to the teachers teaching hours, notifies observers
	 */
	void teacherAddToActDo(Teacher teacher, double value);

}
