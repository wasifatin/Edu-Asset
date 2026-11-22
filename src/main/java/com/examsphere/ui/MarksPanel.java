package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.FormDialog;
import com.examsphere.components.RoundedPanel;
import com.examsphere.exception.InvalidMarksException;
import com.examsphere.model.Course;
import com.examsphere.model.MarkRecord;
import com.examsphere.model.Student;
import com.examsphere.service.CourseService;
import com.examsphere.service.MarksService;
import com.examsphere.service.StudentService;
import com.examsphere.util.Constants;
import com.examsphere.util.IdGenerator;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Continuous academic assessment & marks management panel supporting:
 *
 * Theory Course: Attendance (7), Assignment (5), Quiz 1 (15), Quiz 2 (15), Quiz 3 (15),
 *                Quiz Average (15), Presentation (8), Midterm (25), Final Exam (40) = 100 Marks
 * Lab Course:    Attendance (10), Lab Report (25), Lab Performance (25), Final Exam (40) = 100 Marks
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class MarksPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final MarksService marksService = MarksService.getInstance();
    private final CourseService courseService = CourseService.getInstance();
    private final StudentService studentService = StudentService.getInstance();

    private JComboBox<String> courseCombo;
    private DataTablePanel marksTable;
    private RoundedPanel tableCard;
    private JLabel schemeBadge;

    public MarksPanel(MainFrame mainFrame) {
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

        // Header Card
        RoundedPanel headerCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        headerCard.setLayout(new BorderLayout(12, 12));
        headerCard.setBorder(new EmptyBorder(10, 16, 10, 16));

        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        titlePanel.setOpaque(false);

        JLabel title = new JLabel("Academic Assessment & Marks Registry");
        title.setFont(theme.getBoldFont(18));
        title.setForeground(theme.getPrimaryText());
        titlePanel.add(title);

        schemeBadge = new JLabel();
        schemeBadge.setFont(theme.getFont(12));
        schemeBadge.setForeground(theme.getSecondaryText());
        titlePanel.add(schemeBadge);

        headerCard.add(titlePanel, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        List<Course> courses = courseService.getAllCourses();
        String[] courseOpts = courses.stream().map(Course::getCourseCode).toArray(String[]::new);
        if (courseOpts.length == 0) courseOpts = new String[]{"CSE221"};
        courseCombo = new JComboBox<>(courseOpts);
        courseCombo.setFont(theme.getFont(13));
        courseCombo.addActionListener(e -> rebuildTableForCourse());
        actions.add(new JLabel("Course:"));
        actions.add(courseCombo);

        CustomButton enterMarksBtn = new CustomButton("Enter / Edit Marks", theme.getPrimaryAccent(), Color.WHITE);
        enterMarksBtn.addActionListener(e -> openEnterMarksDialog());
        actions.add(enterMarksBtn);

        CustomButton deleteMarksBtn = new CustomButton("Delete", theme.getDanger(), Color.WHITE);
        deleteMarksBtn.addActionListener(e -> deleteSelectedMarks());
        actions.add(deleteMarksBtn);

        headerCard.add(actions, BorderLayout.EAST);
        add(headerCard, BorderLayout.NORTH);

        // Table Card
        tableCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));

        rebuildTableForCourse();
        add(tableCard, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private boolean isCurrentCourseLab() {
        String courseCode = (String) courseCombo.getSelectedItem();
        if (courseCode == null) return false;
        Course c = courseService.getCourseByCode(courseCode);
        return c != null && c.isLabCourse();
    }

    private void rebuildTableForCourse() {
        boolean isLab = isCurrentCourseLab();
        if (schemeBadge != null) {
            schemeBadge.setText(isLab
                    ? "• [LAB: Attendance 10 + Report 25 + Performance 25 + Final 40 = 100]"
                    : "• [THEORY: Attendance 7 + Assignment 5 + Quizzes (Avg 15) + Presentation 8 + Midterm 25 + Final 40 = 100]");
        }

        tableCard.removeAll();
        String[] cols;
        if (isLab) {
            cols = new String[]{"Student ID", "Student Name", "Attendance (/10)", "Lab Report (/25)", "Lab Performance (/25)", "Final (/40)", "Total (/100)", "Grade"};
        } else {
            cols = new String[]{"Student ID", "Student Name", "Attendance (/7)", "Assignment (/5)", "Quiz Avg (/15)", "Presentation (/8)", "Midterm (/25)", "Final (/40)", "Total (/100)", "Grade"};
        }

        marksTable = new DataTablePanel(cols, getMarksData(isLab));
        marksTable.addSearchBar("Search enrolled students...");
        tableCard.add(marksTable, BorderLayout.CENTER);
        tableCard.revalidate();
        tableCard.repaint();
    }

    private Object[][] getMarksData(boolean isLab) {
        String courseCode = (String) courseCombo.getSelectedItem();
        List<Student> students = studentService.getAllStudents();

        if (isLab) {
            Object[][] data = new Object[students.size()][8];
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                MarkRecord m = marksService.getMarks(courseCode, s.getStudentId());

                data[i][0] = s.getStudentId();
                data[i][1] = s.getFullName();
                data[i][2] = m != null ? m.getAttendance() : "—";
                data[i][3] = m != null ? m.getLabReport() : "—";
                data[i][4] = m != null ? m.getLabPerformance() : "—";
                data[i][5] = m != null ? m.getFinalExam() : "—";
                data[i][6] = m != null ? String.format("%.2f / 100", m.getTotalMarks()) : "—";
                data[i][7] = m != null ? m.getGrade() : "—";
            }
            return data;
        } else {
            Object[][] data = new Object[students.size()][10];
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                MarkRecord m = marksService.getMarks(courseCode, s.getStudentId());

                data[i][0] = s.getStudentId();
                data[i][1] = s.getFullName();
                data[i][2] = m != null ? m.getAttendance() : "—";
                data[i][3] = m != null ? m.getAssignment() : "—";
                data[i][4] = m != null ? m.getQuizAverage() : "—";
                data[i][5] = m != null ? m.getPresentation() : "—";
                data[i][6] = m != null ? m.getMidterm() : "—";
                data[i][7] = m != null ? m.getFinalExam() : "—";
                data[i][8] = m != null ? String.format("%.2f / 100", m.getTotalMarks()) : "—";
                data[i][9] = m != null ? m.getGrade() : "—";
            }
            return data;
        }
    }

    private void openEnterMarksDialog() {
        int row = marksTable.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student from the table to record or edit marks.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) marksTable.getTable().getValueAt(row, 0);
        String studentName = (String) marksTable.getTable().getValueAt(row, 1);
        String courseCode = (String) courseCombo.getSelectedItem();
        boolean isLab = isCurrentCourseLab();

        MarkRecord existing = marksService.getMarks(courseCode, studentId);
        if (existing == null) {
            existing = new MarkRecord(IdGenerator.generateId("MRK"), courseCode, studentId, isLab ? "LAB" : "THEORY");
        }

        FormDialog dialog = new FormDialog(mainFrame,
                "Enter " + (isLab ? "Lab" : "Theory") + " Marks — " + studentName + " (" + courseCode + ")",
                480, isLab ? 420 : 540);

        if (isLab) {
            JTextField attField = new JTextField(String.valueOf(existing.getAttendance()));
            JTextField repField = new JTextField(String.valueOf(existing.getLabReport()));
            JTextField perfField = new JTextField(String.valueOf(existing.getLabPerformance()));
            JTextField finalField = new JTextField(String.valueOf(existing.getFinalExam()));

            dialog.addFormField("Attendance (Max 10.0):", attField);
            dialog.addFormField("Lab Report (Max 25.0):", repField);
            dialog.addFormField("Lab Performance (Max 25.0):", perfField);
            dialog.addFormField("Final Exam / Viva (Max 40.0):", finalField);

            final MarkRecord target = existing;
            CustomButton saveBtn = new CustomButton("Save Lab Assessment", theme.getPrimaryAccent(), Color.WHITE);
            saveBtn.addActionListener(e -> {
                try {
                    double att = Double.parseDouble(attField.getText().trim());
                    double rep = Double.parseDouble(repField.getText().trim());
                    double perf = Double.parseDouble(perfField.getText().trim());
                    double fin = Double.parseDouble(finalField.getText().trim());

                    target.setCourseType("LAB");
                    target.setAttendance(att);
                    target.setLabReport(rep);
                    target.setLabPerformance(perf);
                    target.setFinalExam(fin);

                    marksService.enterMarks(target);
                    dialog.dispose();
                    rebuildTableForCourse();
                    JOptionPane.showMessageDialog(mainFrame,
                            String.format("Lab marks saved successfully!\nTotal: %.2f / 100.0\nLetter Grade: %s",
                                    target.getTotalMarks(), target.getGrade()),
                            "Assessment Saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(dialog, "Please enter valid numerical scores.", "Input Format Error", JOptionPane.ERROR_MESSAGE);
                } catch (InvalidMarksException ime) {
                    JOptionPane.showMessageDialog(dialog, ime.getMessage(), "Marks Validation Failed", JOptionPane.WARNING_MESSAGE);
                }
            });

            CustomButton cancelAction = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
            cancelAction.addActionListener(e -> dialog.dispose());

            dialog.getButtonPanel().add(cancelAction);
            dialog.getButtonPanel().add(saveBtn);
        } else {
            // Theory Course
            JTextField attField = new JTextField(String.valueOf(existing.getAttendance()));
            JTextField asgField = new JTextField(String.valueOf(existing.getAssignment()));
            JTextField q1Field = new JTextField(String.valueOf(existing.getQuiz1()));
            JTextField q2Field = new JTextField(String.valueOf(existing.getQuiz2()));
            JTextField q3Field = new JTextField(String.valueOf(existing.getQuiz3()));
            JTextField qAvgField = new JTextField(String.valueOf(existing.getQuizAverage()));
            JTextField presField = new JTextField(String.valueOf(existing.getPresentation()));
            JTextField midField = new JTextField(String.valueOf(existing.getMidterm()));
            JTextField finalField = new JTextField(String.valueOf(existing.getFinalExam()));

            dialog.addFormField("Attendance (Max 7.0):", attField);
            dialog.addFormField("Assignment (Max 5.0):", asgField);
            dialog.addFormField("Quiz 1 (Max 15.0):", q1Field);
            dialog.addFormField("Quiz 2 (Max 15.0):", q2Field);
            dialog.addFormField("Quiz 3 (Max 15.0):", q3Field);
            dialog.addFormField("Quiz Average (Max 15.0):", qAvgField);
            dialog.addFormField("Presentation (Max 8.0):", presField);
            dialog.addFormField("Midterm Examination (Max 25.0):", midField);
            dialog.addFormField("Final Examination (Max 40.0):", finalField);

            final MarkRecord target = existing;
            CustomButton saveBtn = new CustomButton("Save Theory Assessment", theme.getPrimaryAccent(), Color.WHITE);
            saveBtn.addActionListener(e -> {
                try {
                    double att = Double.parseDouble(attField.getText().trim());
                    double asg = Double.parseDouble(asgField.getText().trim());
                    double q1 = Double.parseDouble(q1Field.getText().trim());
                    double q2 = Double.parseDouble(q2Field.getText().trim());
                    double q3 = Double.parseDouble(q3Field.getText().trim());
                    double qAvg = qAvgField.getText().trim().isEmpty() ? ((q1 + q2 + q3) / 3.0) : Double.parseDouble(qAvgField.getText().trim());
                    double pres = Double.parseDouble(presField.getText().trim());
                    double mid = Double.parseDouble(midField.getText().trim());
                    double fin = Double.parseDouble(finalField.getText().trim());

                    target.setCourseType("THEORY");
                    target.setAttendance(att);
                    target.setAssignment(asg);
                    target.setQuiz1(q1);
                    target.setQuiz2(q2);
                    target.setQuiz3(q3);
                    target.setQuizAverage(qAvg);
                    target.setPresentation(pres);
                    target.setMidterm(mid);
                    target.setFinalExam(fin);

                    marksService.enterMarks(target);
                    dialog.dispose();
                    rebuildTableForCourse();
                    JOptionPane.showMessageDialog(mainFrame,
                            String.format("Theory marks saved successfully!\nTotal: %.2f / 100.0\nLetter Grade: %s",
                                    target.getTotalMarks(), target.getGrade()),
                            "Assessment Saved", JOptionPane.INFORMATION_MESSAGE);
                } catch (NumberFormatException nfe) {
                    JOptionPane.showMessageDialog(dialog, "Please enter valid numerical scores.", "Input Format Error", JOptionPane.ERROR_MESSAGE);
                } catch (InvalidMarksException ime) {
                    JOptionPane.showMessageDialog(dialog, ime.getMessage(), "Marks Validation Failed", JOptionPane.WARNING_MESSAGE);
                }
            });

            CustomButton cancelAction = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
            cancelAction.addActionListener(e -> dialog.dispose());

            dialog.getButtonPanel().add(cancelAction);
            dialog.getButtonPanel().add(saveBtn);
        }

        dialog.setVisible(true);
    }

    private void deleteSelectedMarks() {
        int row = marksTable.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student row to remove assessment marks.",
                    "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String studentId = (String) marksTable.getTable().getValueAt(row, 0);
        String studentName = (String) marksTable.getTable().getValueAt(row, 1);
        String courseCode = (String) courseCombo.getSelectedItem();

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to delete assessment marks for " + studentName + " (" + studentId + ") in " + courseCode + "?",
                "Confirm Remove Marks", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            marksService.deleteMarks(courseCode, studentId);
            rebuildTableForCourse();
            JOptionPane.showMessageDialog(mainFrame, "Assessment marks removed successfully.", "Marks Deleted", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshTheme() {
        buildUI();
    }
}
