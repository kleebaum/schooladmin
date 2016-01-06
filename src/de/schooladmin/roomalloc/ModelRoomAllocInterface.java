package de.schooladmin.roomalloc;

import java.util.ArrayList;

import de.schooladmin.ModelInterface;
import de.schooladmin.Room;
import de.schooladmin.SchoolClass;
import de.schooladmin.SchoolTime;
import de.schooladmin.Teacher;

/**
 * Interface for Model of Model-View-Controller Pattern
 * 
 * @author Anja Kleebaum
 *
 */
public interface ModelRoomAllocInterface extends ModelInterface {

	// size of buildings (meter)
	final int AltbauWidth = 128;
	final int AltbauLength = 88;
	final int NeubauWidth = 42;
	final int NeubauLength = 40;

	/**
	 * reads room data from file and creates objects of class Room
	 * 
	 * @param fileName
	 * @param fileTimeTableRooms
	 */
	void initRooms(String fileName, String fileTimeTableRooms);

	/**
	 * reads teacher data from file and creates objects of class Teacher
	 * 
	 * @param fileName
	 */
	void initTeachers(String fileTeachers, String fileTimeTableTeachers);

	/**
	 * reads school class data from file and creates objects of class
	 * SchoolClass
	 */
	void initClasses(String fileName);

	/**
	 * reads time data from file and creates objects of class SchoolTimes
	 */
	void initTimes(String fileName);

	/**
	 * sets selected day
	 * 
	 * @param selectedDay
	 */
	void setSelectedDay(int selectedDay);

	/**
	 * sets selected hour
	 * 
	 * @param selectedHour
	 */
	void setSelectedHour(int selectedHour);

	/**
	 * sets both, selected day and hour and notifies observers
	 * 
	 * @param selectedDay
	 * @param selectedHour
	 */
	void setSelectedTime(int selectedDay, int selectedHour);

	/**
	 * 
	 * @return selected hour
	 */
	int getSelectedHour();

	/**
	 * 
	 * @return selected day
	 */
	int getSelectedDay();
	
	/**
	 * 
	 * @return name of selected day (Montag, Dienstag, Mittwoch, ...)
	 */
	String getSelectedDayName();

	/**
	 * sets selected room
	 * 
	 * @param selectedRoom
	 */
	void setSelectedRoom(Room selectedRoom);

	/**
	 * 
	 * @return selected room
	 */
	Room getSelectedRoom();

	/**
	 * sets selected class
	 * 
	 * @param selectedClass
	 */
	void setSelectedClass(SchoolClass selectedClass);

	/**
	 * 
	 * @return selected class
	 */
	SchoolClass getSelectedClass();

	/**
	 * initializes room list
	 * 
	 * @param rooms
	 */
	void setRooms(ArrayList<Room> rooms);

	/**
	 * 
	 * @return list of rooms
	 */
	ArrayList<Room> getRooms();

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

	/**
	 * initializes school times
	 * 
	 * @param times
	 *            list of school times
	 */
	void setTimes(ArrayList<SchoolTime> times);

	/**
	 * 
	 * @return list of school times
	 */
	ArrayList<SchoolTime> getTimes();
	
	/**
	 * returns the teacher abbreviation allocating the room named 'roomName' at selected hour
	 * @param roomName name of a room
	 * @return teacher abbreviation
	 */
	String getRoomAllocationTeacherAbbr(String roomName);

	/**
	 * returns the teacher allocating the room named 'roomName' at selected hour
	 * and day
	 * 
	 * @param roomName
	 *            name of a room
	 * @return teacher
	 */
	Teacher getRoomAllocationTeacher(String roomName);

	/**
	 * return the subject taught in the room named 'roomName' at selected hour and day
	 * @param roomName name of room
	 * @return subject
	 */
	String getRoomAllocationSubject(String roomName);

	/**
	 * returns room named 'roomName'
	 * @param roomName name of room
	 * @return room
	 */
	Room getRoomByName(String roomName);
	
	/**
	 * returns name of school class allocating the room named 'roomName' at selected hour
	 * @param roomName name of a room
	 * @return name of school class
	 */
	String getRoomAllocationClassName(String roomName);

	/**
	 * returns school class allocating the room named 'roomName' at selected hour
	 * @param roomName name of a room
	 * @return school class
	 */
	SchoolClass getRoomAllocationClass(String roomName);
}
