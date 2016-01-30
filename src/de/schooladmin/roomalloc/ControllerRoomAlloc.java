package de.schooladmin.roomalloc;

import de.schooladmin.Controller;

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

}
