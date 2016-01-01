package de.schooladmin;

/**
 * Class for school groups
 * 
 * @author Anja Kleebaum
 *
 */
public class SchoolGroup {
	private String name;
	private int hoursOnSubject;
	private Teacher teacherOnSubject;
	private SchoolSubject subject;
	private String teacherAbbr;
	public Model model;
	
	public SchoolGroup (String name, int hoursOnSubject, Teacher teacherOnSubject) {
		this.name = name;
		this.hoursOnSubject = hoursOnSubject;
		this.teacherOnSubject = teacherOnSubject;
		try {
		this.setTeacherAbbr(teacherOnSubject.getAbbr());
		} catch (NullPointerException e) {
			
		}
	}
	
	public SchoolGroup (String name, int hoursOnSubject, String teacherAbbr) {		
		this.name = name;
		this.hoursOnSubject = hoursOnSubject;
		this.setTeacherAbbr(teacherAbbr);
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the hours_on_subject
	 */
	public int getHoursOnSubject() {
		return hoursOnSubject;
	}

	/**
	 * @param hours_on_subject the hours_on_subject to set
	 */
	public void setHoursOnSubject(int hoursOnSubject) {
		this.hoursOnSubject = hoursOnSubject;
	}

	/**
	 * @return the teacher_on_subject
	 */
	public Teacher getTeacherOnSubject() {
		return teacherOnSubject;
	}

	/**
	 * @param teacher_on_subject the teacher_on_subject to set
	 */
	public void setTeacherOnSubject(Teacher teacherOnSubject) {
		this.teacherOnSubject = teacherOnSubject;
	}

	/**
	 * @return the teacherAbbr
	 */
	public String getTeacherAbbr() {
		return teacherAbbr;
	}

	/**
	 * @param teacherAbbr the teacherAbbr to set
	 */
	public void setTeacherAbbr(String teacherAbbr) {
		this.teacherAbbr = teacherAbbr;
	}

	/**
	 * @return the subject
	 */
	public SchoolSubject getSubject() {
		return subject;
	}

	/**
	 * @param subject the subject to set
	 */
	public void setSubject(SchoolSubject subject) {
		this.subject = subject;
	}

}
