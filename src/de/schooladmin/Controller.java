package de.schooladmin;

public class Controller implements ControllerInterface {
	private ModelInterface model;
	private ViewInterface view;

	public Controller(ModelInterface model) {
		this.model = model;
		model.initialize();
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
	
	@Override
	public void setSelectedTeacher(String abbr) {
		model.setSelectedTeacher(abbr);
	}
	
	@Override
	public void setSelectedClass(SchoolClass selectedClass) {
		model.setSelectedClass(selectedClass);
	}
	
	@Override
	public void setSelectedClass(String selectedClassName) {
		model.setSelectedClass(selectedClassName);
	}
	
	@Override
	public void setSelectedGroup(SchoolGroup selectedGroup) {
		model.setSelectedGroup(selectedGroup);
	}
	
	@Override
	public void setSelectedSubject(SchoolSubject selectedSubject) {
		model.setSelectedSubject(selectedSubject);
	}

	@Override
	public void teacherAddToActDo(Teacher teacher, double value) {
		teacher.addToActDo(value);
		model.notifyObservers();
	}
	
	@Override
	public void setSelectedRoom(Room selectedRoom) {
		model.setSelectedRoom(selectedRoom);
	}
	
}
