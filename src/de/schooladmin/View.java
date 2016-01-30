package de.schooladmin;

/**
 * @author Anja Kleebaum
 *
 */
import java.awt.CardLayout;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import java.io.PrintWriter;

import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.Box;
import javax.swing.GroupLayout;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class View extends JFrame implements ObserverInterface, ViewInterface {

	private static final long serialVersionUID = -5648786394907750019L;

	ModelInterface model;
	ControllerInterface controller;

	protected JFrame loadingFrame;
	protected JFrame viewFrame;
	protected JPanel viewPanel;
	protected JScrollPane scrollPane;
	protected JMenuBar menuBar;
	protected JMenuItem menuFile;
	protected JMenuItem menuItemClose;

	protected Icon icon = new ImageIcon(getClass().getResource("icon.png"));
	protected Icon splash = new ImageIcon(getClass().getResource("splash.png"));

	protected GroupLayout layout;
	protected CardLayout cardLayout;
	protected JPanel cards;

	protected boolean errorWindow = false;

	protected static JPopupMenu popupMenu;
	protected PopClickListener popClickListener;

	// constants for automatic management of program size
	protected final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width - 290;
	protected final int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height - 130;
	protected final int panelWidth = (int) Math.round(Toolkit.getDefaultToolkit().getScreenSize().width / 2.6);
	protected final int panelLength = (int) Math.round(Toolkit.getDefaultToolkit().getScreenSize().height / 3.7);

	// Font settings
	protected Font oldLabelFont = UIManager.getFont("Label.font");
	protected Font font = new Font("Arial", Font.PLAIN, 12);
	protected Font bigFont = new Font("Arial", Font.PLAIN, 16);
	protected Font titleFont = new Font(oldLabelFont.getFontName(), Font.PLAIN, 18);
	protected static Font monospacedFont = new Font("monospaced", Font.PLAIN, 12);
	protected Insets margin = new Insets(0, 0, 0, 0);

	protected HashMap<Teacher, Integer> teacherListMap = new HashMap<Teacher, Integer>();

	// constructor for View class in MVC pattern
	public View(ControllerInterface controller, ModelInterface model) {
		loadingFrame();
		this.controller = controller;
		this.model = model;
		model.registerObserver((ObserverInterface) this);
	}

	@Override
	public void initView() {
		viewFrame = new JFrame(model.getName());
		viewFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		viewFrame.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				exitProgram();
			}
		});
		viewFrame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		viewFrame.setIconImage(((ImageIcon) icon).getImage());
		viewPanel = new JPanel();

		layout = new GroupLayout(viewPanel);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
		viewPanel.setLayout(layout);

		menuBar = new JMenuBar();

		cardLayout = new CardLayout();
		cards = new JPanel(cardLayout);
		scrollPane = new JScrollPane(cards);

		menuBar = createMenuBar();
		createCards();
		createHelpMenu();
		viewFrame.setJMenuBar(menuBar);

		viewFrame.getContentPane().add(scrollPane);
		viewFrame.setMinimumSize(scrollPane.getPreferredSize());
		viewFrame.pack();
		viewFrame.setVisible(true);
		update();
		loadingFrame();
	}

	@Override
	public void exitProgram() {
		final JFrame frame = window("Programm " + model.getName() + " verlassen? ");

		Box box = Box.createVerticalBox();
		box.add(Box.createGlue());
		box.add(new JLabel(" Wollen Sie " + model.getName() + " verlassen? "));
		box.add(Box.createGlue());
		frame.getContentPane().add(box, "East");

		JPanel p2 = new JPanel();
		JButton ok = new JButton("Ja");
		JButton cancel = new JButton("Abbrechen");
		p2.add(ok);
		p2.add(cancel);
		frame.getContentPane().add(p2, "South");

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				viewFrame.dispose();
				frame.dispose();
			}
		});

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				frame.dispose();
			}
		});
		
		frame.pack();	
	}

	@Override
	public void loadingFrame() {
		if (loadingFrame == null) {

			loadingFrame = new JFrame();
			loadingFrame.setUndecorated(true);
			loadingFrame.setContentPane(new JLabel(splash));

			loadingFrame.setSize(splash.getIconWidth(), splash.getIconHeight());
			loadingFrame.setLocationRelativeTo(null);
			loadingFrame.pack();
		}
		if (loadingFrame.isVisible()) {
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			loadingFrame.setVisible(false);
		} else {
			loadingFrame.setVisible(true);
		}
	}

	@Override
	public void initPanelLayout(JPanel panel) {
		layout = new GroupLayout(panel);
		panel.setLayout(layout);
		layout.setAutoCreateGaps(true);
		layout.setAutoCreateContainerGaps(true);
	}

	@Override
	public void update() {
		if (errorWindow == false && !model.getError().equals(""))
			errorWindow(model.getError());
	}

	@Override
	public JMenuBar createMenuBar() {
		menuFile = new JMenu("Datei");
		menuBar.add(menuFile);

		menuItemClose = new JMenuItem("Programm schlie\u00DFen");

		// menuFile.add(menuItemClose);
		menuItemClose.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				exitProgram();
			}
		});

		return menuBar;
	}

	@Override
	public void createCards() {
		scrollPane.addMouseListener(new PopClickListener());
	}

	@Override
	public void createHelpMenu() {
		JMenu menuHelp = new JMenu("Hilfe");
		JMenuItem menuItemAbout = new JMenuItem("\u00dcber " + model.getName());
		menuBar.add(menuHelp);
		menuHelp.add(menuItemAbout);
		ImageIcon imgIcon = (ImageIcon) icon;
		imgIcon.setImage(imgIcon.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT));
		menuItemAbout.setIcon(imgIcon);

		menuItemAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				helpWindow();
			}
		});
	}

	@Override
	public void helpWindow() {
		final JFrame frame = window("\u00dcber " + model.getName());
		
		Box box = Box.createVerticalBox();
		box.add(Box.createGlue());
		box.add(new JLabel(" " + model.getName() + " "));
		box.add(new JLabel(" Version: " + ModelInterface.version));
		box.add(new JLabel(" Entwickelt von Anja Kleebaum "));
		box.add(new JLabel(" Kontakt: Anja [dot] Kleebaum [at] Kleebaum [dot] de "));
		box.add(Box.createGlue());
		frame.getContentPane().add(box, "East");

		JPanel p2 = new JPanel();
		JButton ok = new JButton("OK");
		p2.add(ok);
		frame.getContentPane().add(p2, "South");

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				frame.setVisible(false);
			}
		});		
				
		frame.pack();		
	}
	
	@Override
	public JFrame window(String title) {
		final JFrame frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		ImageIcon icon = new ImageIcon(getClass().getResource("icon.png"));
		frame.setIconImage(((ImageIcon) icon).getImage());

		JLabel lab = new JLabel(icon);
		frame.getContentPane().add(lab, "West");		
		
		frame.setLocation(screenWidth / 2 - 50, screenHeight / 2 - 50);
		frame.setVisible(true);
		
		return frame;
	}

	@Override
	public void errorWindow(String message) {
//		final JFrame frame = window("Fehlermeldung");
//
//		Box box = Box.createVerticalBox();
//		box.add(Box.createGlue());
//		box.add(new JLabel(" " + message));
//		box.add(Box.createGlue());
//		frame.getContentPane().add(box, "East");
//
//		JPanel p2 = new JPanel();
//		JButton ok = new JButton("OK");
//		p2.add(ok);
//		frame.getContentPane().add(p2, "South");
//
//		ok.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent evt) {
//				frame.setVisible(false);
//				controller.setError("");
//				errorWindow = false;
//			}
//		});
//
//		errorWindow = true;
//		frame.pack();
	}

	@Override
	public JList<String> createTeacherNameList() {
		final JList<String> teacherList = new JList<String>();
		teacherList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		ArrayList<String> resultArrayList = new ArrayList<String>();
		int i = 0;
		for (Teacher teacher : model.getSchool().getTeachers()) {
			teacherListMap.put(teacher, i);
			resultArrayList.add(teacher.getAbbr() + " = " + teacher.getSurname() + ", " + teacher.getFirstname() + "");
			i++;
		}

		teacherList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (!evt.getValueIsAdjusting()) {
					for (Teacher teacher : model.getSchool().getTeachers()) {
						if (teacher.getAbbr().equals(teacherList.getSelectedValue().split(" ")[0])) {
							controller.setSelectedTeacher(teacher);
						}
					}
				}
			}
		});
		String[] resultArray = resultArrayList.toArray(new String[resultArrayList.size()]);
		teacherList.setListData(resultArray);
		return teacherList;
	}

	@Override
	public void print(Component toPrint, String name) {
		PrinterJob pjob = PrinterJob.getPrinterJob();
		// Set print component
		PageFormat pf = pjob.defaultPage();
		pf.setOrientation(PageFormat.LANDSCAPE);
		pjob.setPrintable(new Printer(toPrint), pf);
		pjob.setJobName(name);
		if (pjob.printDialog()) {
			try {
				pjob.print();
			} catch (PrinterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Override
	public JPopupMenu createPopUpMenu(final MouseEvent e) {
		popupMenu = new JPopupMenu();

//		if (e.getSource() instanceof JTable) {
//			final JTable table = (JTable) e.getSource();
//			final DefaultTableModel model = (DefaultTableModel) table.getModel();
//
//			JMenuItem addColumnItem = new JMenuItem("Zeile am Tabellenende einf\u00fcgen");
//			popupMenu.add(addColumnItem);
//			addColumnItem.addActionListener(new ActionListener() {
//
//				@Override
//				public void actionPerformed(ActionEvent arg0) {
//					model.addRow(new Object[] { "", "", "" });
//				}
//			});
//
//			if (table.getSelectedRow() > 0) {
//
//				JMenuItem rmColumnItem = new JMenuItem("markierte Zeile l\u00f6schen");
//				popupMenu.add(rmColumnItem);
//				rmColumnItem.addActionListener(new ActionListener() {
//
//					@Override
//					public void actionPerformed(ActionEvent arg0) {
//						model.removeRow(table.getSelectedRow());
//					}
//				});
//			}
//
//		}

		if (e.getSource() instanceof JTextArea) {
			JMenuItem openTextItem = new JMenuItem("Text in Editor \u00F6ffnen");
			final JTextArea textArea = (JTextArea) e.getSource();
			popupMenu.add(openTextItem);
			openTextItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					PrintWriter w;

					try {
						File datei = File.createTempFile("plan", ".txt");
						w = new PrintWriter(new BufferedWriter(new FileWriter(datei)));

						w.print(textArea.getText());
						w.flush();
						w.close();

						Desktop.getDesktop().open(datei);
						datei.deleteOnExit();

					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});

			JMenuItem printTextItem = new JMenuItem("Text drucken");
			popupMenu.add(printTextItem);
			printTextItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {

					PrintWriter w;

					try {
						File datei = File.createTempFile("plan", ".txt");
						w = new PrintWriter(new BufferedWriter(new FileWriter(datei)));

						w.print(textArea.getText());
						w.flush();
						w.close();
						
						int printFontSize = 9;
						
						if (textArea.getWidth() > 600) {
							printFontSize = Math.round(5800 / textArea.getWidth());
						}
						
						Font printFont = new Font("monospaced", Font.PLAIN, printFontSize);
						textArea.setFont(printFont);
						textArea.print();
						textArea.setFont(View.monospacedFont);

						datei.deleteOnExit();

					} catch (IOException | PrinterException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			});
		}
		return (popupMenu);
	}

	public class PopClickListener extends MouseAdapter {

		JPopupMenu popupMenu;

		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		public void doPop(final MouseEvent e) {
			popupMenu = createPopUpMenu(e);
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
}
