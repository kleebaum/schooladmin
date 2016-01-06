package de.schooladmin.teachingtime;

import de.schooladmin.View;

public class ViewTeachingTime extends View implements ViewTeachingTimeInterface {
	
	private static final long serialVersionUID = 1L;
	ModelTeachingTimeInterface model;
	ControllerTeachingTimeInterface controller;
	
	public ViewTeachingTime(ControllerTeachingTimeInterface controller, ModelTeachingTimeInterface model) {
		super(controller, model);
		this.model = model;
		this.controller = controller;
	}

}
