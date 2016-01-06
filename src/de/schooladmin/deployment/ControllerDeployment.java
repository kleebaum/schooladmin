package de.schooladmin.deployment;

import java.io.FileNotFoundException;

import de.schooladmin.Controller;
import de.schooladmin.Teacher;

public class ControllerDeployment extends Controller implements ControllerDeploymentInterface {
	
	private ModelDeploymentInterface model;
	
	public ControllerDeployment(ModelDeploymentInterface model) throws FileNotFoundException {
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
