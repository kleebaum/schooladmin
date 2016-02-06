package de.schooladmin.teachingtime;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;

import de.schooladmin.Model;
import de.schooladmin.Parser;
import de.schooladmin.ParserInterface;
import de.schooladmin.SchoolType;
import de.schooladmin.Teacher;

public class ModelTeachingTime extends Model implements ModelTeachingTimeInterface {

	private LinkedHashMap<String, Integer> teacherDataMap;
	public ArrayList<String> teacherDataList;
	public String fileTeachersStatistics;

	public ModelTeachingTime() {
		this.teacherDataMap = new LinkedHashMap<String, Integer>();
	}

	@Override
	public void initialize() {
		super.initialize();
		
		String[] schoolTypes = prop.get("SchoolTypes").toString().split("\\s*,\\s*");
		String[] schoolTypeHours = prop.get("SchoolTypeHours").toString().split("\\s*,\\s*");
		String[] schoolTypeNames = prop.get("SchoolTypesName").toString().split("\\s*,\\s*");
		String[] partialRetirement = prop.get("PartialRetirement").toString().split("\\s*,\\s*");
		String[] scientificLessons = prop.get("ScientificLessons").toString().split("\\s*,\\s*");
		initSchoolTypes(schoolTypes, schoolTypeHours, schoolTypeNames, partialRetirement, scientificLessons);

		String fileTeachers = prop.get("Teachers").toString();
		//String fileTimeTableTeachers = prop.get("TimeTableTeachers").toString();
		fileTeachersStatistics = prop.getProperty("TeachersStatistics");
		
		String[] plusMinus = prop.getProperty("PlusMinus").toString().split("\\s*,\\s*");

		teacherDataList = new ArrayList<String>();
		teacherDataList.add("Nachname");
		teacherDataList.add("Vorname");
		teacherDataList.add("Abk\u00fcrzung");
		teacherDataList.add("Geschlecht");
		teacherDataList.add("Geburtsdatum");
		teacherDataList.add("Schulform");
		teacherDataList.add("Std.wiss.Unterricht");
		teacherDataList.add("Teilzeit/Vollzeit");
		teacherDataList.add("Mob");
		teacherDataList.add("Soll 1. HJ");
		teacherDataList.add("Einsatz 1. HJ");
		teacherDataList.add("Soll 2. HJ");
		teacherDataList.add("Einsatz 2. HJ");
		teacherDataList.add("Anr. Erkl\u00e4rungen");
		for (int i = 0; i < teacherDataList.size(); i++) {
			teacherDataMap.put(teacherDataList.get(i), i);
		}
		for (int i = 0; i < plusMinus.length; i++) {
			teacherDataList.add(plusMinus[i]);
			teacherDataMap.put(plusMinus[i], i);
		}

		initTeachers(teacherDataList, fileTeachers, null);
	}

	public void initSchoolTypes(String[] schoolTypes, String[] schoolTypeHours, String[] schoolTypeNames,
			String[] partialRetirement, String[] scientificLessons) {

		ArrayList<SchoolType> schoolTypesList = new ArrayList<SchoolType>();
		SchoolType emptyType1 = new SchoolType("", "-", 0.0);
		SchoolType emptyType2 = new SchoolType(" ", "-", 0.0);
		schoolTypesList.add(emptyType1);
		schoolTypesList.add(emptyType2);
		school.getSchoolTypeMap().put("", emptyType1);
		school.getSchoolTypeMap().put(" ", emptyType2);

		for (int i = 0; i < schoolTypes.length; i++) {
			SchoolType newType = new SchoolType(schoolTypes[i], schoolTypeNames[i],
					Double.parseDouble(schoolTypeHours[i]));
			schoolTypesList.add(newType);
			school.getSchoolTypeMap().put(schoolTypes[i], newType);
		}

		for (int i = 0; i < partialRetirement.length; i++) {
			String[] line = partialRetirement[i].split(":");
			SchoolType schoolType = school.getSchoolTypeByAbbr(line[0]);
			if (schoolType != null) {
				for (int j = 1; j < line.length; j = j + 2) {
					schoolType.getPartialRetirementMap().put(Double.parseDouble(line[j]),
							Double.parseDouble(line[j + 1]));
				}
			}
		}
		
		for (int i = 0; i < scientificLessons.length; i++) {
			String[] line = scientificLessons[i].split(":");
			SchoolType schoolType = school.getSchoolTypeByAbbr(line[0]);
			if (schoolType != null) {
				for (int j = 1; j < line.length; j = j + 2) {
					schoolType.getScientificLessonsMap().put(Double.parseDouble(line[j]),
							Double.parseDouble(line[j + 1]));
				}
			}
		}
		
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
			ArrayList<String> teacherData = new ArrayList<String>();

			for (int i = 0; i < teacherDataList.size(); i++) {
				teacherData.add(line.get(i).trim());
			}
			Teacher teacher = new Teacher(teacherData, fileTeachersStatistics);
			teacher.setSurname(line.get(0));
			teacher.setFirstname(line.get(1));
			teacher.setAbbr(line.get(2));
			teacher.setGender(line.get(3));
			teacher.setBirthday(line.get(4));
			String schoolTypeString = line.get(5);
			SchoolType schoolType = school.getSchoolTypeByAbbr(schoolTypeString);
			if (schoolType == null) {
				schoolType = new SchoolType(schoolTypeString, schoolTypeString, 0.0);
				teacher.setSchoolType(schoolType);
			} else {
				teacher.setSchoolType(schoolType);
			}

			school.getTeachers().add(teacher);
			school.getTeacherAbbrMap().put(teacherData.get(2), teacher);
			lineCount++;
		}
	}

	@Override
	public LinkedHashMap<String, Integer> getTeacherDataMap() {
		return this.teacherDataMap;
	}

	@Override
	public ArrayList<String> getTeacherDataList() {
		return this.teacherDataList;
	}
}
