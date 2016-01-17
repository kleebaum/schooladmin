package de.schooladmin;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.ArrayList;

import javax.swing.JList;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;

/**
 * @author Anja Kleebaum
 *
 */
public interface ViewInterface {

	/**
	 * initializes the program's GUI
	 */
	public abstract void initView();

	/**
	 * displays splash screen during initializing process
	 */
	public abstract void loadingFrame();

	/**
	 * this method is called every time a card in Java Swing CardLayout is
	 * created
	 * 
	 * @param panel
	 *            for Java Swing CardLayout
	 */
	public abstract void initPanelLayout(JPanel panel);
	
	/**
	 * method called by controller when something changed
	 */
	public abstract void update();
	
	/**
	 * creates program info
	 */
	public abstract void helpWindow();

	/**
	 * creates help menu entry
	 */
	void createHelpMenu();
	
	/**
	 * creates a new window for an error message
	 */
	void errorWindow(String message);
	
	/**
	 * creates a list with teacher names and abbreviations
	 * 
	 * @return JList with Strings containing teacherNames
	 */
	public abstract JList<String> createTeacherNameList();

	/**
	 * things to do before exiting program
	 * 
	 */
	public abstract void exitProgram();

	/**
	 * exports table to text file
	 * @param className
	 */
	void exportTableDataToCVS(String folder, String fileName, String header, 
			ArrayList<String> content, boolean fileChooser);

	/**
	 * prints a gui component
	 * @param toPrint
	 * @param name
	 */
	void print(Component toPrint, String name);

	/**
	 * creates menu bar
	 * @return
	 */
	JMenuBar createMenuBar();

	/**
	 * creates cards
	 */
	void createCards();

	/**
	 * creates default pop up menu items
	 * @param e
	 * @return JPopupMenu popupMenu
	 */
	JPopupMenu createPopUpMenu(MouseEvent e);
	
}
