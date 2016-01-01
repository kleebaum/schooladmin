package de.schooladmin;

/**
 * @author Anja Kleebaum
 *
 */
import java.awt.CardLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;

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
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

public class View extends JFrame implements ObserverInterface, ViewInterface {

	private static final long serialVersionUID = -5648786394907750019L;

	ModelInterface model;
	ControllerInterface controller;

	protected String title = "GSH Smart Admin";
	protected JFrame loadingFrame;
	protected JFrame viewFrame;
	protected JPanel viewPanel;
	protected JScrollPane scrollPane;
	protected JMenuBar menuBar;
	protected JMenu menuHelp;

	protected Icon icon = new ImageIcon(getClass().getResource("icon.png"));
	protected Icon splash = new ImageIcon(getClass().getResource("splash.png"));

	protected GroupLayout layout;
	protected CardLayout cardLayout;
	protected JPanel cards;
	protected String selectedClassName = "";
	
	protected boolean errorWindow = false;
	
	// constants for automatic management of program size
	protected final int screenWidth = Toolkit.getDefaultToolkit().getScreenSize().width - 290;
	protected final int screenHeight = Toolkit.getDefaultToolkit().getScreenSize().height - 130;
	protected final int panelWidth = (int) Math.round(Toolkit.getDefaultToolkit()
			.getScreenSize().width / 2.6);
	protected final int panelLength = (int) Math.round(Toolkit.getDefaultToolkit()
			.getScreenSize().height / 3.7);

	// Font settings
	protected Font oldLabelFont = UIManager.getFont("Label.font");
	protected Font font = new Font("Arial", Font.PLAIN, 12);
	protected Font bigFont = new Font("Arial", Font.PLAIN, 16);
	protected Font titleFont = new Font (oldLabelFont.getFontName(), Font.PLAIN, 18);
	protected Font monospacedFont = new Font("monospaced", Font.PLAIN, 12);
	protected Insets margin = new Insets(0, 0, 0, 0);

	// constructor for View class in MVC pattern
	public View(ControllerInterface controller, ModelInterface model) {
		this.controller = controller;
		this.model = model;
		model.registerObserver((ObserverInterface) this);
		loadingFrame();
	}

	@Override
	public void initView() {
		title = getTitle();
		viewFrame = new JFrame(title);
		viewFrame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		viewFrame.addWindowListener(new WindowAdapter() {
		    public void windowClosing(WindowEvent e) {
		    	exitProgram();
		        viewFrame.dispose();
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

	public JMenuBar createMenuBar() {
		return menuBar;
	}

	public void createCards() {
	}
	
	public String getTitle() {
		return title;
	}
	
	@Override
	public void createHelpMenu() {
		menuHelp = new JMenu("Hilfe");
		JMenuItem menuItemAbout = new JMenuItem("\u00dcber GSH Smart Admin");
		menuBar.add(menuHelp);
		menuHelp.add(menuItemAbout);
		ImageIcon imgIcon = (ImageIcon) icon;
		imgIcon.setImage(imgIcon.getImage().getScaledInstance(16, 16, Image.SCALE_DEFAULT));
		menuItemAbout.setIcon(imgIcon);

		menuItemAbout.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				createHelpView();
			}
		});	
	}
	
	@Override
	public void createHelpView() {		
		final JFrame frame = new JFrame("About GSH Smart Admin");
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.setIconImage(((ImageIcon) icon).getImage());

		Box box = Box.createVerticalBox();
		box.add(Box.createGlue());
		box.add(new JLabel(" " + model.getName() + " "));
		box.add(new JLabel(" Version: " + ModelInterface.version));
		box.add(new JLabel(" Entwickelt von Anja Kleebaum "));
		box.add(new JLabel(" Kontakt: Anja.Kleebaum@Kleebaum.de "));
		box.add(Box.createGlue());
		frame.getContentPane().add(box, "Center");

		JPanel p2 = new JPanel();
		JButton ok = new JButton("OK");
		p2.add(ok);
		frame.getContentPane().add(p2, "South");

		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				frame.setVisible(false);
			}
		});

		// display the window
		frame.setLocation(screenWidth/2-50, screenHeight/2-50);
		frame.pack();
		frame.setVisible(true);
	}
	
	@Override
	public void errorWindow(String message) {		
//		final JFrame frame = new JFrame("Fehlermeldung");
//		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
//		icon = loadIcon();
//		frame.setIconImage(((ImageIcon) icon).getImage());
//		
//		// add content
//		JLabel lab = new JLabel(icon);
//		frame.getContentPane().add(lab, "West");
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
//
//		// display the window
//		frame.setLocation(screenWidth/2-50, screenHeight/2-50);
//		frame.pack();
//		frame.setVisible(true);
	}
	
	@Override
	public JList<String> createTeacherNameList() {
		final JList<String> teacherList = new JList<String>();
		teacherList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		ArrayList<String> resultArrayList = new ArrayList<String>();
		for (Teacher teacher : model.getTeachers()) {
			resultArrayList.add(teacher.getAbbr() + " = " + teacher.getSurname() + ", " + teacher.getFirstname() + "");
		}

		teacherList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (!evt.getValueIsAdjusting()) {
					for (Teacher teacher : model.getTeachers()) {
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
}
