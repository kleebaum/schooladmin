package de.schooladmin.deployment;

import java.io.FileNotFoundException;

public class Deployment {

	public static void main(String[] args) throws FileNotFoundException {
		ModelDeploymentInterface model = new ModelDeployment();
		ControllerDeploymentInterface controller = new ControllerDeployment(model);
		ViewDeploymentInterface view = new ViewDeployment(controller, model);
		controller.addView(view);
	}
}