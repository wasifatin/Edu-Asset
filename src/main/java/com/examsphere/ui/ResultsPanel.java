package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.RoundedPanel;
import com.examsphere.components.StatCard;
import com.examsphere.exception.AuthorizationException;
import com.examsphere.model.Result;
import com.examsphere.model.Student;
import com.examsphere.service.AuthService;
import com.examsphere.service.ResultService;
import com.examsphere.service.StudentService;
import com.examsphere.util.Constants;
import com.examsphere.util.GradeCalculator;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Map;

/**
 * Result processing and publication panel providing GPA computation, semester transcripts,
 * pass/fail analytics, and authorized controller release workflows.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class ResultsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final ResultService resultService = ResultService.getInstance();
    private final StudentService studentService = StudentService.getInstance();
    private final AuthService authService = AuthService.getInstance();

    private JComboBox<Integer> semesterCombo;
    private DataTablePanel resultsTable;
    private StatCard totalStudentsCard;
    private StatCard passRateCard;
    private StatCard avgGpaCard;

    public ResultsPanel(MainFrame mainFrame) {
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

        JLabel title = new JLabel("Official Semester Results & Academic Transcripts");
        title.setFont(theme.getBoldFont(18));
        title.setForeground(theme.getPrimaryText());
        headerCard.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        semesterCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        semesterCombo.setSelectedItem(5);
        semesterCombo.setFont(theme.getFont(13));
        semesterCombo.addActionListener(e -> refreshResults());
        actions.add(new JLabel("Semester:"));
        actions.add(semesterCombo);

        CustomButton calcBtn = new CustomButton("Compute GPA", theme.getSecondarySurface(), theme.getPrimaryText());
        calcBtn.addActionListener(e -> calculateResults());
        actions.add(calcBtn);

        CustomButton transcriptBtn = new CustomButton("View Transcript", theme.getInfo(), Color.WHITE);
        transcriptBtn.addActionListener(e -> openTranscriptDialog());
        actions.add(transcriptBtn);

        CustomButton publishBtn = new CustomButton("Publish Batch", theme.getSuccess(), Color.WHITE);
        publishBtn.addActionListener(e -> publishSemesterResults());
        actions.add(publishBtn);

        headerCard.add(actions, BorderLayout.EAST);
        add(headerCard, BorderLayout.NORTH);

        // Center Content
        JPanel center = new JPanel();
        center.setLayout(new BoxLayout(center, BoxLayout.Y_AXIS));
        center.setOpaque(false);

        // Stat cards
        int currentSem = (Integer) semesterCombo.getSelectedItem();
        Map<String, Object> stats = resultService.getResultStats(currentSem);

        JPanel statRow = new JPanel(new GridLayout(1, 3, Constants.SPACING, 0));
        statRow.setOpaque(false);
        statRow.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));

        totalStudentsCard = new StatCard("EVALUATED CANDIDATES", String.valueOf(stats.getOrDefault("total", 0)),
                "Semester " + currentSem + " examinees", theme.getPrimaryAccent());
        passRateCard = new StatCard("SEMESTER PASS RATE", stats.getOrDefault("passRate", 0.0) + "%",
                "Minimum CGPA >= 2.00 threshold", theme.getSuccess());
        avgGpaCard = new StatCard("SEMESTER AVERAGE GPA", String.valueOf(stats.getOrDefault("avgGPA", 0.0)),
                "Overall batch academic performance", theme.getWarning());

        statRow.add(totalStudentsCard);
        statRow.add(passRateCard);
        statRow.add(avgGpaCard);

        center.add(statRow);
        center.add(Box.createVerticalStrut(Constants.SPACING));

        // Results Table
        String[] cols = {"Result ID", "Student ID", "Student Name", "Semester", "Term GPA", "Cumulative CGPA", "Publication Status", "Authorized By"};
        resultsTable = new DataTablePanel(cols, getResultsData());
        resultsTable.addSearchBar("Filter results...");

        RoundedPanel tableCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        tableCard.add(resultsTable, BorderLayout.CENTER);

        center.add(tableCard);
        add(center, BorderLayout.CENTER);

        revalidate();
        repaint();
    }

    private Object[][] getResultsData() {
        int sem = (Integer) semesterCombo.getSelectedItem();
        List<Result> list = resultService.getResultsBySemester(sem);

        Object[][] data = new Object[list.size()][8];
        for (int i = 0; i < list.size(); i++) {
            Result r = list.get(i);
            Student stu = studentService.getStudentById(r.getStudentId());

            data[i][0] = r.getResultId();
            data[i][1] = r.getStudentId();
            data[i][2] = stu != null ? stu.getFullName() : "N/A";
            data[i][3] = "Semester " + r.getSemester();
            data[i][4] = String.format("%.2f", r.getGpa());
            data[i][5] = String.format("%.2f", r.getCgpa());
            data[i][6] = r.isPublished() ? "PUBLISHED" : "UNPUBLISHED (DRAFT)";
            data[i][7] = r.getPublishedBy() != null ? r.getPublishedBy() : "Pending";
        }
        return data;
    }

    private void calculateResults() {
        int sem = (Integer) semesterCombo.getSelectedItem();
        resultService.calculateResults(null, sem);
        refreshResults();
        JOptionPane.showMessageDialog(mainFrame, "Term GPAs and CGPAs computed for Semester " + sem, "Calculation Complete", JOptionPane.INFORMATION_MESSAGE);
    }

    private void publishSemesterResults() {
        int sem = (Integer) semesterCombo.getSelectedItem();
        String currentUserId = authService.getCurrentUser() != null ? authService.getCurrentUser().getUserId() : "ADMIN001";

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to publish results for Semester " + sem + "?\nPublished results will become accessible to students.",
                "Confirm Official Publication", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                resultService.publishResults(sem, currentUserId);
                refreshResults();
                JOptionPane.showMessageDialog(mainFrame, "Results for Semester " + sem + " have been officially published!", "Results Published", JOptionPane.INFORMATION_MESSAGE);
            } catch (AuthorizationException e) {
                JOptionPane.showMessageDialog(mainFrame, e.getMessage(), "Authorization Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void openTranscriptDialog() {
        int row = resultsTable.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a student result to inspect transcript.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sid = (String) resultsTable.getTable().getValueAt(row, 1);
        int sem = (Integer) semesterCombo.getSelectedItem();
        Result result = resultService.getResult(sid, sem);
        Student student = studentService.getStudentById(sid);

        if (result == null || student == null) return;

        JDialog dialog = new JDialog(mainFrame, "Academic Transcript — " + student.getFullName(), true);
        dialog.setSize(560, 500);
        dialog.setLocationRelativeTo(mainFrame);
        dialog.setLayout(new BorderLayout(Constants.SPACING, Constants.SPACING));
        dialog.getContentPane().setBackground(theme.getMainSurface());

        JPanel header = new JPanel();
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setOpaque(false);
        header.setBorder(new EmptyBorder(16, 20, 10, 20));

        JLabel uniLabel = new JLabel("DAFFODIL INTERNATIONAL UNIVERSITY");
        uniLabel.setFont(theme.getBoldFont(18));
        uniLabel.setForeground(theme.getPrimaryAccent());
        JLabel subLabel = new JLabel("Office of the Controller of Examinations — Semester Grade Report");
        subLabel.setFont(theme.getFont(13));
        subLabel.setForeground(theme.getSecondaryText());

        header.add(uniLabel);
        header.add(subLabel);
        header.add(Box.createVerticalStrut(10));

        JLabel info = new JLabel("Student: " + student.getFullName() + " | ID: " + student.getStudentId() +
                " | Dept: " + student.getDepartment() + " | Term: Semester " + sem);
        info.setFont(theme.getBoldFont(13));
        header.add(info);

        dialog.add(header, BorderLayout.NORTH);

        // Course breakdown table
        String[] cols = {"Course Code", "Credit Hours", "Grade Points", "Letter Grade", "Remark"};
        Map<String, String> grades = result.getCourseGrades();
        Map<String, Double> points = result.getCourseGradePoints();
        Map<String, Double> credits = result.getCourseCredits();

        Object[][] data = new Object[grades.size()][5];
        int idx = 0;
        for (String c : grades.keySet()) {
            String gr = grades.getOrDefault(c, "F");
            data[idx][0] = c;
            data[idx][1] = credits.getOrDefault(c, 3.0);
            data[idx][2] = points.getOrDefault(c, 0.0);
            data[idx][3] = gr;
            data[idx][4] = GradeCalculator.getRemark(gr);
            idx++;
        }

        DataTablePanel transcriptTable = new DataTablePanel(cols, data);
        transcriptTable.setBorder(new EmptyBorder(0, 20, 0, 20));
        dialog.add(transcriptTable, BorderLayout.CENTER);

        // Footer
        JPanel footer = new JPanel(new BorderLayout());
        footer.setOpaque(false);
        footer.setBorder(new EmptyBorder(10, 20, 16, 20));

        JLabel gpaLabel = new JLabel("Term GPA: " + String.format("%.2f", result.getGpa()) +
                "    |    Cumulative CGPA: " + String.format("%.2f", result.getCgpa()) +
                "    |    Status: " + (result.isPublished() ? "OFFICIAL" : "UNOFFICIAL"));
        gpaLabel.setFont(theme.getBoldFont(14));
        gpaLabel.setForeground(result.getGpa() >= 2.0 ? theme.getSuccess() : theme.getDanger());
        footer.add(gpaLabel, BorderLayout.WEST);

        CustomButton closeBtn = new CustomButton("Close", theme.getSecondarySurface(), theme.getPrimaryText());
        closeBtn.addActionListener(e -> dialog.dispose());
        footer.add(closeBtn, BorderLayout.EAST);

        dialog.add(footer, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }

    private void refreshResults() {
        resultsTable.setData(getResultsData());
        int sem = (Integer) semesterCombo.getSelectedItem();
        Map<String, Object> stats = resultService.getResultStats(sem);
        totalStudentsCard.updateValue(String.valueOf(stats.getOrDefault("total", 0)));
        passRateCard.updateValue(stats.getOrDefault("passRate", 0.0) + "%");
        avgGpaCard.updateValue(String.valueOf(stats.getOrDefault("avgGPA", 0.0)));
    }

    public void refreshTheme() {
        buildUI();
    }
}

