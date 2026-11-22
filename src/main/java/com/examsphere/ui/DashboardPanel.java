package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.RoundedPanel;
import com.examsphere.components.StatCard;
import com.examsphere.model.ApplicationRequest;
import com.examsphere.model.Exam;
import com.examsphere.service.ApplicationService;
import com.examsphere.service.ExamService;
import com.examsphere.service.ReportService;
import com.examsphere.util.Constants;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Overview dashboard displaying high-level institutional KPI statistics cards,
 * upcoming examination schedules, and pending student applications.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class DashboardPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final ReportService reportService = ReportService.getInstance();
    private final ExamService examService = ExamService.getInstance();
    private final ApplicationService applicationService = ApplicationService.getInstance();

    private StatCard totalStudentsCard;
    private StatCard totalExamsCard;
    private StatCard scheduledExamsCard;
    private StatCard publishedResultsCard;
    private StatCard pendingResultsCard;
    private StatCard pendingApplicationsCard;

    private DataTablePanel recentExamsTable;
    private DataTablePanel pendingAppsTable;

    public DashboardPanel(MainFrame mainFrame) {
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

        // Header Panel with title and Quick Action
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setOpaque(false);

        JLabel welcomeLabel = new JLabel("Institutional Examination Overview");
        welcomeLabel.setFont(theme.getBoldFont(22));
        welcomeLabel.setForeground(theme.getPrimaryText());
        headerPanel.add(welcomeLabel, BorderLayout.WEST);

        JPanel actionGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actionGroup.setOpaque(false);

        CustomButton refreshBtn = new CustomButton("⟳ Refresh Data", theme.getMainSurface(), theme.getPrimaryText());
        refreshBtn.addActionListener(e -> refreshData());
        actionGroup.add(refreshBtn);

        CustomButton scheduleQuickBtn = new CustomButton("+ Schedule Exam", theme.getPrimaryAccent(), Color.WHITE);
        scheduleQuickBtn.addActionListener(e -> mainFrame.showPanel("examScheduling"));
        actionGroup.add(scheduleQuickBtn);

        headerPanel.add(actionGroup, BorderLayout.EAST);

        // Center Container: KPI cards + 2-Column Tables
        JPanel mainContent = new JPanel();
        mainContent.setLayout(new BoxLayout(mainContent, BoxLayout.Y_AXIS));
        mainContent.setOpaque(false);

        mainContent.add(headerPanel);
        mainContent.add(Box.createVerticalStrut(Constants.SPACING));

        // 1. KPI Cards Grid (2 rows x 3 cols or 1 row x 6 cols)
        JPanel kpiGrid = new JPanel(new GridLayout(2, 3, Constants.SPACING, Constants.SPACING));
        kpiGrid.setOpaque(false);
        kpiGrid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 220));

        totalStudentsCard = new StatCard("TOTAL STUDENTS", String.valueOf(reportService.getTotalStudents()),
                "Enrolled across departments", theme.getPrimaryAccent());
        totalExamsCard = new StatCard("TOTAL EXAMINATIONS", String.valueOf(reportService.getTotalExams()),
                "Midterm and final sessions", theme.getInfo());
        scheduledExamsCard = new StatCard("SCHEDULED EXAMS", String.valueOf(reportService.getScheduledExams()),
                "Upcoming confirmed slots", theme.getSuccess());
        publishedResultsCard = new StatCard("PUBLISHED RESULTS", String.valueOf(reportService.getPublishedResults()),
                "Official student transcripts", theme.getSuccess());
        pendingResultsCard = new StatCard("PENDING RESULTS", String.valueOf(reportService.getPendingResults()),
                "Awaiting controller approval", theme.getWarning());
        pendingApplicationsCard = new StatCard("PENDING GRIEVANCES", String.valueOf(reportService.getPendingApplications()),
                "Rechecks & retake requests", theme.getDanger());

        kpiGrid.add(totalStudentsCard);
        kpiGrid.add(totalExamsCard);
        kpiGrid.add(scheduledExamsCard);
        kpiGrid.add(publishedResultsCard);
        kpiGrid.add(pendingResultsCard);
        kpiGrid.add(pendingApplicationsCard);

        mainContent.add(kpiGrid);
        mainContent.add(Box.createVerticalStrut(Constants.SPACING_LG));

        // 2. Split Tables: Recent Exams & Pending Applications
        JPanel splitPanel = new JPanel(new GridLayout(1, 2, Constants.SPACING, 0));
        splitPanel.setOpaque(false);

        // Left Card: Recent Exams
        RoundedPanel examsCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        examsCard.setLayout(new BorderLayout(8, 8));
        examsCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));

        JLabel examsTitle = new JLabel("Upcoming Examination Timetable");
        examsTitle.setFont(theme.getBoldFont(16));
        examsTitle.setForeground(theme.getPrimaryText());
        examsCard.add(examsTitle, BorderLayout.NORTH);

        String[] examCols = {"Exam ID", "Course", "Date", "Time", "Room", "Status"};
        recentExamsTable = new DataTablePanel(examCols, getExamsData());
        examsCard.add(recentExamsTable, BorderLayout.CENTER);

        // Right Card: Pending Applications
        RoundedPanel appsCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        appsCard.setLayout(new BorderLayout(8, 8));
        appsCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));

        JLabel appsTitle = new JLabel("Active Student Applications & Requests");
        appsTitle.setFont(theme.getBoldFont(16));
        appsTitle.setForeground(theme.getPrimaryText());
        appsCard.add(appsTitle, BorderLayout.NORTH);

        String[] appCols = {"App ID", "Student", "Type", "Course", "Status"};
        pendingAppsTable = new DataTablePanel(appCols, getAppsData());
        appsCard.add(pendingAppsTable, BorderLayout.CENTER);

        splitPanel.add(examsCard);
        splitPanel.add(appsCard);

        mainContent.add(splitPanel);

        JScrollPane scrollPane = new JScrollPane(mainContent);
        scrollPane.setBorder(null);
        scrollPane.setOpaque(false);
        scrollPane.getViewport().setOpaque(false);
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        add(scrollPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private Object[][] getExamsData() {
        List<Exam> list = examService.getAllExams();
        Object[][] data = new Object[list.size()][6];
        for (int i = 0; i < list.size(); i++) {
            Exam e = list.get(i);
            data[i][0] = e.getExamId();
            data[i][1] = e.getCourseCode();
            data[i][2] = e.getExamDate();
            data[i][3] = e.getStartTime() + " - " + e.getEndTime();
            data[i][4] = e.getRoomId() != null ? e.getRoomId() : "Unassigned";
            data[i][5] = e.getStatus();
        }
        return data;
    }

    private Object[][] getAppsData() {
        List<ApplicationRequest> list = applicationService.getAllApplications();
        Object[][] data = new Object[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            ApplicationRequest a = list.get(i);
            data[i][0] = a.getApplicationId();
            data[i][1] = a.getStudentId();
            data[i][2] = a.getApplicationType();
            data[i][3] = a.getCourseCode() != null ? a.getCourseCode() : "N/A";
            data[i][4] = a.getStatus();
        }
        return data;
    }

    public void refreshData() {
        totalStudentsCard.updateValue(String.valueOf(reportService.getTotalStudents()));
        totalExamsCard.updateValue(String.valueOf(reportService.getTotalExams()));
        scheduledExamsCard.updateValue(String.valueOf(reportService.getScheduledExams()));
        publishedResultsCard.updateValue(String.valueOf(reportService.getPublishedResults()));
        pendingResultsCard.updateValue(String.valueOf(reportService.getPendingResults()));
        pendingApplicationsCard.updateValue(String.valueOf(reportService.getPendingApplications()));

        recentExamsTable.setData(getExamsData());
        pendingAppsTable.setData(getAppsData());
    }

    public void refreshTheme() {
        buildUI();
    }
}

