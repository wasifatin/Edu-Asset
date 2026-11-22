package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.RoundedPanel;
import com.examsphere.model.ApplicationRequest;
import com.examsphere.model.AttendanceRecord;
import com.examsphere.model.Exam;
import com.examsphere.model.Result;
import com.examsphere.service.ApplicationService;
import com.examsphere.service.AttendanceService;
import com.examsphere.service.ExamService;
import com.examsphere.service.ResultService;
import com.examsphere.util.Constants;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Activity log and history panel aggregating comprehensive system audit trails,
 * examination events, marks alterations, and decision timelines.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class HistoryPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final ExamService examService = ExamService.getInstance();
    private final AttendanceService attendanceService = AttendanceService.getInstance();
    private final ResultService resultService = ResultService.getInstance();
    private final ApplicationService applicationService = ApplicationService.getInstance();

    private DataTablePanel historyTable;
    private JComboBox<String> categoryCombo;

    private static class HistoryItem {
        String timestamp;
        String category;
        String eventDescription;
        String initiator;

        HistoryItem(String timestamp, String category, String eventDescription, String initiator) {
            this.timestamp = timestamp;
            this.category = category;
            this.eventDescription = eventDescription;
            this.initiator = initiator;
        }
    }

    public HistoryPanel(MainFrame mainFrame) {
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

        JLabel title = new JLabel("Audit Log, Administrative Operations & Examination History");
        title.setFont(theme.getBoldFont(18));
        title.setForeground(theme.getPrimaryText());
        headerCard.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        categoryCombo = new JComboBox<>(new String[]{"All Categories", "EXAMINATIONS", "ATTENDANCE", "RESULTS", "APPLICATIONS"});
        categoryCombo.setFont(theme.getFont(13));
        categoryCombo.addActionListener(e -> applyFilter());
        actions.add(new JLabel("Category:"));
        actions.add(categoryCombo);

        CustomButton refreshBtn = new CustomButton("⟳ Refresh Log", theme.getSecondarySurface(), theme.getPrimaryText());
        refreshBtn.addActionListener(e -> applyFilter());
        actions.add(refreshBtn);

        headerCard.add(actions, BorderLayout.EAST);
        add(headerCard, BorderLayout.NORTH);

        // Data Table
        String[] cols = {"Timestamp", "Category", "Event Description", "Initiator / Source"};
        historyTable = new DataTablePanel(cols, getHistoryData());
        historyTable.addSearchBar("Search activity audit trail...");

        RoundedPanel tableCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        tableCard.add(historyTable, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private Object[][] getHistoryData() {
        List<HistoryItem> items = compileHistoryItems();
        String cat = (String) categoryCombo.getSelectedItem();
        if (cat != null && !cat.startsWith("All")) {
            items = items.stream().filter(i -> cat.equalsIgnoreCase(i.category)).collect(Collectors.toList());
        }

        Object[][] data = new Object[items.size()][4];
        for (int i = 0; i < items.size(); i++) {
            HistoryItem it = items.get(i);
            data[i][0] = it.timestamp;
            data[i][1] = it.category;
            data[i][2] = it.eventDescription;
            data[i][3] = it.initiator;
        }
        return data;
    }

    private List<HistoryItem> compileHistoryItems() {
        List<HistoryItem> list = new ArrayList<>();

        // 1. Examination schedules
        for (Exam e : examService.getAllExams()) {
            list.add(new HistoryItem(e.getExamDate() + " " + e.getStartTime(), "EXAMINATIONS",
                    "Examination session scheduled: " + e.getExamId() + " (" + e.getCourseCode() + " " + e.getExamType() + ") in Room " + e.getRoomId(),
                    "Coordinator / Admin"));
        }

        // 2. Attendance records
        for (AttendanceRecord ar : attendanceService.getAllRecords()) {
            list.add(new HistoryItem(ar.getTimestamp() != null ? ar.getTimestamp() : "2026-11-12", "ATTENDANCE",
                    "Attendance marked as " + ar.getStatus() + " for student " + ar.getStudentId() + " (Exam: " + ar.getExamId() + ")",
                    "Invigilator"));
        }

        // 3. Results publications
        for (Result r : resultService.getPublishedResults()) {
            list.add(new HistoryItem(r.getPublishDate() != null ? r.getPublishDate() : "2026-11-10", "RESULTS",
                    "Official semester result published for student " + r.getStudentId() + " (Term GPA: " + r.getGpa() + ")",
                    r.getPublishedBy() != null ? r.getPublishedBy() : "Examination Controller"));
        }

        // 4. Applications
        for (ApplicationRequest a : applicationService.getAllApplications()) {
            list.add(new HistoryItem(a.getLastUpdated() != null ? a.getLastUpdated() : a.getSubmissionDate(), "APPLICATIONS",
                    "Application " + a.getApplicationId() + " (" + a.getApplicationType() + ") transitioned to " + a.getStatus(),
                    "Admin / Student"));
        }

        // Sort descending by timestamp
        list.sort((a, b) -> b.timestamp.compareTo(a.timestamp));
        return list;
    }

    private void applyFilter() {
        historyTable.setData(getHistoryData());
    }

    public void refreshTheme() {
        buildUI();
    }
}

