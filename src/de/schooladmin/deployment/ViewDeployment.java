package de.schooladmin.deployment;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Desktop;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.GroupLayout;
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
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;

import de.schooladmin.SchoolClass;
import de.schooladmin.SchoolGroup;
import de.schooladmin.SchoolSubject;
import de.schooladmin.Teacher;
import de.schooladmin.View;

/**
 * @author Anja Kleebaum
 *
 */
public class ViewDeployment extends View implements ViewDeploymentInterface, TableModelListener {

	private static final long serialVersionUID = -5648786394907750019L;

	ModelDeploymentInterface model;
	ControllerDeploymentInterface controller;

	protected JPanel overviewPanel;
	protected JTable teacherTable;
	protected JTable teacherInfoTable;

	protected Object[] subjectTableHeader = new Object[] { "Gruppe", "Lehrer", "Stunden" };

	protected JLabel selectedTeacherAbbrLabel;
	protected JLabel selectedTeacherNameLabel;
	protected JLabel selectedTeacherToDoLabel;
	protected JLabel selectedTeacherSchoolTypeLabel;
	protected JLabel selectedTeacherActDoLabel;

	protected JList<String> teacherList;
	protected JPanel classCards;

	private CardLayout classCardLayout;

	public ViewDeployment(ControllerDeploymentInterface controller, ModelDeploymentInterface model) {
		super(controller, model);
		this.model = model;
		this.controller = controller;
		this.popClickListener = new PopClickListener();
	}

	@Override
	public void createCards() {
		super.createCards();
		cards.add(viewPanel, "viewPanel");
		createOverView();
		cards.add(overviewPanel, "overviewPanel");
		createClassesView();
	}

	@Override
	public JMenuBar createMenuBar() {
		super.createMenuBar();

		JMenuItem menuItemSaveAll = new JMenuItem("Alle Klassen speichern");
		menuFile.add(menuItemSaveAll);
		menuItemSaveAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (SchoolClass eachClass : model.getSchool().getClasses()) {
					model.exportSchoolClassToCVS(eachClass, false);
				}
			}
		});

		JMenuItem menuItemOpenTeacherData = new JMenuItem("Lehrer-Datei \u00f6ffnen");
		menuFile.add(menuItemOpenTeacherData);
		menuItemOpenTeacherData.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				File file = new File(model.getFileTeachers());

				Desktop desktop = Desktop.getDesktop();
				if (file.exists())
					try {
						desktop.open(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
			}
		});

		menuFile.add(new JSeparator());
		menuFile.add(menuItemClose);

		JMenu overviewMenu = new JMenu("\u00dcberblick");

		menuBar.add(overviewMenu);
		overviewMenu.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent arg0) {
			}

			@Override
			public void menuDeselected(MenuEvent arg0) {
			}

			@Override
			public void menuSelected(MenuEvent arg0) {
				cardLayout.show(cards, "overviewPanel");
				controller.setSelectedClass("");
			}
		});

		JMenuItem menuItemSave = new JMenuItem("Speichern als");
		overviewMenu.add(menuItemSave);
		menuItemSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.exportTeacherOverviewToCVS(true);
			}

		});
		
		JMenuItem menuItemOpenFile = new JMenuItem("Speichern und \u00f6ffnen");
		overviewMenu.add(menuItemOpenFile);
		menuItemOpenFile.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				model.exportTeacherOverviewToCVS(false);
				File file = new File("Vergleich-Ist-Soll.csv");

				Desktop desktop = Desktop.getDesktop();
				if (file.exists())
					try {
						desktop.open(file);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				
			}

		});		

		return menuBar;
	}

	@Override
	public void createOverView() {
		overviewPanel = new JPanel();
		initPanelLayout(overviewPanel);

		// teacher table
		DefaultTableModel teacherTableModel = new DefaultTableModel();
		teacherTableModel.addColumn("Abkuerzung");
		teacherTableModel.addColumn("Nachname");
		teacherTableModel.addColumn("Vorname");
		teacherTableModel.addColumn("Sollstunden");
		teacherTableModel.addColumn("aktuelle Stunden");
		teacherTableModel.addColumn("Differenz");
		for (Teacher teacher : model.getSchool().getTeachers()) {
			double toDo = teacher.getToDo();
			double actDo = teacher.getActDo();
			double diff = actDo - toDo;
			teacherTableModel.addRow(new Object[] { teacher.getAbbr(), teacher.getSurname(), teacher.getFirstname(),
					toDo, actDo, diff });
		}
		JPanel panelTeacher = new JPanel(new GridLayout(1, 0));
		teacherTable = new JTable(teacherTableModel) {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int x, int y) {
				return false;
			}
		};
		teacherTable.setFont(bigFont);
		teacherTable.setAutoCreateColumnsFromModel(true);
		teacherTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		teacherTable.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(java.awt.event.MouseEvent evt) {
				int row = teacherTable.rowAtPoint(evt.getPoint());
				int col = teacherTable.columnAtPoint(evt.getPoint());
				if (row >= 0 && col >= 0) {
					String abbr = teacherTable.getValueAt(row, 0).toString().trim();
					controller.setSelectedTeacher(abbr);
				}
			}

		});
		JScrollPane scrollPaneTeacherTable = new JScrollPane(teacherTable);
		panelTeacher.add(scrollPaneTeacherTable);

		// teacher info table
		JPanel panelTeacherInfo = new JPanel(new GridLayout(1, 0));
		teacherInfoTable = new JTable() {
			private static final long serialVersionUID = 1L;

			public boolean isCellEditable(int x, int y) {
				return false;
			}
		};
		teacherInfoTable.setFont(bigFont);
		teacherInfoTable.setAutoCreateColumnsFromModel(true);
		teacherInfoTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPaneTeacherInfoTable = new JScrollPane(teacherInfoTable);
		panelTeacherInfo.add(scrollPaneTeacherInfoTable);

		// Layout OverView
		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, screenWidth - 300, GroupLayout.PREFERRED_SIZE)
				.addComponent(panelTeacherInfo, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE));

		layout.setVerticalGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE)
				.addComponent(panelTeacherInfo, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE));
	}

	@Override
	public void createClassView(final SchoolClass schoolClass) {
		final String localClassName = schoolClass.getName();

		JPanel localPanel = new JPanel();
		GroupLayout groupLayout = new GroupLayout(localPanel);
		localPanel.setLayout(groupLayout);
		classCards.add(localPanel, localClassName);

		JMenu localMenu = new JMenu(localClassName);
		if (schoolClass != model.getSelectedClass()) {
			menuBar.add(localMenu);

			JMenuItem menuItemSave = new JMenuItem("Speichern");
			localMenu.add(menuItemSave);
			menuItemSave.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					model.exportSchoolClassToCVS(schoolClass, false);
				}
			});

			JMenuItem menuItemSaveAs = new JMenuItem("Speichern als");
			localMenu.add(menuItemSaveAs);
			menuItemSaveAs.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					model.exportSchoolClassToCVS(schoolClass, true);
				}
			});

			JMenuItem menuItemOpenFile = new JMenuItem("Datei \u00f6ffnen");
			localMenu.add(menuItemOpenFile);
			menuItemOpenFile.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					File file = new File("input/" + schoolClass.getName() + ".csv");

					Desktop desktop = Desktop.getDesktop();
					if (file.exists())
						try {
							desktop.open(file);
						} catch (IOException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
				}
			});

			localMenu.add(new JSeparator());

			JMenuItem newSubjectItem = new JMenuItem("Fach hinzuf\u00fcgen");
			newSubjectItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					controller.setSelectedClass(schoolClass);
					addNewSubject();
				}
			});
			localMenu.add(newSubjectItem);
		}

		localMenu.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent arg0) {
			}

			@Override
			public void menuDeselected(MenuEvent arg0) {
			}

			@Override
			public void menuSelected(MenuEvent arg0) {
				cardLayout.show(cards, "classPanel");
				classCardLayout.show(classCards, localClassName);
				controller.setSelectedClass(schoolClass);
			}
		});

		// Label showing class name
		JLabel titleLabel = new JLabel(localClassName);
		titleLabel.setFont(titleFont);
		JPanel titlePanel = new JPanel();
		titlePanel.setLayout(new GridLayout(1, 0));
		titlePanel.add(titleLabel);

		// subjects panel
		JPanel subjectsPanel = new JPanel(new GridLayout(3, 0));
		ArrayList<SchoolSubject> subjects = schoolClass.getSubjects();

		for (final SchoolSubject subject : subjects) {
			JPanel subjectPanel = new JPanel(new BorderLayout());
			subjectPanel.setBorder(BorderFactory.createTitledBorder(subject.getName()));
			subjectPanel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(final MouseEvent e) {
					controller.setSelectedSubject(subject);
				}

				@Override
				public void mouseClicked(final MouseEvent e) {
					controller.setSelectedSubject(subject);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					controller.setSelectedSubject(subject);
				}
			});

			DefaultTableModel tableModel = new DefaultTableModel();
			for (Object object : subjectTableHeader) {
				tableModel.addColumn(object);
			}
			for (SchoolGroup group : subject.getSchoolGroups()) {
				tableModel.addRow(new Object[] { group.getName(), group.getTeacherAbbr(), group.getHoursOnSubject() });
			}
			final JTable subjectTable = new JTable(tableModel);
			subjectTable.setFont(bigFont);
			subjectTable.setAutoCreateColumnsFromModel(true);
			subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			subjectTable.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseEntered(final MouseEvent e) {
					controller.setSelectedSubject(subject);
				}

				@Override
				public void mousePressed(MouseEvent e) {
					controller.setSelectedSubject(subject);
				}

				@Override
				public void mouseClicked(final MouseEvent e) {
					controller.setSelectedSubject(subject);
					if (e.getClickCount() == 1) {
						int row = subjectTable.getSelectedRow();
						controller.setSelectedTeacher(subject.getSchoolGroups().get(row).getTeacherOnSubject());
					}
				}
			});

			subjectTable.getModel().addTableModelListener(this);
			subjectTable.addMouseListener(popClickListener);

			JScrollPane scrollPanesubjectTable = new JScrollPane(subjectTable);
			scrollPanesubjectTable.addMouseListener(popClickListener);
			subjectPanel.add(scrollPanesubjectTable);
			subjectsPanel.add(subjectPanel);
		}

		// Layout for right part (subjects)
		groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addComponent(titlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(subjectsPanel, GroupLayout.PREFERRED_SIZE, screenWidth - 100,
						GroupLayout.PREFERRED_SIZE));
		groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
				.addComponent(titlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(subjectsPanel, GroupLayout.PREFERRED_SIZE, screenHeight - 10,
						GroupLayout.PREFERRED_SIZE));
	}

	@Override
	public void createClassesView() {
		JPanel classPanel = new JPanel();
		initPanelLayout(classPanel);
		cards.add(classPanel, "classPanel");

		// Teacher Infos
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		infoPanel.setBorder(BorderFactory.createTitledBorder("Lehrerinfos"));

		selectedTeacherNameLabel = new JLabel("");
		selectedTeacherAbbrLabel = new JLabel("");
		selectedTeacherSchoolTypeLabel = new JLabel("");
		selectedTeacherToDoLabel = new JLabel("");
		selectedTeacherActDoLabel = new JLabel("");
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.7;
		c.weighty = 2.0;
		c.gridx = 0;
		c.gridy = 0;
		infoPanel.add(new JLabel("Abk\u00fcrzung:"), c);
		c.gridx = 1;
		infoPanel.add(selectedTeacherAbbrLabel, c);
		c.gridx = 0;
		c.gridy = 1;
		infoPanel.add(new JLabel("Name:"), c);
		c.gridx = 1;
		infoPanel.add(selectedTeacherNameLabel, c);
		c.gridx = 0;
		c.gridy = 2;
		infoPanel.add(new JLabel("Schulform:"), c);
		c.gridx = 1;
		infoPanel.add(selectedTeacherSchoolTypeLabel, c);
		c.gridx = 0;
		c.gridy = 3;
		infoPanel.add(new JLabel("Sollstunden:"), c);
		c.gridx = 1;
		infoPanel.add(selectedTeacherToDoLabel, c);
		c.gridx = 0;
		c.gridy = 4;
		infoPanel.add(new JLabel("Einsatz:"), c);
		c.gridx = 1;
		infoPanel.add(selectedTeacherActDoLabel, c);

		// Teacher List
		JPanel teacherListPanel = new JPanel();
		teacherListPanel.setLayout(new GridLayout(0, 1));
		teacherListPanel.setBorder(BorderFactory.createTitledBorder("Lehrerliste"));

		teacherList = createTeacherNameList();
		JScrollPane teacherListScrollPane = new JScrollPane(teacherList);
		teacherListPanel.add(teacherListScrollPane);
		teacherList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (!evt.getValueIsAdjusting()) {
					String abbr = teacherList.getSelectedValue().split(" ")[0];
					controller.setSelectedTeacher(abbr);
				}
			}
		});

		// Classes
		ArrayList<SchoolClass> classes = model.getSchool().getClasses();

		classCardLayout = new CardLayout();
		classCards = new JPanel(classCardLayout);

		for (SchoolClass schoolClass : classes) {
			createClassView(schoolClass);

		}

		// Layout for left part (teacher info, teacher list, save button)
		layout.setHorizontalGroup(
				layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addGroup(layout.createSequentialGroup()
								.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
										.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE, 300,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(teacherListPanel, GroupLayout.PREFERRED_SIZE, 300,
												GroupLayout.PREFERRED_SIZE))
								.addComponent(classCards, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE)));

		layout.setVerticalGroup(
				layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
								.addGroup(layout.createSequentialGroup()
										.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE, 284,
												GroupLayout.PREFERRED_SIZE)
										.addComponent(teacherListPanel, screenHeight - 300, GroupLayout.PREFERRED_SIZE,
												GroupLayout.PREFERRED_SIZE))
								.addComponent(classCards, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE)));

	}

	@Override
	public void update() {
		super.update();
		Teacher selectedTeacher = model.getSelectedTeacher();

		DefaultTableModel teacherInfoTableModel = new DefaultTableModel();
		teacherInfoTableModel.addColumn("Fach");
		teacherInfoTableModel.addColumn("Gruppe");
		teacherInfoTableModel.addColumn("Lehrer");

		if (selectedTeacher != null) {
			for (SchoolGroup group : selectedTeacher.getSchoolGroups()) {
				teacherInfoTableModel.addRow(
						new Object[] { group.getSubject().getName(), group.getName(), group.getHoursOnSubject() });

			}
			selectedTeacherAbbrLabel.setText(selectedTeacher.getAbbr());
			selectedTeacherNameLabel.setText(selectedTeacher.getSurname() + ", " + selectedTeacher.getFirstname());
			selectedTeacherToDoLabel.setText(selectedTeacher.getToDo() + "");
			selectedTeacherSchoolTypeLabel.setText(selectedTeacher.getSchoolType().getName());
			selectedTeacherActDoLabel.setText(selectedTeacher.getActDo() + "");

			int selectionIndex = teacherListMap.get(selectedTeacher);
			teacherList.setSelectedIndex(selectionIndex);
			teacherTable.getSelectionModel().setSelectionInterval(selectionIndex - 1, selectionIndex);
		}
		teacherInfoTable.setModel(teacherInfoTableModel);
	}

	@Override
	public JPopupMenu createPopUpMenu(final MouseEvent e) {
		popupMenu = super.createPopUpMenu(e);

		JMenuItem printItem = new JMenuItem("Bildschirmfoto drucken");
		popupMenu.add(printItem);
		printItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				print(cards, model.getName() + " " + model.getSelectedClass());
			}
		});

		final SchoolClass selectedClass = model.getSelectedClass();
		if (selectedClass != null) {
			JMenuItem newSubjectItem = new JMenuItem("Fach hinzuf\u00fcgen");
			popupMenu.add(newSubjectItem);
			newSubjectItem.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					addNewSubject();

				}
			});

			final SchoolSubject selectedSubject = model.getSelectedSubject();
			if (selectedSubject != null) {
				JMenuItem removeSubjectItem = new JMenuItem("Fach " + selectedSubject.getName() + " l\u00f6schen");
				popupMenu.add(removeSubjectItem);
				removeSubjectItem.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						removeSubject(selectedSubject, e);
					}
				});
			}

		}

		if (e.getSource() instanceof JTable) {
			final JTable table = (JTable) e.getSource();
			final SchoolSubject selectedSubject = model.getSelectedSubject();
			final DefaultTableModel tableModel = (DefaultTableModel) table.getModel();
			JMenuItem addColumnItem = new JMenuItem("Zeile am Tabellenende einf\u00fcgen");
			popupMenu.add(addColumnItem);
			addColumnItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					SchoolGroup newGroup = new SchoolGroup("", 0, "");
					selectedSubject.getSchoolGroups().add(newGroup);
					tableModel.addRow(new Object[] { newGroup.getName(), newGroup.getTeacherAbbr(),
							newGroup.getHoursOnSubject() });
					table.setModel(tableModel);
				}
			});

			final int selectedRow = table.getSelectedRow();
			if (selectedRow >= 0) {

				JMenuItem addColumnBeforeItem = new JMenuItem("Zeile vor markierter Zeile einf\u00fcgen");
				popupMenu.add(addColumnBeforeItem);
				addColumnBeforeItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						SchoolGroup newGroup = new SchoolGroup("", 0, "");
						selectedSubject.getSchoolGroups().add(selectedRow, newGroup);

						DefaultTableModel tableModel = new DefaultTableModel();
						for (Object object : subjectTableHeader) {
							tableModel.addColumn(object);
						}
						for (SchoolGroup group : selectedSubject.getSchoolGroups()) {
							tableModel.addRow(new Object[] { group.getName(), group.getTeacherAbbr(),
									group.getHoursOnSubject() });
						}
						table.setModel(tableModel);
						createClassView(selectedClass);
						classCardLayout.show(classCards, selectedClass.getName());
					}
				});

				JMenuItem rmColumnItem = new JMenuItem("markierte Zeile l\u00f6schen");
				popupMenu.add(rmColumnItem);
				rmColumnItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						selectedSubject.getSchoolGroups().remove(selectedRow);

						DefaultTableModel tableModel = new DefaultTableModel();
						for (Object object : subjectTableHeader) {
							tableModel.addColumn(object);
						}
						for (SchoolGroup group : selectedSubject.getSchoolGroups()) {
							tableModel.addRow(new Object[] { group.getName(), group.getTeacherAbbr(),
									group.getHoursOnSubject() });
						}
						table.setModel(tableModel);
					}
				});
			}

		}

		return (popupMenu);
	}

	protected void addNewSubject() {
		final SchoolClass selectedClass = model.getSelectedClass();

		final JFrame frame = window("Neues Fach");
		Box box = Box.createVerticalBox();
		box.add(Box.createGlue());
		box.add(new JLabel(" "));
		box.add(new JLabel(" "));
		box.add(new JLabel(" neues Fach anlegen:"));
		final JTextField field = new JTextField("");
		box.add(field);
		box.add(new JLabel(" "));
		box.add(new JLabel(" "));
		box.add(new JLabel(" "));
		box.add(Box.createGlue());
		frame.getContentPane().add(box, "East");

		JPanel p2 = new JPanel();
		JButton ok = new JButton("OK");
		JButton cancel = new JButton("Abbrechen");
		p2.add(ok);
		p2.add(cancel);

		frame.getContentPane().add(p2, "South");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				frame.dispose();
				SchoolSubject newSubject = new SchoolSubject(field.getText());
				SchoolGroup newGroup = new SchoolGroup("", 0, "");
				newSubject.getSchoolGroups().add(newGroup);
				selectedClass.getSubjects().add(newSubject);
				createClassView(selectedClass);
				classCardLayout.show(classCards, selectedClass.getName());
			}
		});

		cancel.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				frame.dispose();
			}
		});
		frame.pack();
	}

	protected void removeSubject(final SchoolSubject subject, final MouseEvent e) {
		final JFrame frame = window("Fach " + subject.getName() + " l\u00f6schen");
		Box box = Box.createVerticalBox();
		box.add(Box.createGlue());
		box.add(new JLabel(" Wollen Sie das Fach " + subject.getName() + " wirklich l\u00f6schen? "));
		box.add(Box.createGlue());
		frame.getContentPane().add(box, "East");

		JPanel p2 = new JPanel();
		JButton ok = new JButton("OK");
		JButton cancel = new JButton("Abbrechen");
		p2.add(ok);
		p2.add(cancel);

		frame.getContentPane().add(p2, "South");
		ok.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				SchoolClass selectedClass = model.getSelectedClass();
				selectedClass.getSubjects().remove(subject);
				createClassView(selectedClass);
				classCardLayout.show(classCards, selectedClass.getName());
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
	public void tableChanged(TableModelEvent e) {
		int col = e.getColumn();
		int row = e.getFirstRow();
		TableModel cellModel = (TableModel) e.getSource();

		SchoolSubject selectedSubject = model.getSelectedSubject();
		SchoolGroup selectedGroup = selectedSubject.getSchoolGroups().get(row);
		controller.setSelectedGroup(selectedGroup);

		if (col == 0) { // Group Name
			String groupName = cellModel.getValueAt(row, col).toString();
			selectedGroup.setName(groupName);
		}

		if (col == 1) { // Teacher
			Teacher oldTeacher = selectedGroup.getTeacherOnSubject();
			String newTeacherAbbr = cellModel.getValueAt(row, col).toString().trim();
			Teacher newTeacher = model.getSchool().getTeacherByAbbr(newTeacherAbbr);
			selectedGroup.setTeacherOnSubject(newTeacher);

			if (newTeacher != null && !newTeacher.equals(oldTeacher)) {
				int hours = Integer.parseInt(cellModel.getValueAt(row, 2).toString().trim());
				if (oldTeacher != null)
					oldTeacher.subtractFromActDo(hours);
				newTeacher.addToActDo(hours);
				controller.setSelectedTeacher(newTeacher);
			}
		}

		if (col == 2) { // Hours
			int oldHours = selectedGroup.getHoursOnSubject();
			int newHours = Integer.parseInt(cellModel.getValueAt(row, col).toString().trim());
			selectedGroup.setHoursOnSubject(newHours);
			controller.teacherAddToActDo(model.getSelectedTeacher(), newHours - oldHours);
		}

	}

}
