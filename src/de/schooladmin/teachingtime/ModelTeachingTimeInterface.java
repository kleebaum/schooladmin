package de.schooladmin.teachingtime;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;

import de.schooladmin.ModelInterface;

public interface ModelTeachingTimeInterface extends ModelInterface {

	HashMap <String, Double> getSchoolTypesMap();
	void initTeachers(ArrayList<String> teacherData, String fileTeachers, String fileTimeTableTeachers);
	LinkedHashMap <String, Integer> getTeacherDataMap();
}
