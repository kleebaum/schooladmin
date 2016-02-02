package de.schooladmin;

import java.io.FileNotFoundException;
import java.util.ArrayList;

public interface ModelInterface {
	
	// input file location
	final String ConfigFile = "input/config.txt";
	final String version = "1.2.9";
	
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
	 * @return the error
	 */
	public String getError();

	/**
	 * @param error the error to set
	 */
	public void setError(String error);

	/**
	 * 
	 * @return program name
	 */
	String getName();

	/**
	 * exports table to text file
	 * @param className
	 */
	void exportToCVS(String folder, String fileName, String header, 
			ArrayList<String> content, boolean fileChooser);

	School getSchool();

	void setSchool(School school);

	void setSelectedTeacher(String abbr);

	void setSelectedRoom(Room selectedRoom);

	Room getSelectedRoom();

	void setSelectedGroup(SchoolGroup selectedGroup);

	SchoolGroup getSelectedGroup();

	Teacher getOldSelectedTeacher();

	void setOldSelectedTeacher(Teacher oldSelectedTeacher);

	void setSelectedSubject(SchoolSubject selectedSubject);

	SchoolSubject getSelectedSubject();

	void setSelectedClass(String selectedClassName);

	void setSelectedClass(SchoolClass selectedClass);

	SchoolClass getSelectedClass();

	void setSelectedTeacher(Teacher selectedTeacher);

	Teacher getSelectedTeacher();

	String getFileTeachers();

}
