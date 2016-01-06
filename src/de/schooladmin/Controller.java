package de.schooladmin;

public class Controller implements ControllerInterface {
	private ModelInterface model;
	private ViewInterface view;

	public Controller(ModelInterface model) {
	}
	
	@Override
	public void addView(ViewInterface view) {
		this.view = view;
		view.initView();
	}

	@Override
	public void errorMessage(String message) {
		this.view.errorWindow(message);		
	}
	
	@Override
	public void setError(String message) {
		model.setError(message);
	}

	@Override
	public void setSelectedTeacher(Teacher selectedTeacher) {
		model.setSelectedTeacher(selectedTeacher);
	}
	
}
