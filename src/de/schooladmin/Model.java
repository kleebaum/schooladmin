package de.schooladmin;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Model implements ModelInterface {
	
	protected Properties prop;
	protected String name;
	protected ArrayList<Teacher> teachers;
	protected ArrayList<SchoolClass> classes;
	protected HashMap<String, Teacher> teacherAbbrMap = new HashMap<String, Teacher>();
	protected HashMap<String, SchoolClass> classNameMap = new HashMap<String, SchoolClass>();
	private String error = "";
	
	protected Teacher selectedTeacher = null;
	protected SchoolClass selectedClass;
	
	// list containing observing views
	public ArrayList<ObserverInterface> observers = new ArrayList<ObserverInterface>();
	
	public Model() {
		this.teachers = new ArrayList<Teacher>();
		this.classes = new ArrayList<SchoolClass>();
	}

	public Properties readConfig(String fileName) {
		prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(fileName);

			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}

	@Override
	public void registerObserver(ObserverInterface o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(ObserverInterface o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers() {
		for (int i = 0; i < observers.size(); i++) {
			ObserverInterface observer = observers.get(i);
			observer.update();
		}
	}

	@Override
	public void initialize() {
		prop = readConfig(ConfigFile);
		this.name = prop.get("Name").toString();
	}
	
	@Override
	public Teacher getTeacherByAbbr(String abbr) {
		if (abbr.equals(""))
			return null;
		return teacherAbbrMap.get(abbr);
	}
	
	public SchoolClass getClassByName(String name) {
		if (name.equals(""))
			return null;
		return classNameMap.get(name);
	}
	
	@Override
	public String getError() {
		return error;
	}
	
	@Override
	public void setError(String error) {
		this.error = error;
	}
	
	@Override
	public Teacher getSelectedTeacher() {
		return selectedTeacher;
	}

	@Override
	public void setSelectedTeacher(Teacher selectedTeacher) {
		this.selectedTeacher = selectedTeacher;
		notifyObservers();
	}
	
	public void setSelectedTeacher(String abbr) {
		this.selectedTeacher = this.getTeacherByAbbr(abbr);
		notifyObservers();
	}
	
	@Override
	public void setTeachers(ArrayList<Teacher> teachers) {
		this.teachers = teachers;
	}

	@Override
	public ArrayList<Teacher> getTeachers() {
		return this.teachers;
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public void setSelectedClass(SchoolClass selectedClass) {
		this.selectedClass = selectedClass;
		notifyObservers();
	}
	
	@Override
	public void setSelectedClass(String className) {
		this.selectedClass = this.getClassByName(className);
	}

	@Override
	public SchoolClass getSelectedClass() {
		return this.selectedClass;
	}

	@Override
	public void teacherAddToActDo(Teacher teacher, double value) {
		teacher.addToActDo(value);
		notifyObservers();
	}
	

}
