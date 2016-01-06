package de.schooladmin.teachingtime;

import java.io.FileNotFoundException;

import de.schooladmin.Controller;
import de.schooladmin.Teacher;

public class ControllerTeachingTime extends Controller implements ControllerTeachingTimeInterface {

	private ModelTeachingTimeInterface model;
	
	public ControllerTeachingTime(ModelTeachingTimeInterface model) throws FileNotFoundException {
		super(model);
		this.model = model;
		model.initialize();
	}
	
	@Override
	public void setSelectedTeacher(Teacher selectedTeacher) {
		model.setSelectedTeacher(selectedTeacher);
	}
	
	@Override
	public void setError(String message) {
		model.setError(message);
	}

}
