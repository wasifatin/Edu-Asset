package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.FormDialog;
import com.examsphere.components.RoundedPanel;
import com.examsphere.model.Exam;
import com.examsphere.model.ExamRoom;
import com.examsphere.model.SeatAssignment;
import com.examsphere.model.Student;
import com.examsphere.service.ExamService;
import com.examsphere.service.SeatAllocationService;
import com.examsphere.service.StudentService;
import com.examsphere.util.Constants;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Room and seating administration panel providing venue capacity configuration,
 * automated seat number generation, and exam room assignment visualization.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class RoomPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final SeatAllocationService seatService = SeatAllocationService.getInstance();
    private final ExamService examService = ExamService.getInstance();
    private final StudentService studentService = StudentService.getInstance();

    private DataTablePanel roomTablePanel;
    private DataTablePanel seatingTablePanel;
    private JComboBox<String> examSelectorCombo;

    public RoomPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setLayout(new BorderLayout(Constants.SPACING, Constants.SPACING));
        setBorder(new EmptyBorder(Constants.SPACING_LG, Constants.SPACING_LG, Constants.SPACING_LG, Constants.SPACING_LG));
        setBackground(theme.getBackground());

        buildUI();
        theme.addThemeChangeListener(this::refreshTheme);
    }

    private void buildUI() {
        removeAll();
        setBackground(theme.getBackground());

        // Header
        RoundedPanel headerCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        headerCard.setLayout(new BorderLayout(12, 12));
        headerCard.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Examination Rooms & Seating Arrangement Plan");
        title.setFont(theme.getBoldFont(18));
        title.setForeground(theme.getPrimaryText());
        headerCard.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        CustomButton addRoomBtn = new CustomButton("+ Add Room", theme.getPrimaryAccent(), Color.WHITE);
        addRoomBtn.addActionListener(e -> openAddRoomDialog());
        actions.add(addRoomBtn);

        CustomButton autoAllocateBtn = new CustomButton("Auto-Allocate Seating", theme.getSuccess(), Color.WHITE);
        autoAllocateBtn.addActionListener(e -> openAutoAllocateDialog());
        actions.add(autoAllocateBtn);

        headerCard.add(actions, BorderLayout.EAST);
        add(headerCard, BorderLayout.NORTH);

        // Split view: Left = Rooms List, Right = Seating Plan for Selected Exam
        JPanel split = new JPanel(new GridLayout(1, 2, Constants.SPACING, 0));
        split.setOpaque(false);

        // Left Panel: Rooms
        RoundedPanel roomsCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        roomsCard.setLayout(new BorderLayout(8, 8));
        roomsCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));

        JLabel roomsTitle = new JLabel("Available Examination Venues");
        roomsTitle.setFont(theme.getBoldFont(15));
        roomsTitle.setForeground(theme.getPrimaryText());
        roomsCard.add(roomsTitle, BorderLayout.NORTH);

        String[] roomCols = {"Room ID", "Building", "Capacity", "Status", "Facilities"};
        roomTablePanel = new DataTablePanel(roomCols, getRoomData());
        roomsCard.add(roomTablePanel, BorderLayout.CENTER);

        // Right Panel: Seating Arrangements
        RoundedPanel seatingCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        seatingCard.setLayout(new BorderLayout(8, 8));
        seatingCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));

        JPanel seatingTop = new JPanel(new BorderLayout(8, 0));
        seatingTop.setOpaque(false);
        JLabel seatingTitle = new JLabel("Seating Plan by Exam");
        seatingTitle.setFont(theme.getBoldFont(15));
        seatingTitle.setForeground(theme.getPrimaryText());
        seatingTop.add(seatingTitle, BorderLayout.WEST);

        List<Exam> exams = examService.getAllExams();
        String[] examOpts = exams.stream().map(Exam::getExamId).toArray(String[]::new);
        if (examOpts.length == 0) examOpts = new String[]{"No Exams"};
        examSelectorCombo = new JComboBox<>(examOpts);
        examSelectorCombo.setFont(theme.getFont(13));
        examSelectorCombo.addActionListener(e -> refreshSeatingData());
        seatingTop.add(examSelectorCombo, BorderLayout.EAST);

        seatingCard.add(seatingTop, BorderLayout.NORTH);

        String[] seatCols = {"Seat #", "Student ID", "Student Name", "Room", "Exam ID"};
        seatingTablePanel = new DataTablePanel(seatCols, getSeatingData());
        seatingCard.add(seatingTablePanel, BorderLayout.CENTER);

        split.add(roomsCard);
        split.add(seatingCard);

        add(split, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private Object[][] getRoomData() {
        List<ExamRoom> rooms = seatService.getAllRooms();
        Object[][] data = new Object[rooms.size()][5];
        for (int i = 0; i < rooms.size(); i++) {
            ExamRoom r = rooms.get(i);
            data[i][0] = r.getRoomId();
            data[i][1] = r.getBuilding();
            data[i][2] = r.getCapacity() + " Seats";
            data[i][3] = r.isAvailable() ? "ACTIVE" : "MAINTENANCE";
            data[i][4] = String.join(", ", r.getFacilities());
        }
        return data;
    }

    private Object[][] getSeatingData() {
        String examId = (String) examSelectorCombo.getSelectedItem();
        if (examId == null || examId.equals("No Exams")) {
            return new Object[0][5];
        }

        List<SeatAssignment> list = seatService.getSeatsByExam(examId);
        Object[][] data = new Object[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            SeatAssignment sa = list.get(i);
            Student stu = studentService.getStudentById(sa.getStudentId());
            data[i][0] = "Seat " + sa.getSeatNumber();
            data[i][1] = sa.getStudentId();
            data[i][2] = stu != null ? stu.getFullName() : "N/A";
            data[i][3] = sa.getRoomId();
            data[i][4] = sa.getExamId();
        }
        return data;
    }

    private void refreshSeatingData() {
        seatingTablePanel.setData(getSeatingData());
    }

    private void openAddRoomDialog() {
        FormDialog dialog = new FormDialog(mainFrame, "Add Examination Hall / Room", 440, 360);

        JTextField roomIdField = new JTextField();
        JTextField buildingField = new JTextField("KT Building");
        JComboBox<Integer> capacityCombo = new JComboBox<>(new Integer[]{30, 35, 40, 50, 60, 80, 100});
        capacityCombo.setSelectedItem(40);
        JCheckBox acCheck = new JCheckBox("Air Conditioned", true);
        JCheckBox cctvCheck = new JCheckBox("CCTV Surveillance", true);

        dialog.addFormField("Room ID (e.g. KT-320) *:", roomIdField);
        dialog.addFormField("Campus Building *:", buildingField);
        dialog.addFormField("Seating Capacity *:", capacityCombo);
        dialog.addFormField("Climate:", acCheck);
        dialog.addFormField("Security:", cctvCheck);

        CustomButton saveBtn = new CustomButton("Save Room", theme.getPrimaryAccent(), Color.WHITE);
        saveBtn.addActionListener(e -> {
            String rid = roomIdField.getText().trim();
            String bld = buildingField.getText().trim();
            int cap = (Integer) capacityCombo.getSelectedItem();

            if (rid.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Room ID cannot be blank.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ExamRoom room = new ExamRoom(rid, bld, cap);
            if (acCheck.isSelected()) room.addFacility("Air Conditioned");
            if (cctvCheck.isSelected()) room.addFacility("CCTV Surveillance");

            seatService.addRoom(room);
            dialog.dispose();
            roomTablePanel.setData(getRoomData());
            JOptionPane.showMessageDialog(mainFrame, "Examination venue added successfully.", "Room Created", JOptionPane.INFORMATION_MESSAGE);
        });

        CustomButton cancelAction = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
        cancelAction.addActionListener(e -> dialog.dispose());

        dialog.getButtonPanel().add(cancelAction);
        dialog.getButtonPanel().add(saveBtn);
        dialog.setVisible(true);
    }

    private void openAutoAllocateDialog() {
        String examId = (String) examSelectorCombo.getSelectedItem();
        if (examId == null || examId.equals("No Exams")) {
            JOptionPane.showMessageDialog(this, "Please select an examination first.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        Exam exam = examService.getExamById(examId);
        if (exam == null || exam.getRoomId() == null) {
            JOptionPane.showMessageDialog(this, "The selected exam does not have an assigned room.", "Notice", JOptionPane.WARNING_MESSAGE);
            return;
        }

        ExamRoom room = seatService.getRoomById(exam.getRoomId());
        int cap = room != null ? room.getCapacity() : 40;

        List<Student> allStudents = studentService.getAllStudents();
        List<String> studentIds = allStudents.stream().map(Student::getStudentId).collect(Collectors.toList());

        int toAssign = Math.min(cap, studentIds.size());
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Allocate " + toAssign + " students to Room " + exam.getRoomId() + " (Capacity: " + cap + ")?",
                "Confirm Auto-Allocation", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            seatService.allocateSeats(examId, exam.getRoomId(), studentIds);
            refreshSeatingData();
            int remaining = seatService.getAvailableSeats(exam.getRoomId(), examId);
            JOptionPane.showMessageDialog(mainFrame,
                    "Seating arrangement completed!\nAssigned: " + toAssign + " students\nAvailable seats: " + remaining,
                    "Allocation Complete", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshTheme() {
        buildUI();
    }
}

