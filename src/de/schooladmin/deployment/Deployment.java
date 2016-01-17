package de.schooladmin.deployment;

public class Deployment {

	public static void main(String[] args) {
		ModelDeploymentInterface model = new ModelDeployment();
		ControllerDeploymentInterface controller = new ControllerDeployment(model);
		ViewDeploymentInterface view = new ViewDeployment(controller, model);
		controller.addView(view);
	}
}