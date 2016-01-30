package de.schooladmin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;
/**
 * @author Anja Kleebaum
 *
 */
public class School {
	private ArrayList<Teacher> teachers;
	private ArrayList<SchoolClass> classes;
	private ArrayList<SchoolType> schoolTypes;
	private ArrayList<Room> rooms;
	private ArrayList<SchoolTime> times;
	private ArrayList<SchoolSubject> subjects;
	
	private HashMap<String, Teacher> teacherAbbrMap;
	private HashMap<String, SchoolClass> classNameMap ;
	private HashMap<String, SchoolType> schoolTypeMap;
	private TreeMap<String, SchoolSubject> subjectNameMap;
	
	public School() {
		this.teachers = new ArrayList<Teacher>();
		this.teacherAbbrMap = new HashMap<String, Teacher>();
		
		this.classes = new ArrayList<SchoolClass>();
		this.classNameMap = new HashMap<String, SchoolClass>();

		this.schoolTypes = new ArrayList<SchoolType>();		
		this.schoolTypeMap = new HashMap<String, SchoolType>();		
		
		this.rooms = new ArrayList<Room>();
		
		this.times = new ArrayList<SchoolTime>();
		
		this.subjects = new ArrayList<SchoolSubject>();
		this.subjectNameMap = new TreeMap<String, SchoolSubject>();
	}	
	
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
	
	public SchoolType getSchoolTypeByAbbr(String abbr) {
		if (abbr.equals(""))
			return null;
		return schoolTypeMap.get(abbr);
	}
	
	public Room getRoomByName(String roomName) {
		for (Room room : rooms) {
			if (room.getName().equals(roomName))
				return room;
		}
		return null;
	}
	
	public ArrayList<SchoolClass> getClasses() {
		return classes;
	}
	public void setClasses(ArrayList<SchoolClass> classes) {
		this.classes = classes;
	}
	public ArrayList<SchoolType> getSchoolTypes() {
		return schoolTypes;
	}
	public void setSchoolTypes(ArrayList<SchoolType> schoolTypes) {
		this.schoolTypes = schoolTypes;
	}
	public ArrayList<Teacher> getTeachers() {
		return teachers;
	}
	public void setTeachers(ArrayList<Teacher> teachers) {
		this.teachers = teachers;
	}

	public HashMap<String, Teacher> getTeacherAbbrMap() {
		return teacherAbbrMap;
	}

	public void setTeacherAbbrMap(HashMap<String, Teacher> teacherAbbrMap) {
		this.teacherAbbrMap = teacherAbbrMap;
	}

	public HashMap<String, SchoolClass> getClassNameMap() {
		return classNameMap;
	}

	public void setClassNameMap(HashMap<String, SchoolClass> classNameMap) {
		this.classNameMap = classNameMap;
	}

	public HashMap<String, SchoolType> getSchoolTypeMap() {
		return schoolTypeMap;
	}

	public void setSchoolTypeMap(HashMap<String, SchoolType> schoolTypeMap) {
		this.schoolTypeMap = schoolTypeMap;
	}



	public ArrayList<Room> getRooms() {
		return rooms;
	}

	public void setRooms(ArrayList<Room> rooms) {
		this.rooms = rooms;
	}

	public ArrayList<SchoolTime> getTimes() {
		return times;
	}

	public void setTimes(ArrayList<SchoolTime> times) {
		this.times = times;
	}

	public ArrayList<SchoolSubject> getSubjects() {
		return subjects;
	}

	public void setSubjects(ArrayList<SchoolSubject> subjects) {
		this.subjects = subjects;
	}

	public TreeMap<String, SchoolSubject> getSubjectNameMap() {
		return subjectNameMap;
	}

	public void setSubjectNameMap(TreeMap<String, SchoolSubject> subjectNameMap) {
		this.subjectNameMap = subjectNameMap;
	}

}
