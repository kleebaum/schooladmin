package de.schooladmin.teachingtime;

import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import de.schooladmin.Teacher;
import de.schooladmin.View;

public class ViewTeachingTime extends View implements ViewTeachingTimeInterface {

	public JPanel teacherListPanel;
	public JPanel ioPanel;
	public JPanel overviewPanel;

	public JMenu overviewMenu;

	JList<String> teacherSpmList;
	JList<String> teacherDataList;

	private static final long serialVersionUID = 1L;
	ModelTeachingTimeInterface model;
	ControllerTeachingTimeInterface controller;

	public ViewTeachingTime(ControllerTeachingTimeInterface controller, ModelTeachingTimeInterface model) {
		super(controller, model);
		this.model = model;
		this.controller = controller;
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

	public void createCards() {
		cards.add(viewPanel, "viewPanel");		
		createClassesView();
		cards.add(overviewPanel, "overviewPanel");
	}

	public void createClassesView() {
		overviewPanel = new JPanel();
		initPanelLayout(overviewPanel);

		teacherListPanel = new JPanel();
		teacherListPanel.setLayout(new GridLayout(0, 1));
		teacherListPanel.setBorder(BorderFactory.createTitledBorder("Lehrerliste"));

		final JList<String> teacherList = createTeacherNameList();
		JScrollPane teacherListScrollPane = new JScrollPane(teacherList);
		teacherListPanel.add(teacherListScrollPane);
//		teacherList.addListSelectionListener(new ListSelectionListener() {
//			public void valueChanged(ListSelectionEvent evt) {
//				if (!evt.getValueIsAdjusting()) {
//					String abbr = teacherList.getSelectedValue().split(" ")[0];
//					Teacher selectedTeacher = model.getTeacherByAbbr(abbr);
//					if (selectedTeacher != null) {
//						 controller.setSelectedTeacher(selectedTeacher);
//						 selectedTeacherNameLabel = teacherNameLabel;
//						 selectedTeacherAbbrLabel = teacherAbbrLabel;
//						 selectedTeacherSchoolTypeLabel =
//						 teacherSchoolTypeLabel;
//						 selectedTeacherToDoLabel = teacherToDoLabel;
//						 selectedTeacherActDoLabel = teacherActDoLabel;
//						 updateTeacherInfo();
//					}
//				}
//			}
//		});

		layout.setHorizontalGroup(layout.createParallelGroup(GroupLayout.Alignment.CENTER)

				.addGroup(layout.createSequentialGroup()
						.addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)

								.addComponent(teacherListPanel, GroupLayout.PREFERRED_SIZE, GroupLayout.PREFERRED_SIZE,
										GroupLayout.PREFERRED_SIZE))));

		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.CENTER).addGroup(layout.createSequentialGroup()

						.addComponent(teacherListPanel, screenHeight / 2 + 150, GroupLayout.PREFERRED_SIZE,
								GroupLayout.PREFERRED_SIZE))));

	}
	
	@Override
	public void update() {
		super.update();
		Teacher selectedTeacher = model.getSelectedTeacher();
		if (selectedTeacher != null) {

		}
	}

}
