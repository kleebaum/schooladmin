package de.schooladmin;

/**
 * Class for rooms
 * 
 * @author Anja Kleebaum
 *
 */
import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

public class Room {
	private int space, x, y, xLength, yLength;
	private String name, addOn;

	public enum RoomArea {
		ALTBAU_UNTEN, ALTBAU_OBEN, NEUBAU_UNTEN, NEUBAU_OBEN, HALLEN, KELLER, NA
	}

	private RoomArea roomArea;
	private String roomAllocationText;
	private String[][][] roomAllocation; // day, hour, entry
	private BufferedReader in;
	private Scanner scanner;

	public Room(String name, int space, int x, int y, int xLength, int yLength, String addOn, String roomArea,
			String fileTimeTableRooms) {
		this.name = name;
		this.space = space;
		this.x = x;
		this.y = y;
		this.xLength = xLength;
		this.yLength = yLength;
		this.addOn = addOn;

		switch (roomArea) {
		case "Altbau_unten":
			this.roomArea = RoomArea.ALTBAU_UNTEN;
			break;
		case "Altbau_oben":
			this.roomArea = RoomArea.ALTBAU_OBEN;
			break;
		case "Neubau_unten":
			this.roomArea = RoomArea.NEUBAU_UNTEN;
			break;
		case "Neubau_oben":
			this.roomArea = RoomArea.NEUBAU_OBEN;
			break;
		case "Keller":
			this.roomArea = RoomArea.KELLER;
			break;
		case "Hallen":
			this.roomArea = RoomArea.HALLEN;
			break;
		default:
			this.roomArea = RoomArea.NA;
		}
		this.roomAllocationText = readRoomAllocationTextFromFile(fileTimeTableRooms);
		this.roomAllocation = fillRoomAllocationArray();
	}

	private String[][][] fillRoomAllocationArray() {
		roomAllocation = new String[5][12][3];
		if (this.roomAllocationText.equals("")) {
			for (int i = 0; i < roomAllocation.length; i++) {
				for (int j = 0; j < roomAllocation[i].length; j++) {
					for (int k = 1; k < roomAllocation[i][j].length; k++) {
						roomAllocation[i][j][k] = "";
					}
				}
			}
		}
		scanner = new Scanner(this.roomAllocationText);
		String line = null;
		boolean foundSeparationLine = false;
		int hour = -1;
		while (scanner.hasNext()) {
			line = scanner.nextLine();
			if (line.startsWith("+")) {
				foundSeparationLine = true;
				hour++;
			}
			if (foundSeparationLine == true && line.startsWith("| ")) {
				foundSeparationLine = false;
				// Eintraege an Stelle 8, 20, 32, 44, 56
				for (int verPos = 0; verPos < 3; verPos++) {
					for (int day = 1; day <= 5; day++) {
						int horPos = 8 + (day - 1) * 12;
						String entry = line.substring(horPos, horPos + 9).trim();
						roomAllocation[day - 1][hour - 1][verPos] = entry;
					}
					line = scanner.nextLine();
				}
			}
		}
		return roomAllocation;
	}

	private String readRoomAllocationTextFromFile(String fileName) {
		String roomAllocationText = "";
		boolean foundRoom = false;
		try {
			in = new BufferedReader(new InputStreamReader(new FileInputStream(fileName), StandardCharsets.ISO_8859_1));
			String zeile = null;
			while ((zeile = in.readLine()) != null) {
				if (foundRoom == true) {
					if (zeile.equals(""))
						break;
					roomAllocationText += zeile + "\r\n";
				}
				if (zeile.contains("Staatl.") && zeile.contains(" r" + this.name + " ")) {
					foundRoom = true;
					zeile = in.readLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (roomAllocationText.equals(""))
			return "";
		return roomAllocationText;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	public int getxLength() {
		return xLength;
	}

	public void setxLength(int xLength) {
		this.xLength = xLength;
	}

	public int getyLength() {
		return yLength;
	}

	public void setyLength(int yLength) {
		this.yLength = yLength;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getRoomAllocationText() {
		return roomAllocationText;
	}

	public void setRoomAllocationText(String roomAllocationText) {
		this.roomAllocationText = roomAllocationText;
	}

	public RoomArea getRoomArea() {
		return roomArea;
	}

	public void setRoomArea(RoomArea roomArea) {
		this.roomArea = roomArea;
	}

	public String[][][] getRoomAllocation() {
		return roomAllocation;
	}

	public void setRoomAllocation(String[][][] roomAllocation) {
		this.roomAllocation = roomAllocation;
	}

	public int getSpace() {
		return space;
	}

	public void setSpace(int space) {
		this.space = space;
	}

	public String getAddOn() {
		return addOn;
	}

	public void setAddOn(String addOn) {
		this.addOn = addOn;
	}

	public String getSchoolClass(int selectedDay, int selectedHour) {
		String schoolClass = this.roomAllocation[selectedDay - 1][selectedHour - 1][1];
		if (schoolClass != null)
			return schoolClass;
		return "";
	}

	public String getTeacher(int selectedDay, int selectedHour) {
		String teacher = this.roomAllocation[selectedDay - 1][selectedHour - 1][0];
		if (teacher != null)
			return teacher;
		return "";
	}

	public String getSubject(int selectedDay, int selectedHour) {
		try {
			String subject = this.roomAllocation[selectedDay - 1][selectedHour - 1][2];
			if (subject != null)
				return subject;
		} catch (ArrayIndexOutOfBoundsException e) {
			return "";
		}
		return "";
	}

}
