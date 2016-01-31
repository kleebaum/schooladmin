package de.schooladmin.deployment;

import java.io.FileNotFoundException;
import de.schooladmin.ModelInterface;
import de.schooladmin.SchoolClass;

public interface ModelDeploymentInterface extends ModelInterface {

	/**
	 * reads teacher data from file and creates objects of class Teacher
	 * @throws FileNotFoundException 
	 * 
	 * @param fileName
	 */
	void initTeachers(String fileName) throws FileNotFoundException;
	
	void exportSchoolClassToCVS(SchoolClass schoolClass, boolean fileChooser);

	void exportTeacherOverviewToCVS(boolean fileChooser);
	
}
