package de.schooladmin.deployment;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import de.schooladmin.Model;
import de.schooladmin.Parser;
import de.schooladmin.ParserInterface;
import de.schooladmin.SchoolClass;
import de.schooladmin.SchoolGroup;
import de.schooladmin.SchoolSubject;
import de.schooladmin.SchoolType;
import de.schooladmin.Teacher;

/**
 * @author Anja Kleebaum
 *
 */
public class ModelDeployment extends Model implements ModelDeploymentInterface {

	public ModelDeployment() {
		super();
	}

	@Override
	public void initialize() {
		super.initialize();

		String fileTeachers = prop.get("Teachers").toString();
		initTeachers(fileTeachers);

		for (String className : prop.get("SchoolClasses").toString().split("\\s*,\\s*")) {
			className = className.trim();
			ArrayList<SchoolSubject> localSubjects = initSchoolClasses(className);
			SchoolClass localClass = new SchoolClass(className, localSubjects);
			school.getClasses().add(localClass);
		}
	}

	public ArrayList<SchoolSubject> initSchoolClasses(String schoolClassName) {
		ArrayList<SchoolSubject> subjects = new ArrayList<SchoolSubject>();
		ArrayList<String> header = new ArrayList<String>();
		header.add("subject");
		header.add("group");
		header.add("teacher_on_subject");
		header.add("hours_on_subject");
		ParserInterface groupListParser = new Parser("input/" + schoolClassName + ".csv", ";|,",
				StandardCharsets.ISO_8859_1, 1, header);
		try {
			groupListParser.processLineByLine();
		} catch (IOException | java.util.NoSuchElementException e) {
			System.out.println("File not found or corrupt.");
			e.printStackTrace();
		}
		ArrayList<ArrayList<String>> groupList = groupListParser.getTable();

		int lineCount = 0;
		SchoolSubject localSubject = new SchoolSubject("");
		ArrayList<SchoolGroup> groups;

		for (ArrayList<String> line : groupList) {
			if (lineCount == 0) {
				lineCount++;
				continue;
			}
			String subject = line.get(0);
			String group = line.get(1);
			String abbr = line.get(2);
			if(abbr.equals(" ") || abbr == null) {
				abbr = "";
			}
			int hoursOnSubject = Integer.parseInt(line.get(3).trim());
			Teacher teacher = school.getTeacherByAbbr(abbr);
			SchoolGroup localGroup = null;
			if (teacher == null) {
				if (abbr.equals(""))
					teacher = new Teacher("Zuordnung fehlt", "", "");
				else
					teacher = new Teacher("Lehrer fehlt in LehrerDaten.csv", "", abbr);
				school.getTeachers().add(teacher);
				school.getTeacherAbbrMap().put(abbr, teacher);
				teacher.initSchoolGroups();
			}
			localGroup = new SchoolGroup(group, hoursOnSubject, teacher);
			teacher.addToActDo(hoursOnSubject);
			teacher.getSchoolGroups().add(localGroup);

			if (subject.equals(localSubject.getName())) {
				localSubject.getSchoolGroups().add(localGroup);

			} else {
				localSubject = new SchoolSubject(subject);
				groups = new ArrayList<SchoolGroup>();
				groups.add(localGroup);
				localSubject.setSchoolGroups(groups);
				subjects.add(localSubject);
			}
			localGroup.setSubject(localSubject);

			lineCount++;
		}
		return subjects;
	}

	public void initTeachers(String fileTeachers) {
		ArrayList<String> header = new ArrayList<String>();
		header.add("Nachname");
		header.add("Vorname");
		header.add("Abk");
		header.add("Geschlecht");
		header.add("Geb.Datum");
		header.add("Sollstunden");
		header.add("Schultyp");
		header.add("Faecher");
		ParserInterface nameListParser = new Parser(fileTeachers, "[\\s]*;|,[\\s]*", StandardCharsets.ISO_8859_1, 1,
				header);
		try {
			nameListParser.processLineByLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		ArrayList<ArrayList<String>> nameList = nameListParser.getTable();
		int lineCount = 0;
		for (ArrayList<String> line : nameList) {
			if (lineCount == 0) {
				lineCount++;
				continue;
			}
			String surname = line.get(0);
			String firstname = line.get(1);
			String abbr = line.get(2);
			Teacher teacher = new Teacher(surname, firstname, abbr);
			school.getTeachers().add(teacher);
			school.getTeacherAbbrMap().put(abbr, teacher);
			teacher.initSchoolGroups();
			try {
				String schoolTypeString = line.get(6);
				double toDo = Double.parseDouble(line.get(5).trim());
				String subjects = line.get(7);
				teacher.setToDo(toDo);
				SchoolType schoolType = school.getSchoolTypeByAbbr(schoolTypeString);
				if (schoolType == null) {
					schoolType = new SchoolType(schoolTypeString, schoolTypeString, 0.0);
					teacher.setSchoolType(schoolType);
				} else {
					teacher.setSchoolType(schoolType);
				}						
				teacher.setSubjects(subjects);
			} catch (IndexOutOfBoundsException | NumberFormatException e) {
				setError("Die Lehrerdaten Datei ist unvollst\u00e4ndig.");
			}
			lineCount++;
		}
	}

	@Override
	public void exportSchoolClassToCVS(SchoolClass schoolClass, boolean fileChooser) {
		ArrayList<String> content = new ArrayList<String>();

		ArrayList<SchoolSubject> subjects = schoolClass.getSubjects();

		for (SchoolSubject subject : subjects) {
			ArrayList<SchoolGroup> groups = subject.getSchoolGroups();
			for (SchoolGroup group : groups) {
				content.add(subject.getName() + ";" + group.getName() + ";" + group.getTeacherAbbr() + ";"
						+ group.getHoursOnSubject());
			}
		}
		exportToCVS("input/", schoolClass.getName() + ".csv", "Fach;Gruppe;Lehrer;Stunden;", content, fileChooser);
	}

	@Override
	public void exportTeacherOverviewToCVS(boolean fileChooser) {
		ArrayList<String> content = new ArrayList<String>();

		for (Teacher teacher : school.getTeachers()) {
			content.add(teacher.getSurname() + ";" + teacher.getFirstname() + ";" + teacher.getToDo() + ";"
					+ teacher.getActDo() + ";");
		}

		exportToCVS("", "Vergleich-Ist-Soll.csv", "Nachname;Vorname;Soll;Ist;", content, fileChooser);
	}
}
