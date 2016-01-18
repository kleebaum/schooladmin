package de.schooladmin.teachingtime;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.util.ArrayList;

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

	ArrayList<JTextField> teacherDataFields;
	ArrayList<JLabel> teacherDataLabels;
	
	JLabel nameLabel;
	private JLabel schoolTypeLabel;
	private JLabel genderLabel;

	private static final long serialVersionUID = 1L;
	ModelTeachingTimeInterface model;
	ControllerTeachingTimeInterface controller;

	private JLabel birthdayLabel;

	private JLabel ageLabel;

	private JLabel schoolAgeLabel;

	private JLabel teachingTimeLabel;


	private JLabel curSum1Label;

	private JLabel curSum2Label;

	private JLabel seniorReductionLabel;	

	public ViewTeachingTime(ControllerTeachingTimeInterface controller, ModelTeachingTimeInterface model) {
		super(controller, model);
		this.model = model;
		this.controller = controller;
		this.popClickListener = new PopClickListener();
		this.teacherDataFields = new ArrayList<JTextField>();
		this.teacherDataLabels = new ArrayList<JLabel>();
		for (String teacherDataEntry : model.getTeacherDataMap().keySet()) {
			this.teacherDataFields.add(new JTextField(""));
			this.teacherDataLabels.add(new JLabel(teacherDataEntry));
		}
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

		GridLayout gridLayout = new GridLayout(0, 2);
		JPanel infoPanel = new JPanel();
		infoPanel.setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		infoPanel.setBorder(BorderFactory.createTitledBorder("Lehrer-Daten"));
		
		c.anchor = GridBagConstraints.WEST;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.weightx = 1.7;
		c.weighty = 2.0;
		
		teacherDataFields.get(0).setPreferredSize(new Dimension(120, teacherDataFields.get(0).getPreferredSize().height));
		for (int i = 0; i < teacherDataFields.size(); i++) {
			c.gridx = 0;
			c.gridy = i;
			infoPanel.add(this.teacherDataLabels.get(i), c);
			c.gridx = 1;
			infoPanel.add(this.teacherDataFields.get(i), c);
		}
		
		//gridLayout.setHgap(10);
		//JLabel emptyLabel = new JLabel("");
		// Grund-Daten
		JPanel panelBasicData = new JPanel(gridLayout);
		nameLabel = new JLabel("");
		genderLabel = new JLabel("");
		schoolTypeLabel = new JLabel("");
		birthdayLabel = new JLabel("");
		ageLabel = new JLabel("");
		schoolAgeLabel = new JLabel("");
		panelBasicData.add(nameLabel);
		panelBasicData.add(genderLabel);
		panelBasicData.add(schoolTypeLabel);
		panelBasicData.add(birthdayLabel);
		panelBasicData.add(ageLabel);
		panelBasicData.add(schoolAgeLabel);
		panelBasicData.setBorder(BorderFactory.createTitledBorder("Grund-Daten"));
		
		// Unterrichtspflichzeit
		JPanel panelTeachingTime = new JPanel(gridLayout);
		panelTeachingTime.setBorder(BorderFactory.createTitledBorder("Unterrichtspflichzeit"));
		teachingTimeLabel = new JLabel("");
		curSum1Label = new JLabel("");
		curSum1Label.setHorizontalAlignment(JLabel.RIGHT);
		panelTeachingTime.add(teachingTimeLabel);
		panelTeachingTime.add(curSum1Label);
		
		// Altersermaessigung
		JPanel panelSeniorReduction = new JPanel(gridLayout);
		seniorReductionLabel = new JLabel("");
		curSum2Label = new JLabel("");
		curSum2Label.setHorizontalAlignment(JLabel.RIGHT);
		panelSeniorReduction.add(seniorReductionLabel);
		panelSeniorReduction.add(curSum2Label);
		panelSeniorReduction.setBorder(BorderFactory.createTitledBorder("Alterserm\u00e4\u00dfigung"));
		
		// Anrechnung
		JPanel panelPlusMinus = new JPanel(new GridLayout(1, 0));
		panelPlusMinus.setBorder(BorderFactory.createTitledBorder("Anrechnung"));
		
		// Einsatz
		JPanel panelActDo = new JPanel(new GridLayout(1, 0));
		panelActDo.setBorder(BorderFactory.createTitledBorder("Einsatz"));
		
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
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING).addComponent(panelBasicData)
						.addComponent(panelTeachingTime)
						.addComponent(panelSeniorReduction)
						.addComponent(panelPlusMinus)
						.addComponent(panelActDo))
				.addComponent(panelTeacherInfo));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, screenHeight,
								GroupLayout.PREFERRED_SIZE)
				.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createSequentialGroup().addComponent(panelBasicData)
						.addComponent(panelTeachingTime).addComponent(panelSeniorReduction)
						.addComponent(panelPlusMinus)
						.addComponent(panelActDo))
				.addComponent(panelTeacherInfo, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE)));
	}

	@Override
	public void update() {
		super.update();
		Teacher selectedTeacher = model.getSelectedTeacher();
		if (selectedTeacher != null) {
			for (int i = 0; i < teacherDataFields.size(); i++) {
				teacherDataFields.get(i).setText(selectedTeacher.getTeacherData().get(i));
			}
			
			nameLabel.setText(selectedTeacher.getFirstname() + " " + selectedTeacher.getSurname());
			String gender = selectedTeacher.getGender();
			if (gender.substring(0, 1).equals("m"))
				genderLabel.setText("m\u00e4nnlich");
			else
				genderLabel.setText("weiblich");
			schoolTypeLabel.setText(selectedTeacher.getSchoolType());
			birthdayLabel.setText(selectedTeacher.getBirthday());
			
			ageLabel.setText("Alter: " + selectedTeacher.getAge() + "");
			schoolAgeLabel.setText("Schuljahr-Alter: " + selectedTeacher.getSchoolAge() + "");	
			
			double teachingTime = model.getSchoolTypesMap().get(selectedTeacher.getSchoolType());
			teachingTimeLabel.setText("Unterrichtspflichtzeit: " + teachingTime);
			curSum1Label.setText("Zw.Su.=" + teachingTime + "");
			
			seniorReductionLabel.setText("Alterserm.: " + selectedTeacher.seniorReduction);
			curSum2Label.setText("Zw.Su.=" + teachingTime + "");
		}
	}

}
