package de.schooladmin.deployment;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
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
	ArrayList<JPanel> classPanels = new ArrayList<JPanel>();
	ArrayList<JTable> subjectTables = new ArrayList<JTable>();
	HashMap<String, HashMap<String, JTable>> classTablesMap = new HashMap<String, HashMap<String, JTable>>();

	public JLabel selectedTeacherAbbrLabel;
	public JLabel selectedTeacherNameLabel;
	public JLabel selectedTeacherToDoLabel;
	public JLabel selectedTeacherSchoolTypeLabel;
	public JLabel selectedTeacherActDoLabel;
	public JPanel infoPanel;
	public JPanel overviewPanel;
	public JPanel teacherListPanel;
	public JPanel ioPanel;

	public JMenu overviewMenu;

	JList<String> teacherSpmList;
	JList<String> teacherDataList;
	JTable teacherTable;
	JTable teacherInfoTable;

	public int oldValue = 0;
	public String oldTeacherAbbr = "";

	public ViewDeployment(ControllerDeploymentInterface controller, ModelDeploymentInterface model) {
		super(controller, model);
		this.model = model;
		this.controller = controller;
	}

	public void createCards() {
		cards.add(viewPanel, "viewPanel");
		createOverView();
		cards.add(overviewPanel, "overviewPanel");
		createClassesView();
	}

	public JMenuBar createMenuBar() {

		overviewMenu = new JMenu("\u00dcberblick");

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
				selectedClassName = "";
				// scrollPane.setSize(viewFrame.getPreferredSize());
			}
		});

		return menuBar;

	}

	public DefaultTableModel teacherTableModel() {
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
		return (teacherTableModel);
	}

	public DefaultTableModel teacherInfoTableModel() {
		DefaultTableModel teacherTableModel = new DefaultTableModel();
		teacherTableModel.addColumn("Fach");
		teacherTableModel.addColumn("Gruppe");
		teacherTableModel.addColumn("Stunden");
		Teacher teacher = model.getSelectedTeacher();
		if (teacher != null) {
			for (SchoolGroup group : teacher.getSchoolGroups()) {
				teacherTableModel.addRow(new Object[] { group.getSubject().getName(), group.getName(), group.getHoursOnSubject() });
			}
		}
		return (teacherTableModel);

	}

	public void createOverView() {
		overviewPanel = new JPanel();
		initPanelLayout(overviewPanel);

		// teacher table
		JPanel panelTeacher = new JPanel(new GridLayout(1, 0));
		teacherTable = new JTable(teacherTableModel()) {
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
					Teacher teacher = model.getTeacherByAbbr(abbr);
					controller.setSelectedTeacher(teacher);
					teacherInfoTable.setModel(teacherInfoTableModel());
				}
			}

		});
		JScrollPane scrollPaneTeacherTable = new JScrollPane(teacherTable);
		panelTeacher.add(scrollPaneTeacherTable);

		// teacher table
		JPanel panelTeacherInfo = new JPanel(new GridLayout(1, 0));
		teacherInfoTable = new JTable(teacherInfoTableModel()) {
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

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, screenWidth-300, GroupLayout.PREFERRED_SIZE)
				.addComponent(panelTeacherInfo, GroupLayout.PREFERRED_SIZE, 250, GroupLayout.PREFERRED_SIZE));

		layout.setVerticalGroup(layout
				.createParallelGroup(GroupLayout.Alignment.LEADING)
				.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE)
				.addComponent(panelTeacherInfo, GroupLayout.PREFERRED_SIZE, screenHeight,
						GroupLayout.PREFERRED_SIZE));
	}

	public void createClassesView() {
		ArrayList<SchoolClass> classes = model.getClasses();

		for (int i = 0; i < classes.size(); i++) {
			HashMap<String, JTable> subjectTablesMap = new HashMap<String, JTable>();
			final SchoolClass localClass = classes.get(i);
			final String localClassName = localClass.getName();

			JPanel localPanel = new JPanel();
			initPanelLayout(localPanel);

			classPanels.add(localPanel);
			cards.add(localPanel, localClassName);

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
					cardLayout.show(cards, localClassName);
					selectedClassName = localClassName;

				}
			});

			// Label
			JLabel titleLabel = new JLabel(localClassName);
			titleLabel.setFont(titleFont);
			JPanel titlePanel = new JPanel();
			titlePanel.setLayout(new GridLayout(1, 0));
			titlePanel.add(titleLabel);

			infoPanel = new JPanel();
			infoPanel.setLayout(new GridBagLayout());
			GridBagConstraints c = new GridBagConstraints();
			infoPanel.setBorder(BorderFactory.createTitledBorder("Lehrerinfos"));

			final JLabel teacherNameLabel = new JLabel("");
			final JLabel teacherAbbrLabel = new JLabel("");
			final JLabel teacherSchoolTypeLabel = new JLabel("");
			final JLabel teacherToDoLabel = new JLabel("");
			final JLabel teacherActDoLabel = new JLabel("");
			c.anchor = GridBagConstraints.WEST;
			c.fill = GridBagConstraints.HORIZONTAL;
			// c.gridwidth = 2;
			c.weightx = 1.7;
			c.weighty = 2.0;
			c.gridx = 0;
			c.gridy = 0;
			infoPanel.add(new JLabel("Abk\u00fcrzung:"), c);
			c.gridx = 1;
			infoPanel.add(teacherAbbrLabel, c);
			c.gridx = 0;
			c.gridy = 1;
			infoPanel.add(new JLabel("Name:"), c);
			c.gridx = 1;
			infoPanel.add(teacherNameLabel, c);
			c.gridx = 0;
			c.gridy = 2;
			infoPanel.add(new JLabel("Schulform:"), c);
			c.gridx = 1;
			infoPanel.add(teacherSchoolTypeLabel, c);
			c.gridx = 0;
			c.gridy = 3;
			infoPanel.add(new JLabel("Sollstunden:"), c);
			c.gridx = 1;
			infoPanel.add(teacherToDoLabel, c);
			c.gridx = 0;
			c.gridy = 4;
			infoPanel.add(new JLabel("Einsatz:"), c);
			c.gridx = 1;
			infoPanel.add(teacherActDoLabel, c);

			teacherListPanel = new JPanel();
			teacherListPanel.setLayout(new GridLayout(0, 1));
			teacherListPanel.setBorder(BorderFactory.createTitledBorder("Lehrerliste"));

			final JList<String> teacherList = createTeacherNameList();
			JScrollPane teacherListScrollPane = new JScrollPane(teacherList);
			teacherListPanel.add(teacherListScrollPane);
			teacherList.addListSelectionListener(new ListSelectionListener() {
				public void valueChanged(ListSelectionEvent evt) {
					if (!evt.getValueIsAdjusting()) {
						String abbr = teacherList.getSelectedValue().split(" ")[0];
						Teacher selectedTeacher = model.getTeacherByAbbr(abbr);
						if (selectedTeacher != null) {
							controller.setSelectedTeacher(selectedTeacher);
							selectedTeacherNameLabel = teacherNameLabel;
							selectedTeacherAbbrLabel = teacherAbbrLabel;
							selectedTeacherSchoolTypeLabel = teacherSchoolTypeLabel;
							selectedTeacherToDoLabel = teacherToDoLabel;
							selectedTeacherActDoLabel = teacherActDoLabel;
							updateTeacherInfo();
						}
					}
				}
			});

			ioPanel = new JPanel();
			ioPanel.setLayout(new GridLayout(0, 1));
			ioPanel.setBorder(BorderFactory.createTitledBorder("Dateiausgabe"));

			// subjects panel
			JPanel subjectsPanel = new JPanel(new GridLayout(3, 0));
			ArrayList<SchoolSubject> subjects = localClass.getSubjects();

			for (SchoolSubject subject : subjects) {
				JPanel subjectPanel = new JPanel(new BorderLayout());
				subjectPanel.setBorder(BorderFactory.createTitledBorder(subject.getName()));
				DefaultTableModel tableModel = new DefaultTableModel();
				tableModel.addColumn("Gruppe");
				tableModel.addColumn("Lehrer");
				tableModel.addColumn("Stunden");
				for (SchoolGroup group : subject.getSchoolGroups()) {
					tableModel
							.addRow(new Object[] { group.getName(), group.getTeacherAbbr(), group.getHoursOnSubject() });
				}
				final JTable subjectTable = new JTable(tableModel);
				subjectTable.setFont(bigFont);
				subjectTable.setAutoCreateColumnsFromModel(true);
				// subjectTable.setCellSelectionEnabled(true);
				subjectTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
				subjectTable.addMouseListener(new MouseAdapter() {
					@Override
					public void mouseClicked(final MouseEvent e) {
						if (e.getClickCount() == 1) {
							int row = subjectTable.getSelectedRow();
							int column = subjectTable.getSelectedColumn();
							String abbr = (String) subjectTable.getValueAt(row, 1);
							oldTeacherAbbr = abbr;
							Teacher selectedTeacher = model.getTeacherByAbbr(abbr);
							controller.setSelectedTeacher(selectedTeacher);
							if (selectedTeacher != null) {
								selectedTeacherNameLabel = teacherNameLabel;
								selectedTeacherAbbrLabel = teacherAbbrLabel;
								selectedTeacherSchoolTypeLabel = teacherSchoolTypeLabel;
								selectedTeacherToDoLabel = teacherToDoLabel;
								selectedTeacherActDoLabel = teacherActDoLabel;
								updateTeacherInfo();
							}
							if (column == 2) {
								oldValue = Integer.parseInt(subjectTable.getValueAt(row, column).toString().trim());
							}
						}
					}
				});

				subjectTable.getModel().addTableModelListener(this);

				JScrollPane scrollPanesubjectTable = new JScrollPane(subjectTable);
				subjectPanel.add(scrollPanesubjectTable);
				subjectsPanel.add(subjectPanel);
				subjectTables.add(subjectTable);
				subjectTablesMap.put(subject.getName(), subjectTable);

			}
			classTablesMap.put(localClassName, subjectTablesMap);

			JButton saveButton = new JButton("speichern");
			saveButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(final MouseEvent e) {
					if (e.getClickCount() == 1) {
						exportTableDataToCVS(localClassName);
					}
				}
			});
			ioPanel.add(saveButton);

			layout.setHorizontalGroup(layout
					.createParallelGroup(GroupLayout.Alignment.CENTER)
					.addComponent(titlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
							GroupLayout.PREFERRED_SIZE)
					.addGroup(
							layout.createSequentialGroup()
									.addGroup(
											layout.createParallelGroup(GroupLayout.Alignment.LEADING)
													.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE, 300,
															GroupLayout.PREFERRED_SIZE)
													.addComponent(teacherListPanel, GroupLayout.PREFERRED_SIZE, 300,
															GroupLayout.PREFERRED_SIZE)
													.addComponent(ioPanel, GroupLayout.PREFERRED_SIZE, 300,
															GroupLayout.PREFERRED_SIZE))
									.addComponent(subjectsPanel, GroupLayout.PREFERRED_SIZE, screenWidth - 100,
											GroupLayout.PREFERRED_SIZE)));

			layout.setVerticalGroup(layout
					.createSequentialGroup()
					.addComponent(titlePanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
							GroupLayout.PREFERRED_SIZE)
					.addGroup(
							layout.createParallelGroup(GroupLayout.Alignment.CENTER)
									.addGroup(
											layout.createSequentialGroup()
													.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE,
															screenHeight / 4, GroupLayout.PREFERRED_SIZE)
													.addComponent(teacherListPanel, screenHeight / 2 + 115,
															GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE)
													.addComponent(ioPanel, GroupLayout.PREFERRED_SIZE,
															GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE))
									.addComponent(subjectsPanel, GroupLayout.PREFERRED_SIZE, screenHeight - 13,
											GroupLayout.PREFERRED_SIZE)));
		}

	}

	@Override
	public void update() {
		super.update();
		Teacher selectedTeacher = model.getSelectedTeacher();
		if (selectedTeacher != null) {

		}
	}

	@Override
	public void tableChanged(TableModelEvent e) {
		int col = e.getColumn();
		int row = e.getFirstRow();
		TableModel cellModel = (TableModel) e.getSource();
		if (col == 2) {
			int newValue = Integer.parseInt(cellModel.getValueAt(row, col).toString().trim());
			model.getSelectedTeacher().addToActDo(newValue - oldValue);
			selectedTeacherActDoLabel.setText(model.getSelectedTeacher().getActDo() + "");
			teacherTable.setModel(teacherTableModel());
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
				Teacher selectedTeacher = model.getSelectedTeacher();
				if (selectedTeacher != null)
				selectedTeacherActDoLabel.setText(selectedTeacher.getActDo() + "");
				teacherTable.setModel(teacherTableModel());
			}
		}
	}

	public void exportTableDataToCVS(String className) {
		try {
			final JFileChooser fc = new JFileChooser();

			fc.setCurrentDirectory(new File("input/"));
			File file = new File("input/" + className + ".csv");
			fc.setSelectedFile(file);
			int retrival = fc.showSaveDialog(null);
			if (retrival == JFileChooser.APPROVE_OPTION) {
				file = fc.getSelectedFile();
				file.createNewFile();

				BufferedWriter bw = new BufferedWriter((new OutputStreamWriter(new FileOutputStream(file),
						StandardCharsets.ISO_8859_1)));
				bw.write("Fach;Gruppe;Stunden;Lehrer;");
				bw.newLine();

				HashMap<String, JTable> subjectTablesMap = classTablesMap.get(className);

				System.out.println(subjectTablesMap.entrySet());
				TreeMap<String, JTable> treeMap = new TreeMap<String, JTable>(subjectTablesMap);
				for (Map.Entry<String, JTable> entry : treeMap.entrySet()) {
					System.out.println(entry.getKey());
					JTable table = entry.getValue();
					TableModel model = table.getModel();

					for (int clmCnt = model.getColumnCount(), rowCnt = model.getRowCount(), i = 0; i < rowCnt; i++) {
						bw.write(entry.getKey() + ";");
						for (int j = 0; j < clmCnt; j++) {
							if (model.getValueAt(i, j) != null) {
								String value = model.getValueAt(i, j).toString();
								bw.write(value + ";");
							}
						}
						bw.newLine();
					}
				}
				bw.flush();
				bw.close();
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	void updateTeacherInfo() {
		Teacher selectedTeacher = model.getSelectedTeacher();
		selectedTeacherAbbrLabel.setText(selectedTeacher.getAbbr());
		selectedTeacherNameLabel.setText(selectedTeacher.getSurname() + ", " + selectedTeacher.getFirstname());
		selectedTeacherToDoLabel.setText(selectedTeacher.getToDo() + "");
		selectedTeacherSchoolTypeLabel.setText(selectedTeacher.getSchoolType());
		selectedTeacherActDoLabel.setText(selectedTeacher.getActDo() + "");
	}

	public void exitProgram() {
		if (!selectedClassName.equals("")) {
			exportTableDataToCVS(selectedClassName);
		}
	}

}
