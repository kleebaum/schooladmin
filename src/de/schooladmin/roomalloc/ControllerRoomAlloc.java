package de.schooladmin.roomalloc;

import de.schooladmin.Controller;
import de.schooladmin.Room;

/**
 * @author Anja Kleebaum
 *
 */
public class ControllerRoomAlloc extends Controller implements ControllerRoomAllocInterface {

	private ModelRoomAllocInterface model;
	
	public ControllerRoomAlloc(final ModelRoomAllocInterface model) {
		super(model);
		this.model = model;
	}

	@Override
	public void setSelectedTime(int selectedHour, int selectedDay) {
		model.setSelectedTime(selectedDay, selectedHour);
	}

	@Override
	public void setSelectedRoom(Room selectedRoom) {
		model.setSelectedRoom(selectedRoom);
	}

}
