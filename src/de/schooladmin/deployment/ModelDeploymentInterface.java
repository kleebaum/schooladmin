package de.schooladmin.deployment;

import java.io.FileNotFoundException;
import java.util.ArrayList;

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
	
	/**
	 * initializes school class list
	 * 
	 * @param classes
	 *            list of school classes
	 */
	void setClasses(ArrayList<SchoolClass> classes);

	/**
	 * 
	 * @return list of school classes
	 */
	ArrayList<SchoolClass> getClasses();
	
}
