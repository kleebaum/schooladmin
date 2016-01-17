package de.schooladmin.deployment;

import de.schooladmin.Controller;

public class ControllerDeployment extends Controller implements ControllerDeploymentInterface {
	
	//private ModelDeploymentInterface model;

	public ControllerDeployment(ModelDeploymentInterface model) {
		super(model);
		//this.model = model;
	}

}
