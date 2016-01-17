package de.schooladmin.roomalloc;

public class RoomAllocation {

	public static void main(String[] args) {
		ModelRoomAllocInterface model = new ModelRoomAlloc();		
		ControllerRoomAllocInterface controller = new ControllerRoomAlloc(model);
		ViewRoomAllocInterface view = new ViewRoomAlloc(controller, model);
		controller.addView(view);		
	}
}