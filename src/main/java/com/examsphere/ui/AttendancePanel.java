package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.RoundedPanel;
import com.examsphere.components.StatCard;
import com.examsphere.model.AttendanceRecord;
import com.examsphere.model.Exam;
import com.examsphere.model.Student;
import com.examsphere.service.AttendanceService;
import com.examsphere.service.ExamService;
import com.examsphere.service.StudentService;
import com.examsphere.util.Constants;
import com.examsphere.util.IdGenerator;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Attendance management panel facilitating invigilator roll-calls, real-time presence
 * marking, late status logging, and exam attendance analytics.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class AttendancePanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final AttendanceService attendanceService = AttendanceService.getInstance();
    private final ExamService examService = ExamService.getInstance();
    private final StudentService studentService = StudentService.getInstance();

    private JComboBox<String> examCombo;
    private DataTablePanel attendanceTable;
    private StatCard presentCard;
    private StatCard absentCard;
    private StatCard lateCard;

    public AttendancePanel(MainFrame mainFrame) {
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

        JLabel title = new JLabel("Examination Attendance Roll-Call & Presence Tracker");
        title.setFont(theme.getBoldFont(18));
        title.setForeground(theme.getPrimaryText());
        topBar.add(title, BorderLayout.WEST);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controls.setOpaque(false);

        List<Exam> exams = examService.getAllExams();
        String[] examOpts = exams.stream().map(Exam::getExamId).toArray(String[]::new);
        if (examOpts.length == 0) examOpts = new String[]{"EX-2026-001"};
        examCombo = new JComboBox<>(examOpts);
        examCombo.setFont(theme.getFont(13));
        examCombo.addActionListener(e -> refreshAttendance());
        controls.add(new JLabel("Select Exam:"));
        controls.add(examCombo);

        CustomButton markPresentBtn = new CustomButton("Mark Present", theme.getSuccess(), Color.WHITE);
        markPresentBtn.addActionListener(e -> updateStatus("PRESENT"));
        controls.add(markPresentBtn);

        CustomButton markAbsentBtn = new CustomButton("Mark Absent", theme.getDanger(), Color.WHITE);
        markAbsentBtn.addActionListener(e -> updateStatus("ABSENT"));
        controls.add(markAbsentBtn);

        CustomButton markLateBtn = new CustomButton("Mark Late", theme.getWarning(), Color.WHITE);
        markLateBtn.addActionListener(e -> updateStatus("LATE"));
        controls.add(markLateBtn);

        topBar.add(controls, BorderLayout.EAST);
        add(topBar, BorderLayout.NORTH);

        // Center Content: KPI Stats Bar + Table
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        String currentExam = (String) examCombo.getSelectedItem();
        Map<String, Integer> stats = attendanceService.getAttendanceStats(currentExam);

        JPanel statRow = new JPanel(new GridLayout(1, 3, Constants.SPACING, 0));
        statRow.setOpaque(false);
        statRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        presentCard = new StatCard("PRESENT STUDENTS", String.valueOf(stats.getOrDefault("PRESENT", 0)),
                "Verified examinees in hall", theme.getSuccess());
        absentCard = new StatCard("ABSENT STUDENTS", String.valueOf(stats.getOrDefault("ABSENT", 0)),
                "Unreported examinees", theme.getDanger());
        lateCard = new StatCard("LATE ARRIVALS", String.valueOf(stats.getOrDefault("LATE", 0)),
                "Admitted with time penalty", theme.getWarning());

        statRow.add(presentCard);
        statRow.add(absentCard);
        statRow.add(lateCard);

        center.add(statRow);
        center.add(Box.createVerticalStrut(Constants.SPACING));

        String[] cols = {"Student ID", "Full Name", "Department", "Attendance Status", "Recorded Timestamp", "Remarks"};
        attendanceTable = new DataTablePanel(cols, getAttendanceData());
        attendanceTable.addSearchBar("Filter examinee attendance...");

        RoundedPanel tableCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        tableCard.add(attendanceTable, BorderLayout.CENTER);

        center.add(tableCard);
        add(center, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private Object[][] getAttendanceData() {
        String examId = (String) examCombo.getSelectedItem();
        List<Student> students = studentService.getAllStudents();
        Object[][] data = new Object[students.size()][6];

        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            AttendanceRecord r = attendanceService.getAttendance(examId, s.getStudentId());

            data[i][0] = s.getStudentId();
            data[i][1] = s.getFullName();
            data[i][2] = s.getDepartment();
            data[i][3] = r != null ? r.getStatus() : "ABSENT";
            data[i][4] = r != null && r.getTimestamp() != null ? r.getTimestamp() : "—";
            data[i][5] = r != null && r.getRemarks() != null ? r.getRemarks() : "Standard";
        }
        return data;
    }

    private void updateStatus(String status) {
        int row = attendanceTable.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select an examinee from the table.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) attendanceTable.getTable().getValueAt(row, 0);
        String examId = (String) examCombo.getSelectedItem();

        AttendanceRecord record = new AttendanceRecord(IdGenerator.generateId("ATT"), examId, studentId, status);
        attendanceService.markAttendance(record);

        refreshAttendance();
    }

    private void refreshAttendance() {
        attendanceTable.setData(getAttendanceData());
        String currentExam = (String) examCombo.getSelectedItem();
        Map<String, Integer> stats = attendanceService.getAttendanceStats(currentExam);
        presentCard.updateValue(String.valueOf(stats.getOrDefault("PRESENT", 0)));
        absentCard.updateValue(String.valueOf(stats.getOrDefault("ABSENT", 0)));
        lateCard.updateValue(String.valueOf(stats.getOrDefault("LATE", 0)));
    }

    public void refreshTheme() {
        buildUI();
    }
}

