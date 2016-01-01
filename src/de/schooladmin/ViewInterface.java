package de.schooladmin;

import javax.swing.JList;
import javax.swing.JPanel;

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
	public abstract void createHelpView();

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

	
	public abstract void exitProgram();

}
