package de.schooladmin.teachingtime;

import java.awt.Desktop;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import de.schooladmin.SchoolType;
import de.schooladmin.Teacher;
import de.schooladmin.View;

public class ViewTeachingTime extends View implements ViewTeachingTimeInterface {

	public JPanel overviewPanel;

	ArrayList<JTextField> teacherDataFields;
	ArrayList<JLabel> teacherDataLabels;
	ArrayList<JLabel> contentLabels;
	ArrayList<JLabel> curSumLabels;
	ArrayList<Double> curSums;

	private static final long serialVersionUID = 1L;
	ModelTeachingTimeInterface model;
	ControllerTeachingTimeInterface controller;

	private JTextArea teacherInfoText;

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
		this.curSums = new ArrayList<Double>();
		this.curSumLabels = new ArrayList<JLabel>();
		this.contentLabels = new ArrayList<JLabel>();
		for (int i = 0; i < 30; i++) {
			JLabel newContentLabel = new JLabel("");
			this.contentLabels.add(newContentLabel);
		}
		for (int i = 0; i < 25; i++) {
			JLabel newCurSumLabel = new JLabel("");
			newCurSumLabel.setHorizontalAlignment(JLabel.RIGHT);
			this.curSumLabels.add(newCurSumLabel);
			this.curSums.add(0.0);
		}
	}

	public JMenuBar createMenuBar() {
		super.createMenuBar();
		JMenuItem menuItemSave = new JMenuItem("Speichern");
		menuFile.add(menuItemSave);
		menuItemSave.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportTeacherDataToCVS(model.getFileTeachers(), false);
			}
		});

		JMenuItem menuItemSaveAs = new JMenuItem("Speichern unter");
		menuFile.add(menuItemSaveAs);
		menuItemSaveAs.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				exportTeacherDataToCVS(model.getFileTeachers(), true);
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

		final JMenu overviewMenu = new JMenu("\u00dcberblick");

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

				final Teacher selectedTeacher = model.getSelectedTeacher();
				if (selectedTeacher != null) {
					overviewMenu.removeAll();
					JMenuItem openSelectedTeacherInfos = new JMenuItem(
							"Lehrer-Infos von " + selectedTeacher.getSurname() + " in Editor \u00f6ffnen");
					overviewMenu.add(openSelectedTeacherInfos);
					openSelectedTeacherInfos.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							PrintWriter w;

							try {
								File datei = File.createTempFile("plan", ".txt");
								w = new PrintWriter(new BufferedWriter(new FileWriter(datei)));

								w.print(getTeacherInfoText(selectedTeacher));
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
				}
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
					controller.setSelectedTeacher(
							model.getSchool().getTeacherByAbbr(teacherList.getSelectedValue().split(" ")[0]));
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

		for (int i = 0; i < teacherDataFields.size(); i++) {
			c.gridx = 0;
			c.gridy = i;
			infoPanel.add(this.teacherDataLabels.get(i), c);
			c.gridx = 1;
			teacherDataFields.get(i).setPreferredSize(new Dimension(180, 20));
			infoPanel.add(this.teacherDataFields.get(i), c);
			this.teacherDataFields.get(i).addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					controller.setSelectedTeacher(model.getSelectedTeacher());
				}

			});
		}

		// Grund-Daten
		JPanel panelBasicData = new JPanel(gridLayout);
		panelBasicData.add(contentLabels.get(0));
		panelBasicData.add(contentLabels.get(1));
		panelBasicData.add(contentLabels.get(2));
		panelBasicData.add(contentLabels.get(3));
		panelBasicData.add(contentLabels.get(4));
		panelBasicData.add(contentLabels.get(5));
		panelBasicData.setBorder(BorderFactory.createTitledBorder("Grund-Daten"));

		// Unterrichtspflichzeit
		JPanel panelTeachingTime = new JPanel(gridLayout);
		panelTeachingTime.setBorder(BorderFactory.createTitledBorder("Unterrichtspflichzeit"));
		panelTeachingTime.add(contentLabels.get(6));
		panelTeachingTime.add(curSumLabels.get(0));
		panelTeachingTime.add(contentLabels.get(7));
		panelTeachingTime.add(curSumLabels.get(1));
		panelTeachingTime.add(contentLabels.get(8));
		panelTeachingTime.add(curSumLabels.get(2));
		panelTeachingTime.add(contentLabels.get(9));
		panelTeachingTime.add(curSumLabels.get(3));

		// Anrechnung
		JPanel panelPlusMinus = new JPanel(gridLayout);
		panelPlusMinus.setBorder(BorderFactory.createTitledBorder("Anrechnung"));
		panelPlusMinus.add(contentLabels.get(11));
		panelPlusMinus.add(new JLabel(""));

		for (int i = 0; i < 8; i++) {
			panelPlusMinus.add(contentLabels.get(12 + i));
			panelPlusMinus.add(curSumLabels.get(4 + i));
		}

		// Einsatz
		JPanel panelActDo = new JPanel(gridLayout);
		panelActDo.setBorder(BorderFactory.createTitledBorder("Einsatz"));
		panelActDo.add(contentLabels.get(10));
		panelActDo.add(new JLabel(""));
		panelActDo.add(contentLabels.get(20));
		panelActDo.add(new JLabel(""));
		panelActDo.add(contentLabels.get(21));
		panelActDo.add(new JLabel(""));
		panelActDo.add(contentLabels.get(22));
		panelActDo.add(new JLabel(""));
		panelActDo.add(contentLabels.get(23));
		panelActDo.add(new JLabel(""));

		// teacher text area
		JPanel panelTeacherInfo = new JPanel(new GridLayout(1, 0));
		panelTeacherInfo.setBorder(BorderFactory.createTitledBorder("Einsatz"));
		teacherInfoText = new JTextArea();
		teacherInfoText.addMouseListener(new PopClickListener());
		teacherInfoText.setEditable(false);
		teacherInfoText.setFont(monospacedFont);
		JScrollPane scrollPaneTeacherInfoTable = new JScrollPane(teacherInfoText);
		panelTeacherInfo.add(scrollPaneTeacherInfoTable);

		layout.setHorizontalGroup(layout.createSequentialGroup()
				.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, panelTeacher.getPreferredSize().width,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE, infoPanel.getPreferredSize().width + 20,
						GroupLayout.PREFERRED_SIZE)
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.TRAILING)
						.addComponent(panelBasicData, GroupLayout.PREFERRED_SIZE, screenWidth / 4,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(panelTeachingTime, GroupLayout.PREFERRED_SIZE, screenWidth / 4,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(panelPlusMinus, GroupLayout.PREFERRED_SIZE, screenWidth / 4,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(panelActDo, GroupLayout.PREFERRED_SIZE, screenWidth / 4,
								GroupLayout.PREFERRED_SIZE))
				.addComponent(panelTeacherInfo));

		layout.setVerticalGroup(layout.createSequentialGroup()
				.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
								.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, screenHeight,
										GroupLayout.PREFERRED_SIZE)
						.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE)
						.addGroup(layout.createSequentialGroup()
								.addComponent(panelBasicData, GroupLayout.PREFERRED_SIZE, (screenHeight - 20) / 4,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(panelTeachingTime, GroupLayout.PREFERRED_SIZE, (screenHeight - 20) / 4,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(panelPlusMinus, GroupLayout.PREFERRED_SIZE, (screenHeight - 20) / 4,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(panelActDo, GroupLayout.PREFERRED_SIZE, (screenHeight - 20) / 4,
										GroupLayout.PREFERRED_SIZE))
								.addComponent(panelTeacherInfo, GroupLayout.PREFERRED_SIZE, screenHeight,
										GroupLayout.PREFERRED_SIZE))));
	}

	@Override
	public void update() {
		super.update();
		Teacher oldSelectedTeacher = model.getOldSelectedTeacher();
		if (oldSelectedTeacher != null) {
			for (int i = 0; i < teacherDataFields.size(); i++) {
				String fieldText = teacherDataFields.get(i).getText();
				oldSelectedTeacher.getTeacherData().set(i, fieldText);
			}
		}
		Teacher selectedTeacher = model.getSelectedTeacher();
		if (selectedTeacher != null) {
			for (int i = 0; i < teacherDataFields.size(); i++) {
				teacherDataFields.get(i).setText(selectedTeacher.getTeacherData().get(i));
			}

			contentLabels.get(0)
					.setText(selectedTeacher.getTeacherData().get(1) + " " + selectedTeacher.getTeacherData().get(0));
			String gender = selectedTeacher.getTeacherData().get(3);
			if (gender.substring(0, 1).equals("m"))
				contentLabels.get(1).setText("m\u00e4nnlich");
			else
				contentLabels.get(1).setText("weiblich");

			// birthday
			contentLabels.get(3).setText(selectedTeacher.getTeacherData().get(4));

			// age
			contentLabels.get(4).setText("Alter: " + selectedTeacher.getAge() + "");
			int schoolAge = selectedTeacher.getSchoolAge();
			contentLabels.get(5).setText("Schuljahr-Alter: " + schoolAge + "");

			// school type
			SchoolType selectedSchoolType = model.getSchool()
					.getSchoolTypeByAbbr(selectedTeacher.getTeacherData().get(5));

			String schoolTypeName = "";
			Double teachingTime = 0.0;
			Double seniorReduction = 0.0;
			String scientificLessonsString = selectedTeacher.getTeacherData().get(6);
			double scientificLessons = 0.0;
			if (!scientificLessonsString.isEmpty() && !scientificLessonsString.startsWith(" ")
					&& !scientificLessonsString.equals("-")) {
				scientificLessons = Double.parseDouble(scientificLessonsString);
			}
			double teachingTimePerScLes = 0.0;
			if (selectedSchoolType != null) {
				schoolTypeName = selectedSchoolType.getName();
				teachingTime = selectedSchoolType.getTeachingTime();
				seniorReduction = selectedSchoolType.getPartialRetirement(schoolAge);
				teachingTimePerScLes = selectedSchoolType.getTeachingTimePerScLes(scientificLessons);
			}

			contentLabels.get(2).setText(schoolTypeName);

			// teaching time depending on school type
			contentLabels.get(6).setText(schoolTypeName + ": " + teachingTime + " h");
			curSums.set(0, teachingTime);

			// scientific lessons
			contentLabels.get(7).setText("Wissenschaftl. Unterricht: " + scientificLessons + " h");
			curSums.set(1, teachingTimePerScLes);

			// partial retirement
			contentLabels.get(8).setText("");

			if (seniorReduction > 0) {
				contentLabels.get(8).setText("Altersteilzeit mit " + schoolAge + ": " + seniorReduction + " h");
			}
			curSums.set(2, teachingTimePerScLes - seniorReduction);

			// full vs. part time
			String partTimeString = selectedTeacher.getTeacherData().get(7).trim();
			Double partTime = 0.0;
			if (partTimeString.equals("V") || partTimeString.equals("")) {
				contentLabels.get(9).setText("Vollzeit");
				curSums.set(3, curSums.get(2));
			} else {
				contentLabels.get(9).setText("Teilzeit " + partTimeString);
				partTime = Double.parseDouble(partTimeString.substring(1));
				curSums.set(3, partTime);
			}

			// not counted hours
			contentLabels.get(10).setText("Mob: " + selectedTeacher.getTeacherData().get(8) + " h");

			// plus minus
			contentLabels.get(11).setText(selectedTeacher.getTeacherData().get(13));

			// panel actual do vs. to do
			contentLabels.get(20).setText("Soll 1.HJ: " + selectedTeacher.getTeacherData().get(9) + " h");
			contentLabels.get(21).setText("Einsatz 1.HJ: " + selectedTeacher.getTeacherData().get(10) + " h");
			contentLabels.get(22).setText("Soll 2.HJ: " + selectedTeacher.getTeacherData().get(11) + " h");
			contentLabels.get(23).setText("Einsatz 2.HJ: " + selectedTeacher.getTeacherData().get(12) + " h");

			for (int i = 0; i < 8; i++) {
				String valueString = selectedTeacher.getTeacherData().get(14 + i);
				Double value = 0.0;
				if (!valueString.isEmpty() && !valueString.startsWith(" "))
					value = Double.parseDouble(valueString);
				curSums.set(4 + i, curSums.get(3 + i) - value);
				contentLabels.get(12 + i).setText(model.getTeacherDataList().get(14 + i) + ": " + value + " h");
			}

			curSumLabels.get(0).setText("Zw.Su.=" + curSums.get(0) + " h");
			for (int i = 1; i < curSums.size(); i++) {
				if (curSums.get(i).equals(curSums.get(i - 1)))
					curSumLabels.get(i).setText("");
				else
					curSumLabels.get(i).setText("Zw.Su.=" + curSums.get(i) + " h");

				teacherInfoText.setText(selectedTeacher.getTeacherSpmText());

			}
		}
	}

	@Override
	public void exportTeacherDataToCVS(String name, boolean fileChooser) {
		String header = "";
		for (String dataPoint : model.getTeacherDataList()) {
			header += dataPoint + ";";
		}

		ArrayList<String> content = new ArrayList<String>();
		for (Teacher teacher : model.getSchool().getTeachers()) {
			String line = "";
			for (String dataPoint : teacher.getTeacherData()) {
				line += dataPoint + ";";
			}
			content.add(line + ";");
		}

		model.exportToCVS("", name, header + ";", content, fileChooser);
	}

	public String getTeacherInfoText(Teacher teacher) {
		String teacherInfoText = teacher.getSurname() + " " + teacher.getFirstname() + "\t (" + teacher.getAbbr()
				+ ") \t " + contentLabels.get(2).getText() + "\r\n";

		teacherInfoText += teacher.getTeacherSpmText();
		
		teacherInfoText += contentLabels.get(6).getText() + "\r\n";
		teacherInfoText += contentLabels.get(7).getText() + "\r\n";

		return teacherInfoText;

	}

}
