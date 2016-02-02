package de.schooladmin;

import java.util.HashMap;

public class SchoolType {

	private String abbr;
	private String name;
	private Double teachingTime;
	private HashMap<Double, Double> partialRetirementMap;
	private HashMap<Double, Double> scientificLessonsMap;

	public SchoolType(String abbr, String name, Double teachingTime) {
		this.abbr = abbr;
		this.name = name;
		this.teachingTime = teachingTime;
		this.partialRetirementMap = new HashMap<Double, Double>();
		this.scientificLessonsMap = new HashMap<Double, Double>();
	}

	public Double getPartialRetirement(int age) {
		if (age <= 30)
			return 0.0;
		for (int i = age; i > 30; i--) {
			Double partialRetirement = this.partialRetirementMap.get((double) i);
			// System.out.println(i + ": " + partialRetirement);
			if (partialRetirement != null)
				return partialRetirement;
		}
		return 0.0;
	}

	public Double getTeachingTimePerScLes(double scientificLessons) {
		if (this.scientificLessonsMap.size() == 0)
			return this.getTeachingTime();
		for (double i = scientificLessons; i < 31; i++) {
			Double teachingTime = this.scientificLessonsMap.get(i);
			if (teachingTime != null)
				return teachingTime;
		}

		return this.getTeachingTime();
	}

	public String getAbbr() {
		return abbr;
	}

	public void setAbbr(String abbr) {
		this.abbr = abbr;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public HashMap<Double, Double> getPartialRetirementMap() {
		return partialRetirementMap;
	}

	public void setPartialRetirementMap(HashMap<Double, Double> partialRetirementMap) {
		this.partialRetirementMap = partialRetirementMap;
	}

	public Double getTeachingTime() {
		return teachingTime;
	}

	public void setTeachingTime(Double teachingTime) {
		this.teachingTime = teachingTime;
	}

	public HashMap<Double, Double> getScientificLessonsMap() {
		return scientificLessonsMap;
	}

	public void setScientificLessonsMap(HashMap<Double, Double> scientificLessonsMap) {
		this.scientificLessonsMap = scientificLessonsMap;
	}

}
