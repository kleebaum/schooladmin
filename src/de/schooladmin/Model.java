package de.schooladmin;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Properties;

public class Model implements ModelInterface {
	
	protected Properties prop;
	protected ArrayList<Teacher> teachers;
	protected HashMap<String, Teacher> teacherAbbrMap = new HashMap<String, Teacher>(); 
	private String error = "";
	
	protected Teacher selectedTeacher = null;
	protected SchoolClass selectedClass;

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

	// list containing observing views
	public ArrayList<ObserverInterface> observers = new ArrayList<ObserverInterface>();

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
		// TODO Auto-generated method stub

	}
	
	@Override
	public Teacher getTeacherByAbbr(String abbr) {
		if (abbr.equals(""))
			return null;
		return teacherAbbrMap.get(abbr);
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
		return "GSH Smart Admin: ";
	}

}
