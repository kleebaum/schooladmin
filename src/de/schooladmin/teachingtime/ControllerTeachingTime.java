package de.schooladmin.teachingtime;

import java.io.FileNotFoundException;

import de.schooladmin.Controller;

public class ControllerTeachingTime extends Controller implements ControllerTeachingTimeInterface {
	
	//private ModelTeachingTimeInterface model;

	public ControllerTeachingTime(ModelTeachingTimeInterface model) throws FileNotFoundException {
		super(model);
		//this.model = model;
	}

}
