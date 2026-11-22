package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.FormDialog;
import com.examsphere.components.RoundedPanel;
import com.examsphere.exception.ScheduleConflictException;
import com.examsphere.model.Course;
import com.examsphere.model.Exam;
import com.examsphere.model.ExamRoom;
import com.examsphere.model.Teacher;
import com.examsphere.service.AuthService;
import com.examsphere.service.CourseService;
import com.examsphere.service.ExamService;
import com.examsphere.service.SeatAllocationService;
import com.examsphere.util.Constants;
import com.examsphere.util.IdGenerator;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Examination scheduling panel facilitating exam slot creation, room and invigilator
 * assignment, schedule conflict detection, and timetable administration.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class ExamPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final ExamService examService = ExamService.getInstance();
    private final CourseService courseService = CourseService.getInstance();
    private final SeatAllocationService seatService = SeatAllocationService.getInstance();
    private final AuthService authService = AuthService.getInstance();

    private DataTablePanel tablePanel;
    private JComboBox<String> typeFilterCombo;

    public ExamPanel(MainFrame mainFrame) {
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

        // Top Controls Bar
        RoundedPanel topBar = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        topBar.setLayout(new BorderLayout(12, 12));
        topBar.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Official Examination Timetable & Slot Administration");
        title.setFont(theme.getBoldFont(18));
        title.setForeground(theme.getPrimaryText());
        topBar.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        typeFilterCombo = new JComboBox<>(new String[]{"All Exam Types", "MIDTERM", "FINAL", "CLASS_TEST", "QUIZ"});
        typeFilterCombo.setFont(theme.getFont(13));
        typeFilterCombo.addActionListener(e -> applyFilter());
        actions.add(new JLabel("Type:"));
        actions.add(typeFilterCombo);

        CustomButton createBtn = new CustomButton("+ Schedule Exam", theme.getPrimaryAccent(), Color.WHITE);
        createBtn.addActionListener(e -> openCreateExamDialog());
        actions.add(createBtn);

        CustomButton editBtn = new CustomButton("Edit Slot", theme.getSecondarySurface(), theme.getPrimaryText());
        editBtn.addActionListener(e -> openEditExamDialog());
        actions.add(editBtn);

        CustomButton cancelBtn = new CustomButton("Cancel Exam", theme.getDanger(), Color.WHITE);
        cancelBtn.addActionListener(e -> cancelSelectedExam());
        actions.add(cancelBtn);

        topBar.add(actions, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Data Table
        String[] cols = {"Exam ID", "Course", "Type", "Semester", "Date", "Time Slot", "Assigned Room", "Invigilator", "Status"};
        tablePanel = new DataTablePanel(cols, getTableData(examService.getAllExams()));
        tablePanel.addSearchBar("Search examinations...");

        RoundedPanel tableCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        tableCard.add(tablePanel, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private Object[][] getTableData(List<Exam> exams) {
        Object[][] data = new Object[exams.size()][9];
        for (int i = 0; i < exams.size(); i++) {
            Exam e = exams.get(i);
            data[i][0] = e.getExamId();
            data[i][1] = e.getCourseCode();
            data[i][2] = e.getExamType();
            data[i][3] = "Semester " + e.getSemester();
            data[i][4] = e.getExamDate();
            data[i][5] = e.getStartTime() + " - " + e.getEndTime();
            data[i][6] = e.getRoomId() != null ? e.getRoomId() : "Unassigned";
            data[i][7] = e.getInvigilatorId() != null ? e.getInvigilatorId() : "Unassigned";
            data[i][8] = e.getStatus();
        }
        return data;
    }

    private void applyFilter() {
        String type = (String) typeFilterCombo.getSelectedItem();
        List<Exam> list = examService.getAllExams();
        if (type != null && !type.startsWith("All")) {
            list = list.stream().filter(e -> type.equalsIgnoreCase(e.getExamType())).collect(Collectors.toList());
        }
        tablePanel.setData(getTableData(list));
    }

    private void openCreateExamDialog() {
        FormDialog dialog = new FormDialog(mainFrame, "Schedule New Examination Session", 500, 520);

        JTextField examIdField = new JTextField(IdGenerator.generateId("EX"));
        examIdField.setEditable(false);

        List<Course> courses = courseService.getAllCourses();
        String[] courseOptions = courses.stream().map(Course::getCourseCode).toArray(String[]::new);
        if (courseOptions.length == 0) courseOptions = new String[]{"CSE221"};
        JComboBox<String> courseCombo = new JComboBox<>(courseOptions);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"MIDTERM", "FINAL", "CLASS_TEST", "QUIZ"});
        JComboBox<Integer> semCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        JTextField dateField = new JTextField("2026-12-01");
        JTextField startTimeField = new JTextField("10:00");
        JTextField endTimeField = new JTextField("12:00");

        List<ExamRoom> rooms = seatService.getAllRooms();
        String[] roomOptions = rooms.stream().map(ExamRoom::getRoomId).toArray(String[]::new);
        if (roomOptions.length == 0) roomOptions = new String[]{"KT-320"};
        JComboBox<String> roomCombo = new JComboBox<>(roomOptions);

        List<Teacher> teachers = authService.getAllUsers().stream()
                .filter(u -> u instanceof Teacher).map(u -> (Teacher) u).collect(Collectors.toList());
        String[] teacherOptions = teachers.stream().map(Teacher::getUserId).toArray(String[]::new);
        if (teacherOptions.length == 0) teacherOptions = new String[]{"TCH001"};
        JComboBox<String> invigilatorCombo = new JComboBox<>(teacherOptions);

        dialog.addFormField("Exam ID:", examIdField);
        dialog.addFormField("Target Course *:", courseCombo);
        dialog.addFormField("Exam Type *:", typeCombo);
        dialog.addFormField("Semester *:", semCombo);
        dialog.addFormField("Exam Date (YYYY-MM-DD) *:", dateField);
        dialog.addFormField("Start Time (HH:mm) *:", startTimeField);
        dialog.addFormField("End Time (HH:mm) *:", endTimeField);
        dialog.addFormField("Allocated Room *:", roomCombo);
        dialog.addFormField("Faculty Invigilator *:", invigilatorCombo);

        CustomButton saveBtn = new CustomButton("Confirm Schedule", theme.getPrimaryAccent(), Color.WHITE);
        saveBtn.addActionListener(e -> {
            String examId = examIdField.getText().trim();
            String course = (String) courseCombo.getSelectedItem();
            String type = (String) typeCombo.getSelectedItem();
            int sem = (Integer) semCombo.getSelectedItem();
            String date = dateField.getText().trim();
            String start = startTimeField.getText().trim();
            String end = endTimeField.getText().trim();
            String room = (String) roomCombo.getSelectedItem();
            String invigilator = (String) invigilatorCombo.getSelectedItem();

            Exam exam = new Exam(examId, course, type, sem, date, start, end);
            exam.setRoomId(room);
            exam.setInvigilatorId(invigilator);
            exam.setStatus("SCHEDULED");

            try {
                examService.createExam(exam);
                dialog.dispose();
                applyFilter();
                JOptionPane.showMessageDialog(mainFrame, "Examination scheduled successfully without conflicts!", "Schedule Confirmed", JOptionPane.INFORMATION_MESSAGE);
            } catch (ScheduleConflictException ex) {
                JOptionPane.showMessageDialog(dialog, "Conflict Detected:\n" + ex.getMessage(), "Scheduling Conflict Warning", JOptionPane.WARNING_MESSAGE);
            }
        });

        CustomButton cancelAction = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
        cancelAction.addActionListener(e -> dialog.dispose());

        dialog.getButtonPanel().add(cancelAction);
        dialog.getButtonPanel().add(saveBtn);
        dialog.setVisible(true);
    }

    private void openEditExamDialog() {
        int row = tablePanel.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an examination to modify.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String examId = (String) tablePanel.getTable().getValueAt(row, 0);
        Exam exam = examService.getExamById(examId);
        if (exam == null) return;

        FormDialog dialog = new FormDialog(mainFrame, "Modify Exam Session — " + exam.getExamId(), 480, 460);

        JTextField dateField = new JTextField(exam.getExamDate());
        JTextField startTimeField = new JTextField(exam.getStartTime());
        JTextField endTimeField = new JTextField(exam.getEndTime());

        List<ExamRoom> rooms = seatService.getAllRooms();
        JComboBox<String> roomCombo = new JComboBox<>(rooms.stream().map(ExamRoom::getRoomId).toArray(String[]::new));
        roomCombo.setSelectedItem(exam.getRoomId());

        List<Teacher> teachers = authService.getAllUsers().stream()
                .filter(u -> u instanceof Teacher).map(u -> (Teacher) u).collect(Collectors.toList());
        JComboBox<String> invigilatorCombo = new JComboBox<>(teachers.stream().map(Teacher::getUserId).toArray(String[]::new));
        invigilatorCombo.setSelectedItem(exam.getInvigilatorId());

        JComboBox<String> statusCombo = new JComboBox<>(new String[]{"SCHEDULED", "ONGOING", "COMPLETED", "CANCELLED"});
        statusCombo.setSelectedItem(exam.getStatus());

        dialog.addFormField("Exam Date (YYYY-MM-DD):", dateField);
        dialog.addFormField("Start Time (HH:mm):", startTimeField);
        dialog.addFormField("End Time (HH:mm):", endTimeField);
        dialog.addFormField("Room Assignment:", roomCombo);
        dialog.addFormField("Invigilator:", invigilatorCombo);
        dialog.addFormField("Session Status:", statusCombo);

        CustomButton updateBtn = new CustomButton("Save Changes", theme.getPrimaryAccent(), Color.WHITE);
        updateBtn.addActionListener(e -> {
            exam.setExamDate(dateField.getText().trim());
            exam.setStartTime(startTimeField.getText().trim());
            exam.setEndTime(endTimeField.getText().trim());
            exam.setRoomId((String) roomCombo.getSelectedItem());
            exam.setInvigilatorId((String) invigilatorCombo.getSelectedItem());
            exam.setStatus((String) statusCombo.getSelectedItem());

            try {
                examService.updateExam(exam);
                dialog.dispose();
                applyFilter();
                JOptionPane.showMessageDialog(mainFrame, "Examination session updated successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
            } catch (ScheduleConflictException ex) {
                JOptionPane.showMessageDialog(dialog, "Scheduling Conflict:\n" + ex.getMessage(), "Conflict Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        CustomButton cancelAction = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
        cancelAction.addActionListener(e -> dialog.dispose());

        dialog.getButtonPanel().add(cancelAction);
        dialog.getButtonPanel().add(updateBtn);
        dialog.setVisible(true);
    }

    private void cancelSelectedExam() {
        int row = tablePanel.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an examination to cancel.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String examId = (String) tablePanel.getTable().getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to mark exam " + examId + " as CANCELLED?",
                "Confirm Cancellation", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            examService.cancelExam(examId);
            applyFilter();
            JOptionPane.showMessageDialog(mainFrame, "Examination cancelled.", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshTheme() {
        buildUI();
    }
}

