package de.schooladmin.roomalloc;

import java.io.FileNotFoundException;

import de.schooladmin.Controller;
import de.schooladmin.Room;
import de.schooladmin.SchoolClass;
import de.schooladmin.Teacher;

/**
 * @author Anja Kleebaum
 *
 */
public class ControllerRoomAlloc extends Controller implements ControllerRoomAllocInterface {

	private ModelRoomAllocInterface model;
	
	public ControllerRoomAlloc(final ModelRoomAllocInterface model) throws FileNotFoundException {
		super(model);
		this.model = model;
		model.initialize();
	}

	@Override
	public void setSelectedTime(int selectedHour, int selectedDay) {
		model.setSelectedTime(selectedDay, selectedHour);
	}

	@Override
	public void setSelectedRoom(Room selectedRoom) {
		model.setSelectedRoom(selectedRoom);
	}

	@Override
	public void setSelectedClass(SchoolClass selectedClass) {
		model.setSelectedClass(selectedClass);
	}
	
	@Override
	public void setSelectedTeacher(Teacher selectedTeacher) {
		model.setSelectedTeacher(selectedTeacher);
	}

}
