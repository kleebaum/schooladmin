package de.schooladmin.roomalloc;

import javax.swing.JPanel;

import de.schooladmin.ViewInterface;
import de.schooladmin.Room.RoomArea;

/**
 * Interface for View of Model-View-Controller Pattern
 * 
 * @author Anja Kleebaum
 *
 */
public interface ViewRoomAllocInterface extends ViewInterface {

	/**
	 * creates card in CardLayout "Raumplan" at program start
	 */
	public abstract void createRoomView();

	/**
	 * creates card in CardLayout "Stundenplaene" at program start
	 */
	public abstract void createListView();

	/**
	 * method called for every room area in school
	 * 
	 * @param panel
	 *            specifies the panel in GUI
	 * @param roomAreaTitle
	 *            name of room area in GUI (like Altbau, Obergeschoss, ...)
	 * @param roomArea
	 *            name of room area in file Raeume.csv (like Altbau_oben)
	 * @param areaWidth
	 *            absolute width of room area in meter
	 * @param areaLength
	 *            absolute length of room area in meter
	 */
	public abstract void fillRoomArea(JPanel panel, String roomAreaTitle, RoomArea roomArea, int areaWidth,
			int areaLength);

	/**
	 * shows String content in a text area
	 * 
	 * @param title
	 *            title of the shown text
	 * @param text
	 *            specifies String content
	 * @param emptyText
	 *            specifies what's displayed if text is empty
	 */
	public abstract void showText(String title, String text, String emptyText);

	/**
	 * updates card in CardLayout called "Raumplan"
	 */
	public abstract void updateAllRoomAllocation();
	
	/**
	 * returns all room allocation for selected time and hour
	 */
	public abstract String showRoomAllocation();
	
	/** 
	 * 
	 * @return String with whitespaces of a certain length
	 */
	public abstract String whitespaceCalc(int totalLength, int letterLength);
}