package de.schooladmin.teachingtime;

import java.io.FileNotFoundException;

public class TeachingTime {

	public static void main(String[] args) throws FileNotFoundException {
		ModelTeachingTimeInterface model = new ModelTeachingTime();
		ControllerTeachingTimeInterface controller = new ControllerTeachingTime(model);
		ViewTeachingTimeInterface view = new ViewTeachingTime(controller, model);
		controller.addView(view);
	}

}
