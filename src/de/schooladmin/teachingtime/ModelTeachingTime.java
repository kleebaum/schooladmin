package de.schooladmin.teachingtime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

import de.schooladmin.Model;
import de.schooladmin.Parser;
import de.schooladmin.ParserInterface;
import de.schooladmin.Teacher;

public class ModelTeachingTime extends Model implements ModelTeachingTimeInterface {

	public ModelTeachingTime() {
		this.teachers = new ArrayList<Teacher>();
	}
	
	@Override
	public void initialize(){
		super.initialize();
		
		String fileTeachers = prop.get("Teachers").toString();
		initTeachers(fileTeachers);		
	}
	
	@Override
	public void initTeachers(String fileTeachers) {
		ArrayList<String> header = new ArrayList<String>();
		header.add("Nachname");
		header.add("Vorname");
		header.add("Abk");
		header.add("Geschlecht");
		header.add("Geb.Datum");
		header.add("Sollstunden");
		header.add("Schulform");
//		header.add("Faecher");
//		header.add("Teilzeit");
//		header.add("Arbeitskonto");
//		header.add("Std.wiss.Unterricht");
//		header.add("Behinderung.Erm.");
//		header.add("Alters.Erm");
//		header.add("Ausgleich.x");
//		header.add("Ausgleich.v");
//		header.add("UeD");
//		header.add("SL");
//		header.add("P");
//		header.add("Fkt");
//		header.add("So");
//		header.add("Anr.-Text");
//		header.add("Soll");
//		header.add("Mob");
//		header.add("E1");
//		header.add("E2");
//		header.add("S1HJ");
		ParserInterface nameListParser = new Parser(fileTeachers, "[\\s]*;[\\s]*", StandardCharsets.ISO_8859_1, 1, header);
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
			
			teachers.add(teacher);
			teacherAbbrMap.put(abbr, teacher);
			try {
				String gender = line.get(3);
				String birthday = line.get(4);
				double toDo = Double.parseDouble(line.get(5).trim());
				String schoolType = line.get(6);
				String subjects = line.get(7);
				teacher.setGender(gender);
				teacher.setBirthday(birthday);
				teacher.setToDo(toDo);
				teacher.setSchoolType(schoolType);
				teacher.setSubjects(subjects);
			} catch (IndexOutOfBoundsException | NumberFormatException e) {
				setError("Die Lehrerdaten Datei ist unvollst\u00e4ndig.");
			}
			lineCount++;
		}
	}
}
