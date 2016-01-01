package de.schooladmin;

/**
 * Class for school times
 * 
 * @author Anja Kleebaum
 *
 */
public class SchoolTime {
	private int referencedHour;
	private long start, end;
	private String name;

	public SchoolTime(String name, long start, long end) {
		this.name = name;
		this.start = start;
		this.end = end;
		if (name.substring(0, 1).matches("\\d")) {
			this.referencedHour = Integer.parseInt(name.split("\\.")[0]);
		} else {
			 this.referencedHour = 0;
		}
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public long getEnd() {
		return end;
	}

	public void setEnd(long end) {
		this.end = end;
	}

	public int getReferencedHour() {
		return referencedHour;
	}

	public void setReferencedHour(int referencedHour) {
		this.referencedHour = referencedHour;
	}

}
