package de.schooladmin.roomalloc;

import de.schooladmin.ControllerInterface;

/**
 * Interface for Controller of Model-View-Controller Pattern
 * 
 * @author Anja Kleebaum
 *
 */
public interface ControllerRoomAllocInterface extends ControllerInterface {
	
	void setSelectedTime(int selectedHour, int selectedDay); 

}
