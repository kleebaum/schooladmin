package de.schooladmin.roomalloc;

import de.schooladmin.ControllerInterface;
import de.schooladmin.Room;

/**
 * Interface for Controller of Model-View-Controller Pattern
 * 
 * @author Anja Kleebaum
 *
 */
public interface ControllerRoomAllocInterface extends ControllerInterface {
	
	void setSelectedTime(int selectedHour, int selectedDay); 
	void setSelectedRoom(Room selectedRoom);

}
