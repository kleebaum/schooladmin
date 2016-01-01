package de.schooladmin;


public interface ControllerInterface {
	
	void addView(ViewInterface view);
	void errorMessage(String message);
	void setSelectedTeacher(Teacher selectedTeacher);
	void setError(String message);

}
