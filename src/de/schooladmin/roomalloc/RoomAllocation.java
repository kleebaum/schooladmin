package de.schooladmin.roomalloc;

import java.io.FileNotFoundException;

public class RoomAllocation {

	public static void main(String[] args) throws FileNotFoundException {
		ModelRoomAllocInterface model = new ModelRoomAlloc();		
		ControllerRoomAllocInterface controller = new ControllerRoomAlloc(model);
		ViewRoomAllocInterface view = new ViewRoomAlloc(controller, model);
		controller.addView(view);		
	}
}