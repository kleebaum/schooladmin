package de.schooladmin.roomalloc;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import de.schooladmin.Model;
import de.schooladmin.Parser;
import de.schooladmin.ParserInterface;
import de.schooladmin.Room;
import de.schooladmin.SchoolClass;
import de.schooladmin.SchoolSubject;
import de.schooladmin.SchoolTime;
import de.schooladmin.Teacher;

/**
 * Model of Model-View-Controller Pattern
 * 
 * @author Anja Kleebaum
 *
 */
public class ModelRoomAlloc extends Model implements ModelRoomAllocInterface {

	// current model state
	private int selectedHour;
	private int selectedDay;

	private BufferedReader in;

	@Override
	public void initialize() {
		super.initialize();

		String fileTeachers = prop.get("Teachers").toString();
		String fileRooms = prop.get("Rooms").toString();
		String fileTimes = prop.get("Times").toString();
		String fileTimeTableClasses = prop.get("TimeTableSchoolClasses").toString();
		String fileTimeTableRooms = prop.get("TimeTableRooms").toString();
		String fileTimeTableTeachers = prop.get("TimeTableTeachers").toString();

		initRooms(fileRooms, fileTimeTableRooms);
		initTeachers(fileTeachers, fileTimeTableTeachers);
		initClasses(fileTimeTableClasses);
		initTimes(fileTimes);
		initSubjects();

		Date date = new Date();
		Calendar calendar = GregorianCalendar.getInstance(); // creates a new
																// calendar
																// instance
		calendar.setTime(date);
		SimpleDateFormat dayOfWeekFormat = new SimpleDateFormat("u");
		int dayOfWeek = Integer.parseInt(dayOfWeekFormat.format(date));
		if (dayOfWeek > 0 && dayOfWeek < 6) {
			this.selectedDay = dayOfWeek;
		} else {
			this.selectedDay = 1;
		}

		SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm");
		this.selectedHour = 1;
		try {
			Date timeNow = timeFormat.parse(calendar.get(Calendar.HOUR_OF_DAY) + ":" + calendar.get(Calendar.MINUTE));
			for (int i = 0; i < school.getTimes().size(); i++) {
				if (school.getTimes().get(i).getStart() <= timeNow.getTime()
						&& school.getTimes().get(i).getEnd() > timeNow.getTime()) {
					int referencedHour = school.getTimes().get(i).getReferencedHour();
					if (referencedHour != 0) {
						this.selectedHour = referencedHour;

					} else if (school.getTimes().get(i + 1) != null) {

						this.selectedHour = school.getTimes().get(i + 1).getReferencedHour();
					}
					break;
				}
			}

		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void initRooms(String fileName, String fileTimeTableRooms) {
		ArrayList<String> header = new ArrayList<String>();
		header.add("Name");
		header.add("Plaetze");
		header.add("x-Koordinate");
		header.add("y-Koordinate");
		header.add("x-Laenge");
		header.add("y-Laenge");
		header.add("Raumgruppe");
		header.add("Gebaeudegruppe");
		ParserInterface roomListParser = new Parser(fileName, ";", StandardCharsets.UTF_8, 1, header);
		try {
			roomListParser.processLineByLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<ArrayList<String>> roomArrayList = roomListParser.getTable();
		int lineCount = 0;
		for (ArrayList<String> line : roomArrayList) {
			if (lineCount == 0) {
				lineCount++;
				continue;
			}
			String name = line.get(0);
			String spaceText = line.get(1);
			int space = !spaceText.matches("\\d+") ? 0 : Integer.parseInt(spaceText);
			int x = Integer.parseInt(line.get(2));
			int y = Integer.parseInt(line.get(3));
			int xLength = Integer.parseInt(line.get(4));
			int yLength = Integer.parseInt(line.get(5));
			String addOn = line.get(6);
			String roomArea = line.get(7);
			school.getRooms().add(new Room(name, space, x, y, xLength, yLength, addOn, roomArea, fileTimeTableRooms));
			lineCount++;
		}
	}

	@Override
	public void initTeachers(String fileTeachers, String fileTimeTableTeachers) {
		ArrayList<String> header = new ArrayList<String>();
		header.add("Nachname");
		header.add("Vorname");
		header.add("Abk");
		header.add("Geb.Datum");
		ParserInterface nameListParser = new Parser(fileTeachers, "[\\s]*;|,[\\s]*", StandardCharsets.ISO_8859_1, 1,
				header);
		try {
			nameListParser.processLineByLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
			school.getTeachers().add(teacher);
			school.getTeacherAbbrMap().put(abbr, teacher);
			lineCount++;
		}
	}

	@Override
	public void initClasses(String fileName) {
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.ISO_8859_1));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {
				if (zeile.contains("Staatl.")) {
					String[] splitItems = zeile.split(" ");
					for (int i = 0; i < splitItems.length; i++) {
						if (splitItems[i].equals("Klasse")) {
							school.getClasses().add(new SchoolClass(splitItems[i + 1], fileName));
						}
					}
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initTimes(String fileName) {
		ArrayList<String> header = new ArrayList<String>();
		header.add("name");
		header.add("start");
		header.add("end");
		ParserInterface timeListParser = new Parser(fileName, ";", StandardCharsets.ISO_8859_1, 1, header);
		try {
			timeListParser.processLineByLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		ArrayList<ArrayList<String>> timeList = timeListParser.getTable();
		int lineCount = 0;
		for (ArrayList<String> line : timeList) {
			if (lineCount == 0) {
				lineCount++;
				continue;
			}
			String name = line.get(0);
			SimpleDateFormat formatter = new SimpleDateFormat("hh:mm");
			Date start, end;
			try {
				start = formatter.parse(line.get(1));
				end = formatter.parse(line.get(2));
				school.getTimes().add(new SchoolTime(name, start.getTime(), end.getTime()));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			lineCount++;
		}
	}

	@Override
	public void initSubjects() {
		for (Room room : school.getRooms()) {
			for (int i = 1; i < 6; i++)
				for (int j = 1; j < 12; j++) {
					String subjectName = room.getSubject(i, j);
					String teacherName = room.getTeacher(i, j);
					Teacher teacher = school.getTeacherByAbbr(teacherName);

					SchoolSubject subject = school.getSubjectNameMap().get(subjectName);
					if (subject == null) {
						SchoolSubject newSubject = new SchoolSubject(subjectName);
						school.getSubjectNameMap().put(subjectName, newSubject);
						if (teacher != null && !newSubject.getTeachersOnSubject().contains(teacher))
							newSubject.getTeachersOnSubject().add(teacher);
					} else {
						if (teacher != null && !subject.getTeachersOnSubject().contains(teacher))
							subject.getTeachersOnSubject().add(teacher);
					}
				}
		}
		for (SchoolSubject subject : school.getSubjectNameMap().values())
			school.getSubjects().add(subject);
	}

	@Override
	public String getRoomAllocationTeacherAbbr(String roomName) {
		for (Room room : school.getRooms()) {
			if (room.getName().equals(roomName)) {
				String roomAllocationTeacher = room.getRoomAllocation()[this.getSelectedDay() - 1][this
						.getSelectedHour() - 1][0];
				if (roomAllocationTeacher == null && room.getRoomAllocationText() != "") {
					return "";
				}
				return roomAllocationTeacher;
			}
		}
		return "";
	}

	@Override
	public String getRoomAllocationClassName(String roomName) {
		for (Room room : school.getRooms()) {
			if (room.getName().equals(roomName)) {
				String roomAllocationClass = room.getRoomAllocation()[this.getSelectedDay() - 1][this.getSelectedHour()
						- 1][1];
				if (roomAllocationClass == null && room.getRoomAllocationText() != "") {
					return "";
				}
				return roomAllocationClass;
			}
		}
		return "";
	}

	@Override
	public Teacher getRoomAllocationTeacher(String roomName) {
		String abbr = getRoomAllocationTeacherAbbr(roomName);
		for (Teacher teacher : school.getTeachers()) {
			if (teacher.getAbbr().equals(abbr)) {
				return teacher;
			}
		}
		return null;
	}

	@Override
	public SchoolClass getRoomAllocationClass(String roomName) {
		String name = getRoomAllocationClassName(roomName);
		// System.out.println(name);
		for (SchoolClass schoolClass : school.getClasses()) {
			if (name.contains(schoolClass.getName())) {
				return schoolClass;
			}
		}
		return null;
	}

	@Override
	public String getRoomAllocationSubject(String roomName) {
		for (Room room : school.getRooms()) {
			if (room.getName().equals(roomName)) {
				String roomAllocationSubject = room.getRoomAllocation()[selectedDay - 1][selectedHour - 1][2];
				return roomAllocationSubject;
			}
		}
		return "";
	}

	@Override
	public void setSelectedDay(int selectedDay) {
		this.selectedDay = selectedDay;
		notifyObservers();
	}

	@Override
	public void setSelectedHour(int selectedHour) {
		this.selectedHour = selectedHour;
		notifyObservers();
	}

	@Override
	public int getSelectedHour() {
		return this.selectedHour;
	}

	@Override
	public int getSelectedDay() {
		return this.selectedDay;
	}

	@Override
	public void setSelectedTime(int selectedDay, int selectedHour) {
		this.selectedDay = selectedDay;
		this.selectedHour = selectedHour;
		notifyObservers();
	}

	@Override
	public String getSelectedDayName() {
		switch (this.getSelectedDay()) {
		case 1:
			return "Montag";
		case 2:
			return "Dienstag";
		case 3:
			return "Mittwoch";
		case 4:
			return "Donnerstag";
		case 5:
			return "Freitag";
		default:
			return "-";
		}
	}

}
