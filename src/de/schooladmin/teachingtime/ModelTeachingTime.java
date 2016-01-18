package de.schooladmin.teachingtime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import de.schooladmin.Model;
import de.schooladmin.Parser;
import de.schooladmin.ParserInterface;
import de.schooladmin.Teacher;

public class ModelTeachingTime extends Model implements ModelTeachingTimeInterface {
	
	private HashMap <String, Double> schoolTypesMap;
	private LinkedHashMap <String, Integer> teacherDataMap;

	public ModelTeachingTime() {
		this.teachers = new ArrayList<Teacher>();
		this.schoolTypesMap = new HashMap<String, Double>();
		this.teacherDataMap = new LinkedHashMap<String, Integer>();
	}

	@Override
	public void initialize() {
		super.initialize();

		String fileTeachers = prop.get("Teachers").toString();
		String fileTimeTableTeachers = prop.get("TimeTableTeachers").toString();
		String[] teacherData = prop.getProperty("TeacherData").toString().split("\\s*,\\s*");
		
		ArrayList<String> teacherDataList = new ArrayList<String>();
		for (int i = 0; i < teacherData.length; i++) {
			teacherDataList.add(teacherData[i]);
			teacherDataMap.put(teacherData[i], i);
		}

		String[] schoolTypes = prop.get("SchoolTypes").toString().split("\\s*,\\s*");
		String[] schoolTypeHours = prop.get("SchoolTypeHours").toString().split("\\s*,\\s*");

		for (int i = 0; i < schoolTypes.length; i++) {
			schoolTypesMap.put(schoolTypes[i], Double.parseDouble(schoolTypeHours[i]));
		}

		initTeachers(teacherDataList, fileTeachers, fileTimeTableTeachers);
	}

	@Override
	public void initTeachers(ArrayList<String> teacherDataList, String fileTeachers, String fileTimeTableTeachers) {
		ParserInterface nameListParser = new Parser(fileTeachers, "[\\s]*;|,[\\s]*", StandardCharsets.ISO_8859_1, 1,
				teacherDataList);
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

			Teacher teacher = new Teacher(surname, firstname, abbr, fileTimeTableTeachers);

			teachers.add(teacher);
			teacherAbbrMap.put(abbr, teacher);
			try {
				teacher.setGender(line.get(3));
				teacher.setBirthday(line.get(4));
				teacher.setSchoolType(line.get(5));
				for (int i=0; i < teacherDataList.size(); i++) {
					teacher.getTeacherData().add(line.get(i).trim());
				}
//				teacher.partTime = Double.parseDouble(line.get(6).trim());
//				teacher.workAccount = Double.parseDouble(line.get(7).trim());
//				teacher.scientificTime = Double.parseDouble(line.get(8).trim());
//				teacher.handicapTime = Double.parseDouble(line.get(9).trim());
//				teacher.seniorReduction = Double.parseDouble(line.get(10).trim());
//				teacher.compensateMidTerm = Double.parseDouble(line.get(11).trim());
//				teacher.compensateYear = Double.parseDouble(line.get(12).trim());
//				teacher.plusMinus = Double.parseDouble(line.get(13).trim());
//				teacher.directorTime = Double.parseDouble(line.get(14).trim());
//				teacher.committeTime = Double.parseDouble(line.get(15).trim());
//				teacher.functionTime = Double.parseDouble(line.get(16).trim());
//				teacher.otherTimeDif = Double.parseDouble(line.get(17).trim());
//				teacher.teachingTimeText = line.get(18);
				teacher.setToDo(Double.parseDouble(line.get(19).trim()));

			} catch (IndexOutOfBoundsException | NumberFormatException e) {
				setError("Die Lehrerdaten Datei ist unvollst\u00e4ndig.");
			}
			lineCount++;
		}
	}
	
	@Override
	public HashMap <String, Double> getSchoolTypesMap() {
		return this.schoolTypesMap;
	}
	
	@Override
	public LinkedHashMap <String, Integer> getTeacherDataMap() {
		return this.teacherDataMap;
	}
}
