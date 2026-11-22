package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.RoundedPanel;
import com.examsphere.model.Exam;
import com.examsphere.model.Teacher;
import com.examsphere.service.AuthService;
import com.examsphere.service.ExamService;
import com.examsphere.util.Constants;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Invigilator management panel displaying faculty duty schedules, room assignments,
 * and exam supervision rosters.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class InvigilatorPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final AuthService authService = AuthService.getInstance();
    private final ExamService examService = ExamService.getInstance();

    private DataTablePanel invigilatorTable;

    public InvigilatorPanel(MainFrame mainFrame) {
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

        JLabel title = new JLabel("Faculty Invigilation Roster & Duty Allocations");
        title.setFont(theme.getBoldFont(18));
        title.setForeground(theme.getPrimaryText());
        headerCard.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        CustomButton reassignBtn = new CustomButton("Reassign Duty", theme.getPrimaryAccent(), Color.WHITE);
        reassignBtn.addActionListener(e -> reassignSelected());
        actions.add(reassignBtn);

        headerCard.add(actions, BorderLayout.EAST);
        add(headerCard, BorderLayout.NORTH);

        // Data Table
        String[] cols = {"Faculty ID", "Full Name", "Department", "Designation", "Assigned Exam Duty", "Exam Room", "Duty Date & Time"};
        invigilatorTable = new DataTablePanel(cols, getInvigilatorData());
        invigilatorTable.addSearchBar("Search faculty duty roster...");

        RoundedPanel tableCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        tableCard.add(invigilatorTable, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private Object[][] getInvigilatorData() {
        List<Teacher> teachers = authService.getAllUsers().stream()
                .filter(u -> u instanceof Teacher)
                .map(u -> (Teacher) u)
                .collect(Collectors.toList());

        List<Exam> exams = examService.getAllExams();

        Object[][] data = new Object[teachers.size()][7];
        for (int i = 0; i < teachers.size(); i++) {
            Teacher t = teachers.get(i);
            Exam assigned = null;
            for (Exam e : exams) {
                if (t.getTeacherId().equalsIgnoreCase(e.getInvigilatorId())) {
                    assigned = e;
                    break;
                }
            }

            data[i][0] = t.getTeacherId();
            data[i][1] = t.getFullName();
            data[i][2] = t.getDepartment();
            data[i][3] = t.getDesignation();
            data[i][4] = assigned != null ? assigned.getExamId() + " (" + assigned.getCourseCode() + ")" : "Available";
            data[i][5] = assigned != null && assigned.getRoomId() != null ? assigned.getRoomId() : "—";
            data[i][6] = assigned != null ? assigned.getExamDate() + " @ " + assigned.getStartTime() : "—";
        }
        return data;
    }

    private void reassignSelected() {
        int row = invigilatorTable.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a faculty member from the roster.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String teacherId = (String) invigilatorTable.getTable().getValueAt(row, 0);
        List<Exam> exams = examService.getAllExams();
        String[] examOpts = exams.stream().map(Exam::getExamId).toArray(String[]::new);

        String chosenExam = (String) JOptionPane.showInputDialog(mainFrame,
                "Select Examination slot to assign to " + teacherId + ":",
                "Assign Invigilation Duty", JOptionPane.QUESTION_MESSAGE, null, examOpts, examOpts.length > 0 ? examOpts[0] : null);

        if (chosenExam != null) {
            Exam exam = examService.getExamById(chosenExam);
            if (exam != null) {
                exam.setInvigilatorId(teacherId);
                try {
                    examService.updateExam(exam);
                    invigilatorTable.setData(getInvigilatorData());
                    JOptionPane.showMessageDialog(mainFrame, "Invigilator duty successfully assigned!", "Duty Confirmed", JOptionPane.INFORMATION_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(mainFrame, "Assignment Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    public void refreshTheme() {
        buildUI();
    }
}

