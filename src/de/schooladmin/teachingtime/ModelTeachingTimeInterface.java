package de.schooladmin.teachingtime;

import java.util.ArrayList;
import java.util.LinkedHashMap;

import de.schooladmin.ModelInterface;

public interface ModelTeachingTimeInterface extends ModelInterface {

	void initTeachers(ArrayList<String> teacherData, String fileTeachers, String fileTimeTableTeachers);
	LinkedHashMap <String, Integer> getTeacherDataMap();
	ArrayList<String> getTeacherDataList();
}
