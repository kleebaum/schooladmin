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

	public ViewTeachingTime(ControllerTeachingTimeInterface controller,
			ModelTeachingTimeInterface model) {
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

		JMenuItem menuItemOpenTeacherData = new JMenuItem(
				"Lehrer-Datei \u00f6ffnen");
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
		final JMenuItem openAllTeachersInfos = new JMenuItem(
				"Infos von allen Lehrern in Editor \u00f6ffnen");

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
					overviewMenu.add(openAllTeachersInfos);
					JMenuItem openSelectedTeacherInfos = new JMenuItem(
							"Lehrer-Infos von " + selectedTeacher.getSurname()
									+ " in Editor \u00f6ffnen");
					overviewMenu.add(openSelectedTeacherInfos);
					openSelectedTeacherInfos
							.addActionListener(new ActionListener() {

								@Override
								public void actionPerformed(ActionEvent arg0) {
									PrintWriter w;

									try {
										File datei = File.createTempFile(
												"plan", ".txt");
										w = new PrintWriter(new BufferedWriter(
												new FileWriter(datei)));

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

		overviewMenu.add(openAllTeachersInfos);
		openAllTeachersInfos.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				PrintWriter w;

				String infoText = "";
				for (Teacher teacher : model.getSchool().getTeachers()) {
					infoText += getTeacherInfoText(teacher);
				}

				try {
					File datei = File.createTempFile("plan", ".txt");
					w = new PrintWriter(new BufferedWriter(
							new FileWriter(datei)));

					w.print(infoText);
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
			this.teacherDataFields.get(i).addActionListener(
					new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							controller.setSelectedTeacher(model
									.getSelectedTeacher());
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
		panelBasicData.setBorder(BorderFactory
				.createTitledBorder("Grund-Daten"));

		// Unterrichtspflichzeit
		JPanel panelTeachingTime = new JPanel(gridLayout);
		panelTeachingTime.setBorder(BorderFactory
				.createTitledBorder("Unterrichtspflichzeit"));
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
		panelPlusMinus
				.setBorder(BorderFactory.createTitledBorder("Anrechnung"));
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
		JScrollPane scrollPaneTeacherInfoTable = new JScrollPane(
				teacherInfoText);
		panelTeacherInfo.add(scrollPaneTeacherInfoTable);

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE,
						panelTeacher.getPreferredSize().width,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(infoPanel, GroupLayout.PREFERRED_SIZE,
						infoPanel.getPreferredSize().width + 20,
						GroupLayout.PREFERRED_SIZE)
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.TRAILING)
								.addComponent(panelBasicData,
										GroupLayout.PREFERRED_SIZE,
										screenWidth / 3,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(panelTeachingTime,
										GroupLayout.PREFERRED_SIZE,
										screenWidth / 3,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(panelPlusMinus,
										GroupLayout.PREFERRED_SIZE,
										screenWidth / 3,
										GroupLayout.PREFERRED_SIZE)
								.addComponent(panelActDo,
										GroupLayout.PREFERRED_SIZE,
										screenWidth / 3,
										GroupLayout.PREFERRED_SIZE))
				.addComponent(panelTeacherInfo));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addGroup(
						layout.createParallelGroup(
								GroupLayout.Alignment.LEADING)
								.addGroup(
										layout.createParallelGroup(
												GroupLayout.Alignment.BASELINE)
												.addComponent(
														panelTeacher,
														GroupLayout.PREFERRED_SIZE,
														screenHeight,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(
														infoPanel,
														GroupLayout.PREFERRED_SIZE,
														screenHeight,
														GroupLayout.PREFERRED_SIZE)
												.addGroup(
														layout.createSequentialGroup()
																.addComponent(
																		panelBasicData,
																		GroupLayout.PREFERRED_SIZE,
																		(screenHeight - 20) / 4,
																		GroupLayout.PREFERRED_SIZE)
																.addComponent(
																		panelTeachingTime,
																		GroupLayout.PREFERRED_SIZE,
																		(screenHeight - 20) / 4,
																		GroupLayout.PREFERRED_SIZE)
																.addComponent(
																		panelPlusMinus,
																		GroupLayout.PREFERRED_SIZE,
																		(screenHeight - 20) / 4,
																		GroupLayout.PREFERRED_SIZE)
																.addComponent(
																		panelActDo,
																		GroupLayout.PREFERRED_SIZE,
																		(screenHeight - 20) / 4,
																		GroupLayout.PREFERRED_SIZE))
												.addComponent(
														panelTeacherInfo,
														GroupLayout.PREFERRED_SIZE,
														screenHeight,
														GroupLayout.PREFERRED_SIZE))));
	}

	@Override
	public void update() {
		super.update();
		Teacher oldSelectedTeacher = model.getOldSelectedTeacher();
		if (oldSelectedTeacher != null) {
			ArrayList<String> teacherData = new ArrayList<String>();
			for (int i = 0; i < teacherDataFields.size(); i++) {
				teacherData.add(teacherDataFields.get(i).getText());
			}
			oldSelectedTeacher.setTeacherData(teacherData);

			SchoolType schoolType = model.getSchool().getSchoolTypeByAbbr(
					teacherDataFields.get(5).getText());
			if (schoolType == null) {
				schoolType = new SchoolType(teacherDataFields.get(5).getText(),
						teacherDataFields.get(5).getText(), 0.0);
				oldSelectedTeacher.setSchoolType(schoolType);
			} else {
				oldSelectedTeacher.setSchoolType(schoolType);
			}
		}
		Teacher selectedTeacher = model.getSelectedTeacher();
		if (selectedTeacher != null) {
			for (int i = 0; i < teacherDataFields.size(); i++) {
				teacherDataFields.get(i).setText(
						selectedTeacher.getTeacherData().get(i));
			}

			contentLabels.get(0).setText(
					selectedTeacher.getFirstname() + " "
							+ selectedTeacher.getSurname());
			contentLabels.get(1).setText(selectedTeacher.getGender());

			// birthday
			contentLabels.get(3).setText(selectedTeacher.getBirthday());

			// age
			contentLabels.get(4).setText(
					"Alter: " + selectedTeacher.getAge() + "");
			int schoolAge = selectedTeacher.getSchoolAge();
			contentLabels.get(5).setText("Schuljahr-Alter: " + schoolAge + "");

			// school type
			SchoolType selectedSchoolType = selectedTeacher.getSchoolType();

			double seniorReduction = selectedTeacher.getSeniorReduction();
			double scientificLessons = selectedTeacher
					.getScientificLectureTime();
			double teachingTimePerScLes = selectedTeacher
					.getTeachingTimePerScLes();

			contentLabels.get(2).setText(selectedSchoolType.getName());

			// teaching time depending on school type
			contentLabels.get(6).setText(
					selectedSchoolType.getName() + ": "
							+ selectedSchoolType.getTeachingTime() + " h");
			curSums.set(0, selectedSchoolType.getTeachingTime());

			// scientific lessons
			contentLabels.get(7).setText(
					"Wissenschaftl. Unterricht: " + scientificLessons + " h");
			curSums.set(1, teachingTimePerScLes);

			// partial retirement
			contentLabels.get(8).setText("");

			if (seniorReduction > 0) {
				contentLabels.get(8).setText(
						"Altersteilzeit mit " + schoolAge + ": "
								+ seniorReduction + " h");
			}
			curSums.set(2, teachingTimePerScLes - seniorReduction);

			// full vs. part time
			contentLabels.get(9).setText(selectedTeacher.getPartTimeText());
			curSums.set(3, selectedTeacher.getPartTime(curSums.get(2)));

			// not counted hours
			contentLabels.get(10).setText(selectedTeacher.getMobHoursText());

			// plus minus
			contentLabels.get(11).setText(selectedTeacher.getPlusMinusText());

			// panel actual do vs. to do
			contentLabels.get(20).setText(selectedTeacher.getToDoText(1));
			contentLabels.get(21).setText(selectedTeacher.getActDoText(1));
			contentLabels.get(22).setText(selectedTeacher.getToDoText(2));
			contentLabels.get(23).setText(selectedTeacher.getActDoText(2));

			for (int i = 0; i < 8; i++) {
				String valueString = selectedTeacher.getTeacherData().get(
						14 + i);
				Double value = 0.0;
				if (!valueString.isEmpty() && !valueString.startsWith(" "))
					value = Double.parseDouble(valueString);
				curSums.set(4 + i, curSums.get(3 + i) - value);
				contentLabels.get(12 + i).setText(
						model.getTeacherDataList().get(14 + i) + ": " + value
								+ " h");
			}

			curSumLabels.get(0).setText("Zw.Su.=" + curSums.get(0) + " h");
			for (int i = 1; i < curSums.size(); i++) {
				if (curSums.get(i).equals(curSums.get(i - 1)))
					curSumLabels.get(i).setText("");
				else
					curSumLabels.get(i).setText(
							"Zw.Su.=" + curSums.get(i) + " h");

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
		SchoolType schoolType = teacher.getSchoolType();
		double seniorReduction = teacher.getSeniorReduction();

		String teacherInfoText = teacher.getSurname() + " "
				+ teacher.getFirstname() + "\t (" + teacher.getAbbr() + ") \t "
				+ schoolType.getName() + "\r\n";

		teacherInfoText += teacher.getTeacherSpmText();

		teacherInfoText += schoolType.getName() + ": "
				+ schoolType.getTeachingTime() + " h" + "\r\n";
		teacherInfoText += "Wissenschaftl. Unterricht: "
				+ teacher.getScientificLectureTime() + " h" + "\r\n";

		if (seniorReduction > 0) {
			teacherInfoText += "\r\n Altersteilzeit mit " + teacher.getSchoolAge()
					+ ": " + seniorReduction + " h \r\n";
		}
		teacherInfoText += "\r\n";

		teacherInfoText += teacher.getPartTimeText();
		teacherInfoText += teacher.getMobHoursText();
		teacherInfoText += teacher.getPlusMinusText();

		for (int i = 0; i < 8; i++) {
			String valueString = teacher.getTeacherData().get(14 + i);
			Double value = 0.0;
			if (!valueString.isEmpty() && !valueString.startsWith(" "))
				value = Double.parseDouble(valueString);
			if(value != 0.0)
			teacherInfoText += model.getTeacherDataList().get(14 + i) + ": "
					+ value + " h\r\n";
		}

		teacherInfoText += "\r\n";
		
		teacherInfoText += teacher.getToDoText(1);
		teacherInfoText += teacher.getActDoText(1);
		teacherInfoText += teacher.getToDoText(2);
		teacherInfoText += teacher.getActDoText(2);

		teacherInfoText += "\r\n\r\n";

		return teacherInfoText;
	}

}
