package de.schooladmin;

public interface ControllerInterface {
	
	void addView(ViewInterface view);
	void errorMessage(String message);
	void setSelectedTeacher(Teacher selectedTeacher);
	void setSelectedTeacher(String abbr);
	void setSelectedClass(SchoolClass selectedClass);
	void setSelectedClass(String selectedClassName);
	void setError(String message);
	void teacherAddToActDo(Teacher selectedTeacher, double value);

}
