package de.schooladmin.teachingtime;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import de.schooladmin.Teacher;
import de.schooladmin.View;

public class ViewTeachingTime extends View implements ViewTeachingTimeInterface {

	public JPanel overviewPanel;

	protected JTable teacherInfoTable;

	JTextField selectedTeacherFirstnameField;
	JTextField selectedTeacherNameField;
	JTextField selectedTeacherAbbrField;
	JTextField selectedTeacherSchoolTypeField;
	JTextField selectedTeacherToDoField;
	JTextField selectedTeacherActDoField;

	private static final long serialVersionUID = 1L;
	ModelTeachingTimeInterface model;
	ControllerTeachingTimeInterface controller;

	public ViewTeachingTime(ControllerTeachingTimeInterface controller, ModelTeachingTimeInterface model) {
		super(controller, model);
		this.model = model;
		this.controller = controller;
		this.popClickListener = new PopClickListener();
	}

	public JMenuBar createMenuBar() {
		super.createMenuBar();
		// menuFile.add(new JSeparator());
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
			}
		});

		return menuBar;

	}

	public void createCards() {
		cards.add(viewPanel, "viewPanel");
		createOverView();
		cards.add(overviewPanel, "overviewPanel");
	}

	public void createOverView() {
		overviewPanel = new JPanel();
		initPanelLayout(overviewPanel);

		// teacher list
		JPanel panelTeacher = new JPanel(new GridLayout(1, 0));
		panelTeacher.setBorder(BorderFactory.createTitledBorder("Lehrer"));
		final JList<String> teacherList = createTeacherNameList();
		JScrollPane scrollPaneTeacherList = new JScrollPane(teacherList);
		panelTeacher.add(scrollPaneTeacherList);
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
		teacherList.addMouseListener(new PopClickListener());

		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		infoPanel.setBorder(BorderFactory.createTitledBorder("Lehrerinfos"));

		selectedTeacherFirstnameField = new JTextField("");
		selectedTeacherNameField = new JTextField("");
		selectedTeacherNameField
				.setPreferredSize(new Dimension(120, selectedTeacherNameField.getPreferredSize().height));
		selectedTeacherAbbrField = new JTextField("");
		selectedTeacherSchoolTypeField = new JTextField("");
		selectedTeacherToDoField = new JTextField("");
		selectedTeacherActDoField = new JTextField("");
		selectedTeacherActDoField.setEditable(false);
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.7;
		c.weighty = 2.0;
		c.gridx = 0;
		c.gridy = 0;
		infoPanel.add(new JLabel("Abk\u00fcrzung:"), c);
		c.gridx = 1;
		infoPanel.add(selectedTeacherAbbrField, c);
		c.gridx = 0;
		c.gridy = 1;
		infoPanel.add(new JLabel("Vorname:"), c);
		c.gridx = 1;
		infoPanel.add(selectedTeacherFirstnameField, c);
		c.gridx = 0;
		c.gridy = 2;
		infoPanel.add(new JLabel("Name:"), c);
		c.gridx = 1;
		infoPanel.add(selectedTeacherNameField, c);
		c.gridx = 0;
		c.gridy = 3;
		infoPanel.add(new JLabel("Schulform:"), c);
		c.gridx = 1;
		infoPanel.add(selectedTeacherSchoolTypeField, c);
		c.gridx = 0;
		c.gridy = 4;
		infoPanel.add(new JLabel("Sollstunden:"), c);
		c.gridx = 1;
		infoPanel.add(selectedTeacherToDoField, c);
		c.gridx = 0;
		c.gridy = 5;
		infoPanel.add(new JLabel("Einsatz:"), c);
		c.gridx = 1;
		infoPanel.add(selectedTeacherActDoField, c);

		// teacher info table
		JPanel panelTeacherInfo = new JPanel(new GridLayout(1, 0));
		panelTeacherInfo.setBorder(BorderFactory.createTitledBorder("Einsatz"));
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

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, panelTeacher.getPreferredSize().width,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE, infoPanel.getPreferredSize().width + 20,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(panelTeacherInfo));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, screenHeight,
								GroupLayout.PREFERRED_SIZE)
				.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE)
				.addComponent(panelTeacherInfo, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE)));
	}

	@Override
	public void update() {
		super.update();
		Teacher selectedTeacher = model.getSelectedTeacher();
		if (selectedTeacher != null) {
			selectedTeacherAbbrField.setText(selectedTeacher.getAbbr());
			selectedTeacherNameField.setText(selectedTeacher.getSurname());
			selectedTeacherFirstnameField.setText(selectedTeacher.getFirstname());
			selectedTeacherSchoolTypeField.setText(selectedTeacher.getSchoolType());
			selectedTeacherToDoField.setText(selectedTeacher.getToDo() + "");
			selectedTeacherActDoField.setText(selectedTeacher.getActDo() + "");
		}
	}

}
