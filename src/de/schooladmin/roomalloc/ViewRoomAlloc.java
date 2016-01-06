package de.schooladmin.roomalloc;

import java.awt.Color;
import java.awt.Component;
import java.awt.Desktop;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.print.PageFormat;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.swing.BorderFactory;
import javax.swing.ButtonGroup;
import javax.swing.GroupLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

import de.schooladmin.Printer;
import de.schooladmin.Room;
import de.schooladmin.SchoolClass;
import de.schooladmin.Teacher;
import de.schooladmin.View;
import de.schooladmin.Room.RoomArea;

/**
 * @author Anja Kleebaum
 *
 */
public class ViewRoomAlloc extends View implements ViewRoomAllocInterface {

	private static final long serialVersionUID = -5648786394907750019L;

	ModelRoomAllocInterface model;
	ControllerRoomAllocInterface controller;

	public ViewRoomAlloc(ControllerRoomAllocInterface controller, ModelRoomAllocInterface model) {
		super(controller, model);
		this.model = model;
		this.controller = controller;
	}

	JPanel upzPanel;
	JPanel roomPanel;
	JPanel listPanel;
	JPanel showTextPanel;

	JList<String> teacherSpmList;
	JList<String> teacherDataList;

	static JPopupMenu popupMenu;
	JMenu menuUPZ;
	JMenu menuRoom;
	JMenu menuLists;

	JLabel roomLabel;

	Teacher selectedRoomTeacher;

	ArrayList<JTextArea> panelArrayList;
	public static ArrayList<JButton> rooms = new ArrayList<JButton>();

	public void createCards() {
		createRoomView();
		createListView();
		cards.add(viewPanel, "viewPanel");
		cards.add(roomPanel, "roomPanel");
		cards.add(listPanel, "listPanel");
		scrollPane.addMouseListener(new PopClickListener());
	}

	public JMenuBar createMenuBar() {
		menuBar = new JMenuBar();
		menuRoom = new JMenu("Raumplan");
		menuLists = new JMenu("Stundenpl\u00E4ne");
		menuUPZ = new JMenu("LehrerUPZ");

		menuBar.add(menuRoom);
		menuRoom.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent arg0) {
			}

			@Override
			public void menuDeselected(MenuEvent arg0) {
			}

			@Override
			public void menuSelected(MenuEvent arg0) {
				cardLayout.show(cards, "roomPanel");
				// scrollPane.setSize(viewFrame.getPreferredSize());
			}
		});

		menuBar.add(menuLists);
		menuLists.addMenuListener(new MenuListener() {

			@Override
			public void menuCanceled(MenuEvent arg0) {
			}

			@Override
			public void menuDeselected(MenuEvent arg0) {
			}

			@Override
			public void menuSelected(MenuEvent arg0) {
				cardLayout.show(cards, "listPanel");
				// scrollPane.setSize(viewFrame.getPreferredSize());
			}
		});

		return menuBar;
	}

	@Override
	public void createUpzView() {
		upzPanel = new JPanel();
		initPanelLayout(upzPanel);

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

		JPanel panelTeacherData = new JPanel(new GridLayout(1, 0));
		panelTeacherData.setBorder(BorderFactory.createTitledBorder("Daten"));
		teacherDataList = new JList<String>();
		teacherDataList.setFont(monospacedFont);
		JScrollPane dataListScrollPane = new JScrollPane(teacherDataList);
		panelTeacherData.add(dataListScrollPane);

		JPanel panelTeacherSpm = new JPanel(new GridLayout(1, 0));
		panelTeacherSpm.setBorder(BorderFactory.createTitledBorder("SPM"));
		teacherSpmList = new JList<String>();
		teacherSpmList.setFont(monospacedFont);
		JScrollPane spmListScrollPane = new JScrollPane(teacherSpmList);
		panelTeacherSpm.add(spmListScrollPane);

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, panelTeacher.getPreferredSize().width,
						GroupLayout.PREFERRED_SIZE)
				.addComponent(panelTeacherData, GroupLayout.PREFERRED_SIZE, panelTeacherData.getPreferredSize().width,
						GroupLayout.PREFERRED_SIZE).addComponent(panelTeacherSpm));

		layout.setVerticalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
						.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, screenHeight,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(panelTeacherData, GroupLayout.PREFERRED_SIZE, screenHeight,
								GroupLayout.PREFERRED_SIZE)
						.addComponent(panelTeacherSpm, GroupLayout.PREFERRED_SIZE, screenHeight,
								GroupLayout.PREFERRED_SIZE)));
	}

	@Override
	public void createRoomView() {
		roomPanel = new JPanel();
		initPanelLayout(roomPanel);

		// Label
		roomLabel = new JLabel("");
		JPanel panelRoomLabel = new JPanel();
		panelRoomLabel.setLayout(new GridLayout(1, 0));
		panelRoomLabel.add(roomLabel);

		// Days of the week
		JPanel panelDayOfWeek = new JPanel();
		panelDayOfWeek.setLayout(new GridLayout(5, 0));
		panelDayOfWeek.setBorder(BorderFactory.createTitledBorder("Tag"));
		final ButtonGroup daysOfWeek = new ButtonGroup();
		String[] dayOfWeekNames = { "Mo", "Di", "Mi", "Do", "Fr" };
		JRadioButton[] dayOfWeek = new JRadioButton[5];
		for (int i = 0; i < dayOfWeek.length; i++) {
			dayOfWeek[i] = new JRadioButton(dayOfWeekNames[i]);
			dayOfWeek[i].setActionCommand("" + (i + 1));
			daysOfWeek.add(dayOfWeek[i]);
			panelDayOfWeek.add(dayOfWeek[i]);
		}
		dayOfWeek[model.getSelectedDay() - 1].setSelected(true);

		// Class hours
		JPanel panelHour = new JPanel();
		panelHour.setLayout(new GridLayout(11, 0));
		panelHour.setBorder(BorderFactory.createTitledBorder("Stunde"));
		final ButtonGroup hours = new ButtonGroup();
		JRadioButton[] hour = new JRadioButton[11];
		for (int i = 0; i < hour.length; i++) {
			hour[i] = new JRadioButton("" + (i + 1));
			hour[i].setActionCommand("" + (i + 1));
			hours.add(hour[i]);
			panelHour.add(hour[i]);
		}
		hour[model.getSelectedHour() - 1].setSelected(true);

		ActionListener radioButtonActionListener = new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() instanceof JRadioButton) {
					JRadioButton radioButton = (JRadioButton) e.getSource();
					if (radioButton.isSelected()) {
						int selectedHour = Integer.parseInt(hours.getSelection().getActionCommand());
						int selectedDay = Integer.parseInt(daysOfWeek.getSelection().getActionCommand());
						controller.setSelectedTime(selectedHour, selectedDay);
					}
				}
			}
		};
		for (int i = 0; i < dayOfWeek.length; i++) {
			dayOfWeek[i].addActionListener(radioButtonActionListener);
		}
		for (int i = 0; i < hour.length; i++) {
			hour[i].addActionListener(radioButtonActionListener);
		}

		// Class selection
		JPanel panelClass = new JPanel(new GridLayout(1, 0));
		panelClass.setBorder(BorderFactory.createTitledBorder("Klassen"));
		final JList<String> classList = new JList<String>();
		classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		JScrollPane scrollPaneClassList = new JScrollPane(classList);
		panelClass.add(scrollPaneClassList);
		ArrayList<String> classResultArrayList = new ArrayList<String>();
		for (SchoolClass schoolClass : model.getClasses()) {
			classResultArrayList.add(schoolClass.getName());
		}
		String[] classResultArray = classResultArrayList.toArray(new String[classResultArrayList.size()]);
		classList.setListData(classResultArray);

		classList.addMouseListener(new PopClickListener());
		classList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (!evt.getValueIsAdjusting()) {
					for (SchoolClass schoolClass : model.getClasses()) {
						if (schoolClass.getName().equals(classList.getSelectedValue())) {
							controller.setSelectedClass(schoolClass);
						}
					}
				}
			}
		});

		// Teacher selection
		JPanel panelTeacher = new JPanel(new GridLayout(1, 0));
		panelTeacher.setBorder(BorderFactory.createTitledBorder("Lehrer"));
		final JList<String> teacherList = createTeacherNameList();
		JScrollPane scrollPaneTeacherList = new JScrollPane(teacherList);
		panelTeacher.add(scrollPaneTeacherList);
		teacherList.addMouseListener(new PopClickListener());

		// Bereichsdefinition
		JPanel panelAltbauUnten = new JPanel();
		JPanel panelAltbauOben = new JPanel();
		JPanel panelNeubauUnten = new JPanel();
		JPanel panelNeubauOben = new JPanel();
		JPanel panelKeller = new JPanel();
		JPanel panelHallen = new JPanel();

		fillRoomArea(panelAltbauUnten, "Altbau Untergeschoss", RoomArea.ALTBAU_UNTEN,
				ModelRoomAllocInterface.AltbauWidth, ModelRoomAllocInterface.AltbauLength);
		fillRoomArea(panelAltbauOben, "Altbau Obergeschoss", RoomArea.ALTBAU_OBEN, ModelRoomAllocInterface.AltbauWidth,
				ModelRoomAllocInterface.AltbauLength);
		fillRoomArea(panelNeubauUnten, "Neubau Untergeschoss", RoomArea.NEUBAU_UNTEN,
				ModelRoomAllocInterface.NeubauWidth, ModelRoomAllocInterface.NeubauLength);
		fillRoomArea(panelNeubauOben, "Neubau Obergeschoss", RoomArea.NEUBAU_OBEN, ModelRoomAllocInterface.NeubauWidth,
				ModelRoomAllocInterface.NeubauLength);
		fillRoomArea(panelKeller, "Keller", RoomArea.KELLER, ModelRoomAllocInterface.AltbauWidth,
				ModelRoomAllocInterface.AltbauLength);
		fillRoomArea(panelHallen, "Hallen", RoomArea.HALLEN, ModelRoomAllocInterface.AltbauWidth,
				ModelRoomAllocInterface.AltbauLength);

		layout.setHorizontalGroup(layout
				.createParallelGroup()
				.addComponent(panelRoomLabel)
				.addGroup(
						layout.createSequentialGroup()
								.addGroup(
										layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(panelDayOfWeek, GroupLayout.PREFERRED_SIZE,
														panelDayOfWeek.getPreferredSize().width + 20,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(panelHour, GroupLayout.PREFERRED_SIZE,
														panelDayOfWeek.getPreferredSize().width + 20,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(panelClass, GroupLayout.PREFERRED_SIZE,
														panelDayOfWeek.getPreferredSize().width + 20,
														GroupLayout.PREFERRED_SIZE))
								.addGroup(
										layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(panelNeubauUnten, GroupLayout.PREFERRED_SIZE, panelWidth,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(panelAltbauUnten, GroupLayout.PREFERRED_SIZE, panelWidth,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(panelHallen, GroupLayout.PREFERRED_SIZE, panelWidth,
														GroupLayout.PREFERRED_SIZE))
								.addGroup(
										layout.createParallelGroup(GroupLayout.Alignment.LEADING)
												.addComponent(panelNeubauOben, GroupLayout.PREFERRED_SIZE, panelWidth,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(panelAltbauOben, GroupLayout.PREFERRED_SIZE, panelWidth,
														GroupLayout.PREFERRED_SIZE)
												.addComponent(panelKeller, GroupLayout.PREFERRED_SIZE, panelWidth,
														GroupLayout.PREFERRED_SIZE))
								.addGroup(
										layout.createParallelGroup(GroupLayout.Alignment.TRAILING).addComponent(
												panelTeacher))));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addComponent(panelRoomLabel, GroupLayout.PREFERRED_SIZE, panelLength / 8, GroupLayout.PREFERRED_SIZE)
				.addGroup(
						layout.createBaselineGroup(false, true)
								.addGroup(
										layout.createSequentialGroup()
												.addGroup(
														layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
																.addComponent(panelDayOfWeek,
																		GroupLayout.PREFERRED_SIZE, panelLength,
																		GroupLayout.PREFERRED_SIZE)
																.addComponent(panelNeubauUnten,
																		GroupLayout.PREFERRED_SIZE, panelLength,
																		GroupLayout.PREFERRED_SIZE)
																.addComponent(panelNeubauOben,
																		GroupLayout.PREFERRED_SIZE, panelLength,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createParallelGroup(GroupLayout.Alignment.LEADING)
																.addComponent(panelHour, GroupLayout.PREFERRED_SIZE,
																		panelLength, GroupLayout.PREFERRED_SIZE)
																.addComponent(panelAltbauUnten,
																		GroupLayout.PREFERRED_SIZE, panelLength,
																		GroupLayout.PREFERRED_SIZE)
																.addComponent(panelAltbauOben,
																		GroupLayout.PREFERRED_SIZE, panelLength,
																		GroupLayout.PREFERRED_SIZE))
												.addGroup(
														layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
																.addComponent(panelClass, GroupLayout.PREFERRED_SIZE,
																		panelLength, GroupLayout.PREFERRED_SIZE)
																.addComponent(panelHallen, GroupLayout.PREFERRED_SIZE,
																		panelLength, GroupLayout.PREFERRED_SIZE)
																.addComponent(panelKeller, GroupLayout.PREFERRED_SIZE,
																		panelLength, GroupLayout.PREFERRED_SIZE)))
								.addComponent(panelTeacher)));
	}

	@Override
	public void createListView() {
		listPanel = new JPanel();
		initPanelLayout(listPanel);

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
							showText(
									"Lehrerstundenplan f\u00fcr " + teacher.getSurname() + ", "
											+ teacher.getFirstname(), teacher.getTimeTableText(),
									"F\u00fcr " + teacher.getSurname() + ", " + teacher.getFirstname()
											+ " ist leider kein Stundenplan vorhanden.");
						}
					}
				}
			}
		});
		teacherList.addMouseListener(new PopClickListener());

		// Class list
		JPanel panelClass = new JPanel(new GridLayout(1, 0));
		panelClass.setBorder(BorderFactory.createTitledBorder("Klassen"));
		final JList<String> classList = new JList<String>();
		classList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		ArrayList<String> classResultArrayList = new ArrayList<String>();
		for (SchoolClass schoolClass : model.getClasses()) {
			classResultArrayList.add(schoolClass.getName());
		}
		String[] classResultArray = classResultArrayList.toArray(new String[classResultArrayList.size()]);
		classList.setListData(classResultArray);

		JScrollPane scrollPaneClassList = new JScrollPane(classList);
		panelClass.add(scrollPaneClassList);

		classList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (!evt.getValueIsAdjusting()) {
					for (SchoolClass schoolClass : model.getClasses()) {
						if (schoolClass.getName().equals(classList.getSelectedValue())) {
							controller.setSelectedClass(schoolClass);
							showText("Stundenplan f\u00fcr Klasse " + schoolClass.getName(),
									schoolClass.getTimeTableText(),
									"F\u00fcr diese Klasse wurde leider kein Stundenplan gefunden.");
						}
					}
				}
			}
		});
		classList.addMouseListener(new PopClickListener());

		// Room list
		JPanel panelRoom = new JPanel(new GridLayout(1, 0));
		panelRoom.setBorder(BorderFactory.createTitledBorder("R\u00E4ume"));
		final JList<String> roomList = new JList<String>();
		roomList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		ArrayList<String> roomResultArrayList = new ArrayList<String>();
		for (Room room : model.getRooms()) {
			if (!room.getRoomAllocationText().equals("")) {
				roomResultArrayList.add(room.getName());
			}
		}
		String[] roomResultArray = roomResultArrayList.toArray(new String[roomResultArrayList.size()]);
		roomList.setListData(roomResultArray);

		JScrollPane scrollPaneRoomList = new JScrollPane(roomList);
		panelRoom.add(scrollPaneRoomList);

		roomList.addListSelectionListener(new ListSelectionListener() {
			public void valueChanged(ListSelectionEvent evt) {
				if (!evt.getValueIsAdjusting()) {
					for (Room room : model.getRooms()) {
						if (room.getName().equals(roomList.getSelectedValue())) {
							controller.setSelectedRoom(room);
							showText("Stundenplan f\u00fcr Raum " + room.getName(), room.getRoomAllocationText(),
									"F\u00fcr diesen Raum wurde leider kein Stundenplan gefunden.");
						}
					}
				}
			}
		});
		roomList.addMouseListener(new PopClickListener());

		layout.setHorizontalGroup(layout
				.createSequentialGroup()
				.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, panelTeacher.getPreferredSize().width,
						GroupLayout.PREFERRED_SIZE)
				.addContainerGap()
				.addComponent(panelClass, GroupLayout.PREFERRED_SIZE, panelClass.getPreferredSize().width + 20,
						GroupLayout.PREFERRED_SIZE)
				.addContainerGap()
				.addComponent(panelRoom, GroupLayout.PREFERRED_SIZE, panelRoom.getPreferredSize().width + 20,
						GroupLayout.PREFERRED_SIZE));

		layout.setVerticalGroup(layout.createParallelGroup()
				.addComponent(panelTeacher, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE)
				.addComponent(panelClass, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE)
				.addComponent(panelRoom, GroupLayout.PREFERRED_SIZE, screenHeight, GroupLayout.PREFERRED_SIZE));
	}

	@Override
	public void fillRoomArea(JPanel panel, String roomAreaTitle, RoomArea roomArea, int areaWidth, int areaLength) {
		panel.setLayout(null);
		panel.setBorder(BorderFactory.createTitledBorder(roomAreaTitle));

		for (final Room room : model.getRooms()) {
			if (room.getRoomArea().equals(roomArea)) {
				int xKoordRel = room.getX() * panelWidth / areaWidth;
				int yKoordRel = room.getY() * panelLength / areaLength;
				int widthRel = room.getxLength() * panelWidth / areaWidth;
				int lengthRel = room.getyLength() * panelLength / areaLength;

				JButton roomButton = new JButton();
				roomButton.setHorizontalAlignment(SwingConstants.LEFT);
				roomButton.setFont(font);

				roomButton.setMargin(margin);
				roomButton.setBounds(xKoordRel, panelLength - (yKoordRel + lengthRel), widthRel, lengthRel);

				final String roomName = room.getName();
				roomButton.setActionCommand(roomName);
				int space = room.getSpace();
				if (space == 0)
					roomButton.setText("<html>" + roomName + "<br>" + room.getAddOn() + "</html>");
				else
					roomButton.setText("<html>" + roomName + " (" + space + ")" + "<br>" + room.getAddOn() + "</html>");

				roomButton.addMouseListener(new MouseListener() {
					@Override
					public void mouseClicked(MouseEvent arg0) {
						controller.setSelectedRoom(room);
					}

					public void mouseEntered(MouseEvent arg0) {
					}

					public void mouseExited(MouseEvent arg0) {
					}

					public void mousePressed(MouseEvent arg0) {
						controller.setSelectedRoom(room);
					}

					public void mouseReleased(MouseEvent arg0) {

					}
				});
				roomButton.addMouseListener(new PopClickListener());
				roomButton.setBackground(Color.lightGray);
				panel.add(roomButton);
				rooms.add(roomButton);
			}
		}
	}

	@Override
	public void showText(String title, String text, String emptyText) {
		showTextPanel = new JPanel();
		initPanelLayout(showTextPanel);
		cards.add(showTextPanel, "showTextPanel");
		cardLayout.show(cards, "showTextPanel");

		JLabel label = new JLabel();
		label.setText(title);
		JTextArea textArea = new JTextArea();
		textArea.addMouseListener(new PopClickListener());
		textArea.setEditable(false);
		textArea.setFont(monospacedFont);
		if (text.equals(""))
			textArea.setText(emptyText);
		else
			textArea.setText(title + "\r\n" + text);
		JScrollPane textAreaScrollPane = new JScrollPane(textArea);

		layout.setHorizontalGroup(layout.createSequentialGroup().addGroup(
				layout.createParallelGroup(GroupLayout.Alignment.LEADING)
						.addComponent(label)
						.addComponent(textAreaScrollPane, GroupLayout.PREFERRED_SIZE,
								textAreaScrollPane.getPreferredSize().width + 20, GroupLayout.PREFERRED_SIZE)));

		layout.setVerticalGroup(layout
				.createSequentialGroup()
				.addComponent(label)
				.addComponent(textAreaScrollPane, GroupLayout.PREFERRED_SIZE, screenHeight - 10,
						GroupLayout.PREFERRED_SIZE));
	}

	@Override
	public void update() {
		updateAllRoomAllocation();
		// updateUpz();
	}

	@Override
	public void updateUpz() {
		Teacher selectedTeacher = model.getSelectedTeacher();
		if (selectedTeacher != null) {
			teacherSpmList.setListData(selectedTeacher.getTeacherSpmText());
			teacherDataList.setListData(selectedTeacher.getTeacherData());
		}
	}

	@Override
	public void updateAllRoomAllocation() {
		Room selectedRoom = model.getSelectedRoom();
		Teacher selectedTeacher = model.getSelectedTeacher();
		SchoolClass selectedClass = model.getSelectedClass();

		String labelText = model.getSelectedDayName() + " " + model.getSelectedHour() + ". Stunde";
		if (selectedTeacher != null) {
			String firstnameAbbr = selectedTeacher.getFirstname().substring(0, 1) + ".";
			labelText += "    |    " + selectedTeacher.getSurname() + ", " + firstnameAbbr;
		}

		String classLabelText = "";
		for (JButton roomButton : ViewRoomAlloc.rooms) {
			Teacher roomAllocationTeacher = model.getRoomAllocationTeacher(roomButton.getActionCommand());
			String roomAllocationTeacherAbbr = model.getRoomAllocationTeacherAbbr(roomButton.getActionCommand());

			if (roomAllocationTeacherAbbr == null) {
				roomButton.setBackground(Color.lightGray);
			} else if (roomAllocationTeacherAbbr.equals("")) {
				roomButton.setBackground(Color.green);
			} else if (selectedTeacher != null
					&& roomAllocationTeacherAbbr.equals(model.getSelectedTeacher().getAbbr())) {
				roomButton.setBackground(Color.yellow);
				labelText += ": Raum " + roomButton.getActionCommand();
			} else {
				roomButton.setBackground(Color.red);
			}

			if (selectedClass != null) {
				SchoolClass roomAllocationClass = model.getRoomAllocationClass(roomButton.getActionCommand());
				if (roomAllocationClass != null && roomAllocationClass.getName().equals(selectedClass.getName())) {
					roomButton.setBackground(Color.magenta);
					String firstnameAbbr = roomAllocationTeacher.getFirstname().substring(0, 1) + ".";
					classLabelText += ": Raum " + roomButton.getActionCommand() + ": "
							+ roomAllocationTeacher.getSurname() + ", " + firstnameAbbr;
				}
			}
		}
		if (selectedClass != null) {
			labelText += "    |    Klasse " + selectedClass.getName() + classLabelText;
		}

		if (selectedRoom != null) {
			Teacher selectedRoomTeacher = model.getRoomAllocationTeacher(selectedRoom.getName());
			if (selectedRoomTeacher != null) {
				String firstnameAbbr = selectedRoomTeacher.getFirstname().substring(0, 1) + ".";
				labelText += "    |    Raum " + selectedRoom.getName() + ": " + selectedRoomTeacher.getSurname() + ", "
						+ firstnameAbbr;
			}
		}

		roomLabel.setText(labelText);
	}

	public class PopClickListener extends MouseAdapter {

		public void mousePressed(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		public void mouseReleased(MouseEvent e) {
			if (e.isPopupTrigger())
				doPop(e);
		}

		private void doPop(final MouseEvent e) {
			popupMenu = new JPopupMenu();
			final Room selectedRoom = model.getSelectedRoom();
			final Teacher selectedTeacher = model.getSelectedTeacher();
			final SchoolClass selectedClass = model.getSelectedClass();

			if (selectedRoom != null) {
				JMenuItem selectedRoomItem = new JMenuItem("Raumplan f\u00fcr Raum " + selectedRoom.getName());
				popupMenu.add(selectedRoomItem);
				selectedRoomItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						showText("Belegungsplan f\u00fcr Raum " + selectedRoom.getName(),
								selectedRoom.getRoomAllocationText(),
								"F\u00fcr diesen Raum wurde leider kein Eintrag gefunden.");
					}
				});

				selectedRoomTeacher = model.getRoomAllocationTeacher(selectedRoom.getName());
				if (selectedRoomTeacher != null) {
					final String firstnameAbbr = selectedRoomTeacher.getFirstname().substring(0, 1) + ".";
					JMenuItem selectedRoomTeacherItem = new JMenuItem("Lehrerstundenplan f\u00fcr "
							+ selectedRoomTeacher.getSurname() + ", " + firstnameAbbr + " (in Raum "
							+ selectedRoom.getName() + ")");
					popupMenu.add(selectedRoomTeacherItem);
					selectedRoomTeacherItem.addActionListener(new ActionListener() {

						@Override
						public void actionPerformed(ActionEvent arg0) {
							showText("Lehrerstundenplan f\u00fcr " + selectedRoomTeacher.getSurname() + ", "
									+ firstnameAbbr, selectedRoomTeacher.getTimeTableText(), "F\u00fcr "
									+ selectedRoomTeacher.getSurname() + ", " + firstnameAbbr
									+ " ist leider kein Stundenplan vorhanden.");

						}

					});
				}
			}

			if (selectedTeacher != null && !selectedTeacher.equals(selectedRoomTeacher)) {
				final String firstnameAbbr = selectedTeacher.getFirstname().substring(0, 1) + ".";
				JMenuItem selectedTeacherButton = new JMenuItem("Lehrerstundenplan f\u00fcr "
						+ selectedTeacher.getSurname() + ", " + firstnameAbbr);
				popupMenu.add(selectedTeacherButton);
				selectedTeacherButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						showText("Lehrerstundenplan f\u00fcr " + selectedTeacher.getSurname() + ", " + firstnameAbbr,
								selectedTeacher.getTimeTableText(), "F\u00fcr " + selectedTeacher.getSurname() + ", "
										+ firstnameAbbr + " ist leider kein Stundenplan vorhanden.");

					}
				});
			}

			if (selectedClass != null) {
				JMenuItem selectedClassItem = new JMenuItem("Stundenplan f\u00fcr Klasse " + selectedClass.getName());
				popupMenu.add(selectedClassItem);
				selectedClassItem.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent arg0) {
						showText("Stundenplan f\u00fcr Klasse " + selectedClass.getName(),
								selectedClass.getTimeTableText(),
								"F\u00fcr diese Klasse wurde leider kein Stundenplan gefunden.");
					}
				});

			}

			JMenuItem showRoomAllocationItem = new JMenuItem("\u00dcbersicht Raumbelegung "
					+ model.getSelectedDayName() + ", Stunde " + model.getSelectedHour());
			popupMenu.add(showRoomAllocationItem);
			showRoomAllocationItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					showText(
							"\u00dcbersicht Raumbelegung " + model.getSelectedDayName() + ", Stunde "
									+ model.getSelectedHour(), showRoomAllocation(),
							"Die \u00dcbersicht zur Raumbelegung konnte leider nicht erstellt werden.");
				}
			});
			JMenuItem printItem = new JMenuItem("Ansicht drucken");
			popupMenu.add(printItem);
			printItem.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent arg0) {
					print(cards);
				}
			});

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
							w = new PrintWriter(new BufferedWriter(
									new FileWriter(datei)));

							w.print(textArea.getText());
							w.flush();
							w.close();
							
							int printFontSize = Math.round(5800/textArea.getWidth());
							Font printFont = new Font("monospaced", Font.PLAIN, printFontSize);
							textArea.setFont(printFont);							
							textArea.print();							
							textArea.setFont(monospacedFont);
						
							datei.deleteOnExit();

						} catch (IOException | PrinterException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				});
			}
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}

	@Override
	public void print(Component toPrint) {
		PrinterJob pjob = PrinterJob.getPrinterJob();
		// Set print component
		PageFormat pf = pjob.defaultPage();
		pf.setOrientation(PageFormat.LANDSCAPE);
		pjob.setPrintable(new Printer(toPrint), pf);
		pjob.setJobName("GSH_" + model.getSelectedDayName() + "_Stunde_" + model.getSelectedHour());
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
	public String showRoomAllocation() {
		int selectedDay = model.getSelectedDay();
		int selectedHour = model.getSelectedHour();
		String separation = "+========+=========+========+==========================+ \r\n";
		String roomAllocation = separation + "| Raum   | Klasse  | Fach   | Lehrer                   | \r\n"
				+ separation;
		for (Room room : model.getRooms()) {
			if (!room.getRoomAllocationText().equals("")) {
				String roomName = room.getName();
				String schoolClass = room.getSchoolClass(selectedDay, selectedHour);
				String subject = room.getSubject(selectedDay, selectedHour);
				String teacherName = "";
				Teacher roomAllocationTeacher = model.getTeacherByAbbr(room.getTeacher(selectedDay, selectedHour));
				if (roomAllocationTeacher != null) {
					teacherName += roomAllocationTeacher.getSurname() + ", " + roomAllocationTeacher.getFirstname();
				}
				roomAllocation += "| " + roomName + whitespaceCalc(7, roomName.length()) + "| " + schoolClass
						+ whitespaceCalc(8, schoolClass.length()) + "| " + subject
						+ whitespaceCalc(7, subject.length()) + "| " + teacherName
						+ whitespaceCalc(25, teacherName.length()) + "| \r\n"
						+ "+--------+---------+--------+--------------------------+\r\n";
			}

		}
		return roomAllocation;
	}

	@Override
	public String whitespaceCalc(int totalLength, int letterLength) {
		String whiteSpaces = "";
		for (int i = 0; i < totalLength - letterLength; i++) {
			whiteSpaces += " ";
		}
		return whiteSpaces;
	}

}
