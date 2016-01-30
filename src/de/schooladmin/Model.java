package de.schooladmin;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Properties;

import javax.swing.JFileChooser;

public class Model implements ModelInterface {
	
	protected Properties prop;
	protected String name;
	
	protected School school;
	private Teacher selectedTeacher;
	private Teacher oldSelectedTeacher;
	private SchoolClass selectedClass;
	private SchoolGroup selectedGroup;
	private SchoolSubject selectedSubject;
	private Room selectedRoom;

	private String error = "";
	
	// list containing observing views
	public ArrayList<ObserverInterface> observers = new ArrayList<ObserverInterface>();
	
	public Model() {
		this.setSchool(new School());
		this.selectedClass = null;
		this.selectedTeacher = null;
		this.oldSelectedTeacher = null;
		this.selectedGroup = null;
		this.selectedRoom = null;
		this.selectedSubject = null;
	}

	public Properties readConfig(String fileName) {
		prop = new Properties();
		InputStream input = null;

		try {

			input = new FileInputStream(fileName);

			// load a properties file
			prop.load(input);
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return prop;
	}

	@Override
	public void registerObserver(ObserverInterface o) {
		observers.add(o);
	}

	@Override
	public void removeObserver(ObserverInterface o) {
		observers.remove(o);
	}

	@Override
	public void notifyObservers() {
		for (int i = 0; i < observers.size(); i++) {
			ObserverInterface observer = observers.get(i);
			observer.update();
		}
	}

	@Override
	public void initialize() {
		prop = readConfig(ConfigFile);
		this.name = prop.get("Name").toString();
	}
	
	@Override
	public String getError() {
		return error;
	}
	
	@Override
	public void setError(String error) {
		this.error = error;
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public void exportToCVS(String folder, String fileName, String header, ArrayList<String> content,
			boolean fileChooser) {
		try {
			final JFileChooser fc = new JFileChooser();

			fc.setCurrentDirectory(new File(folder));
			File file = new File(folder + fileName);
			fc.setSelectedFile(file);
			int retrival = JFileChooser.APPROVE_OPTION;
			if (fileChooser) {
				retrival = fc.showSaveDialog(null);
			}
			if (retrival == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				file.createNewFile();

				BufferedWriter bw = new BufferedWriter(
						(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.ISO_8859_1)));
				bw.write(header);
				bw.newLine();

				for (int i = 0; i < content.size(); i++) {
					bw.write(content.get(i));
					bw.newLine();
				}
				bw.flush();
				bw.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	@Override
	public String getFileTeachers() {
		return prop.get("Teachers").toString();
	}

	@Override
	public School getSchool() {
		return school;		
	}

	@Override
	public void setSchool(School school) {
		this.school = school;
	}
	
	@Override
	public void setSelectedTeacher(String abbr) {
		this.oldSelectedTeacher = this.selectedTeacher;
		this.selectedTeacher = this.school.getTeacherByAbbr(abbr);
		notifyObservers();
	}
	
	@Override
	public Teacher getSelectedTeacher() {
		return selectedTeacher;
	}

	@Override
	public void setSelectedTeacher(Teacher selectedTeacher) {
		this.oldSelectedTeacher = this.selectedTeacher;
		this.selectedTeacher = selectedTeacher;
		notifyObservers();
	}

	@Override
	public SchoolClass getSelectedClass() {
		return selectedClass;
	}

	@Override
	public void setSelectedClass(SchoolClass selectedClass) {
		this.selectedClass = selectedClass;
		notifyObservers();
	}
	
	@Override
	public void setSelectedClass(String selectedClassName) {
		this.selectedClass = this.school.getClassByName(selectedClassName);
		notifyObservers();
	}
	
	@Override
	public Teacher getOldSelectedTeacher() {
		return oldSelectedTeacher;
	}

	@Override
	public void setOldSelectedTeacher(Teacher oldSelectedTeacher) {
		this.oldSelectedTeacher = oldSelectedTeacher;
		notifyObservers();
	}

	@Override
	public SchoolSubject getSelectedSubject() {
		return selectedSubject;
	}

	@Override
	public void setSelectedSubject(SchoolSubject selectedSubject) {
		this.selectedSubject = selectedSubject;
		notifyObservers();
	}
	
	@Override
	public SchoolGroup getSelectedGroup() {
		return selectedGroup;
	}
	
	@Override
	public void setSelectedGroup(SchoolGroup selectedGroup) {
		this.selectedGroup = selectedGroup;
		notifyObservers();
	}
	
	@Override
	public Room getSelectedRoom() {
		return selectedRoom;
	}
	
	@Override
	public void setSelectedRoom(Room selectedRoom) {
		this.selectedRoom = selectedRoom;
		notifyObservers();
	}


}
