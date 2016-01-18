package de.schooladmin.deployment;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
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

	protected JLabel selectedTeacherAbbrLabel;
	protected JLabel selectedTeacherNameLabel;
	protected JLabel selectedTeacherToDoLabel;
	protected JLabel selectedTeacherSchoolTypeLabel;
	protected JLabel selectedTeacherActDoLabel;

	protected JList<String> teacherList;
	protected JPanel classCards;
	protected int oldValue = 0;
	protected String oldTeacherAbbr = "";
	protected HashMap<String, HashMap<String, JTable>> classTablesMap = new HashMap<String, HashMap<String, JTable>>();

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

		JMenuItem menuItemSave = new JMenuItem("Speichern als");
		menuFile.add(menuItemSave);
		menuItemSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				SchoolClass selectedClass = model.getSelectedClass();
				if (selectedClass != null) {
					exportTableDataToCVS(selectedClass.getName(), true);
				}
			}
		});

		JMenuItem menuItemSaveAll = new JMenuItem("Alle speichern");
		menuFile.add(menuItemSaveAll);
		menuItemSaveAll.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				for (SchoolClass eachClass : model.getClasses()) {
					exportTableDataToCVS(eachClass.getName(), false);
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
		for (Teacher teacher : model.getTeachers()) {
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

		// Save button
		JPanel ioPanel = new JPanel();
		ioPanel.setLayout(new GridLayout(0, 1));
		ioPanel.setBorder(BorderFactory.createTitledBorder("Dateiausgabe"));

		JButton saveButton = new JButton("speichern als");
		saveButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(final MouseEvent e) {
				if (e.getClickCount() == 1) {
					exportTableDataToCVS(model.getSelectedClass().getName(), true);
				}
			}
		});
		ioPanel.add(saveButton);

		// Classes
		ArrayList<SchoolClass> classes = model.getClasses();

		final CardLayout classCardLayout = new CardLayout();
		classCards = new JPanel(classCardLayout);

		for (int i = 0; i < classes.size(); i++) {
			final SchoolClass localClass = classes.get(i);
			final String localClassName = localClass.getName();

			JPanel localPanel = new JPanel();
			GroupLayout groupLayout = new GroupLayout(localPanel);
			localPanel.setLayout(groupLayout);
			classCards.add(localPanel, localClassName);

			JMenu localMenu = new JMenu(localClassName);
			menuBar.add(localMenu);

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
					controller.setSelectedClass(localClassName);
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
			ArrayList<SchoolSubject> subjects = localClass.getSubjects();
			HashMap<String, JTable> subjectTablesMap = new HashMap<String, JTable>();

			for (SchoolSubject subject : subjects) {
				JPanel subjectPanel = new JPanel(new BorderLayout());
				subjectPanel.setBorder(BorderFactory.createTitledBorder(subject.getName()));

				DefaultTableModel tableModel = new DefaultTableModel();
				tableModel.addColumn("Gruppe");
				tableModel.addColumn("Lehrer");
				tableModel.addColumn("Stunden");
				for (SchoolGroup group : subject.getSchoolGroups()) {
					tableModel.addRow(
							new Object[] { group.getName(), group.getTeacherAbbr(), group.getHoursOnSubject() });
				}
				final JTable subjectTable = new JTable(tableModel);
				subjectTable.setFont(bigFont);
				subjectTable.setAutoCreateColumnsFromModel(true);
				subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				subjectTable.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(final MouseEvent e) {
						if (e.getClickCount() == 1) {
							int row = subjectTable.getSelectedRow();
							int column = subjectTable.getSelectedColumn();
							String abbr = (String) subjectTable.getValueAt(row, 1);
							oldTeacherAbbr = abbr;
							if (column == 2) {
								oldValue = Integer.parseInt(subjectTable.getValueAt(row, column).toString().trim());
							}
							controller.setSelectedTeacher(abbr);
						}
					}
				});

				subjectTable.getModel().addTableModelListener(this);
				subjectTable.addMouseListener(popClickListener);

				JScrollPane scrollPanesubjectTable = new JScrollPane(subjectTable);
				scrollPanesubjectTable.addMouseListener(popClickListener);
				subjectPanel.add(scrollPanesubjectTable);
				subjectsPanel.add(subjectPanel);

				// hash map to link subject name to table
				subjectTablesMap.put(subject.getName(), subjectTable);
			}
			classTablesMap.put(localClassName, subjectTablesMap);

			// Layout for right part (subjects)
			groupLayout.setHorizontalGroup(groupLayout.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(titlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
							GroupLayout.PREFERRED_SIZE)
					.addComponent(subjectsPanel, GroupLayout.PREFERRED_SIZE, screenWidth - 100,
							GroupLayout.PREFERRED_SIZE));
			groupLayout.setVerticalGroup(groupLayout.createSequentialGroup()
					.addComponent(titlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
							GroupLayout.PREFERRED_SIZE)
					.addComponent(subjectsPanel, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE));
		}

		// Layout for left part (teacher info, teacher list, save button)
		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
								.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE)
								.addComponent(teacherListPanel, GroupLayout.PREFERRED_SIZE, 300,
										GroupLayout.PREFERRED_SIZE)
						.addComponent(ioPanel, GroupLayout.PREFERRED_SIZE, 300, GroupLayout.PREFERRED_SIZE))
				.addComponent(classCards, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
						GroupLayout.PREFERRED_SIZE)));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)
						.addGroup(layout.createSequentialGroup()
								.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE, screenHeight / 4,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(teacherListPanel, screenHeight / 2 + 115, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(ioPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
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
		teacherInfoTableModel.addColumn("Stunden");

		if (selectedTeacher != null) {
			for (SchoolGroup group : selectedTeacher.getSchoolGroups()) {
				teacherInfoTableModel.addRow(
						new Object[] { group.getSubject().getName(), group.getName(), group.getHoursOnSubject() });

			}
			selectedTeacherAbbrLabel.setText(selectedTeacher.getAbbr());
			selectedTeacherNameLabel.setText(selectedTeacher.getSurname() + ", " + selectedTeacher.getFirstname());
			selectedTeacherToDoLabel.setText(selectedTeacher.getToDo() + "");
			selectedTeacherSchoolTypeLabel.setText(selectedTeacher.getSchoolType());
			selectedTeacherActDoLabel.setText(selectedTeacher.getActDo() + "");

			int selectionIndex = teacherListMap.get(selectedTeacher);
			teacherList.setSelectedIndex(selectionIndex);
			teacherTable.getSelectionModel().setSelectionInterval(selectionIndex - 1, selectionIndex);
		}
		teacherInfoTable.setModel(teacherInfoTableModel);
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		int col = e.getColumn();
		int row = e.getFirstRow();
		TableModel cellModel = (TableModel) e.getSource();
		if (col == 2) {
			int newValue = Integer.parseInt(cellModel.getValueAt(row, col).toString().trim());
			controller.teacherAddToActDo(model.getSelectedTeacher(), newValue - oldValue);
		}
		if (col == 1) {
			Teacher oldTeacher = model.getTeacherByAbbr(oldTeacherAbbr);
			String newTeacherAbbr = cellModel.getValueAt(row, col).toString().trim();
			Teacher newTeacher = model.getTeacherByAbbr(newTeacherAbbr);
			if (newTeacher != null && !newTeacher.equals(oldTeacher)) {
				int value = Integer.parseInt(cellModel.getValueAt(row, 2).toString().trim());
				if (oldTeacher != null)
					oldTeacher.subtractFromActDo(value);
				newTeacher.addToActDo(value);
			}
			controller.setSelectedTeacher(newTeacher);
		}
	}

	@Override
	public void exportTableDataToCVS(String className, boolean fileChooser) {
		HashMap<String, JTable> subjectTablesMap = classTablesMap.get(className);
		TreeMap<String, JTable> treeMap = new TreeMap<String, JTable>(subjectTablesMap);

		ArrayList<String> content = new ArrayList<String>();

		for (Map.Entry<String, JTable> entry : treeMap.entrySet()) {
			JTable table = entry.getValue();
			TableModel model = table.getModel();

			for (int clmCnt = model.getColumnCount(), rowCnt = model.getRowCount(), i = 0; i < rowCnt; i++) {
				String line = entry.getKey() + ";";
				for (int j = 0; j < clmCnt; j++) {
					if (model.getValueAt(i, j) != null) {
						String value = model.getValueAt(i, j).toString();
						line += value + ";";
					}
				}
				content.add(line);
			}
		}
		exportTableDataToCVS("input/", className + ".csv", "Fach;Gruppe;Stunden;Lehrer;", content, fileChooser);
	}
	
	@Override
	public JPopupMenu createPopUpMenu(final MouseEvent e) {
		popupMenu = super.createPopUpMenu(e);

		JMenuItem printItem = new JMenuItem("Ansicht drucken");
		popupMenu.add(printItem);
		printItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				print(cards, model.getName() + " " + model.getSelectedClass());
			}
		});
		return (popupMenu);
	}

}
