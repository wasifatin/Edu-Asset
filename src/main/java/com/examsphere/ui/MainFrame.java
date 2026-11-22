package com.examsphere.ui;

import com.examsphere.model.User;
import com.examsphere.util.ThemeManager;
import com.examsphere.util.Constants;

import javax.swing.*;
import java.awt.*;

/**
 * Main application window — hosts Sidebar, TopBar, and content CardLayout.
 */
public class MainFrame extends JFrame {

    private SidebarPanel sidebar;
    private TopBarPanel topBar;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    private final User currentUser;
    private final ThemeManager theme = ThemeManager.getInstance();

    // Panel references for theme refresh
    private DashboardPanel dashboardPanel;
    private StudentPanel studentPanel;
    private CoursePanel coursePanel;
    private ExamPanel examPanel;
    private RoomPanel roomPanel;
    private AttendancePanel attendancePanel;
    private InvigilatorPanel invigilatorPanel;
    private MarksPanel marksPanel;
    private ResultsPanel resultsPanel;
    private ApplicationsPanel applicationsPanel;
    private ReportsPanel reportsPanel;
    private HistoryPanel historyPanel;
    private UserManagementPanel userManagementPanel;
    private SettingsPanel settingsPanel;

    public MainFrame(User currentUser) {
        this.currentUser = currentUser;
        setTitle("EduAsset — " + currentUser.getDisplayRole());
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(Constants.MIN_WIDTH, Constants.MIN_HEIGHT));
        setLocationRelativeTo(null);
        setLayout(new BorderLayout());
        getContentPane().setBackground(theme.getBackground());

        initComponents();

        theme.addThemeChangeListener(this::refreshTheme);
        setVisible(true);
    }

    private void initComponents() {
        // Top bar
        topBar = new TopBarPanel(this);
        add(topBar, BorderLayout.NORTH);

        // Sidebar
        sidebar = new SidebarPanel(this, currentUser);
        add(sidebar, BorderLayout.WEST);

        // Content area with CardLayout
        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(theme.getBackground());

        // Create and register all panels
        dashboardPanel = new DashboardPanel(this);
        studentPanel = new StudentPanel(this);
        coursePanel = new CoursePanel(this);
        examPanel = new ExamPanel(this);
        roomPanel = new RoomPanel(this);
        attendancePanel = new AttendancePanel(this);
        invigilatorPanel = new InvigilatorPanel(this);
        marksPanel = new MarksPanel(this);
        resultsPanel = new ResultsPanel(this);
        applicationsPanel = new ApplicationsPanel(this);
        reportsPanel = new ReportsPanel(this);
        historyPanel = new HistoryPanel(this);
        userManagementPanel = new UserManagementPanel(this);
        settingsPanel = new SettingsPanel(this);

        contentPanel.add(dashboardPanel, "overview");
        contentPanel.add(studentPanel, "students");
        contentPanel.add(coursePanel, "departments");
        contentPanel.add(examPanel, "examScheduling");
        contentPanel.add(roomPanel, "rooms");
        contentPanel.add(attendancePanel, "attendance");
        contentPanel.add(invigilatorPanel, "invigilators");
        contentPanel.add(marksPanel, "marks");
        contentPanel.add(resultsPanel, "results");
        contentPanel.add(applicationsPanel, "applications");
        contentPanel.add(reportsPanel, "reports");
        contentPanel.add(historyPanel, "history");
        contentPanel.add(userManagementPanel, "users");
        contentPanel.add(settingsPanel, "settings");

        add(contentPanel, BorderLayout.CENTER);

        // Default panel
        showPanel("overview");
    }

    /**
     * Switch the visible content panel and update sidebar + topbar.
     */
    public void showPanel(String panelName) {
        cardLayout.show(contentPanel, panelName);
        sidebar.setActiveItem(panelName);
        topBar.updateTitle(getPanelTitle(panelName));
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public TopBarPanel getTopBar() {
        return topBar;
    }

    public SidebarPanel getSidebar() {
        return sidebar;
    }

    /**
     * Refreshes all component colors from ThemeManager after a theme toggle.
     */
    public void refreshTheme() {
        SwingUtilities.invokeLater(() -> {
            getContentPane().setBackground(theme.getBackground());
            contentPanel.setBackground(theme.getBackground());
            repaint();
            revalidate();
        });
    }

    /**
     * Maps card names to human-readable page titles.
     */
    private String getPanelTitle(String panelName) {
        switch (panelName) {
            case "overview":        return "Dashboard Overview";
            case "students":        return "Student Records";
            case "departments":     return "Departments & Courses";
            case "examScheduling":  return "Exam Scheduling";
            case "rooms":           return "Rooms & Seating";
            case "attendance":      return "Attendance Management";
            case "invigilators":    return "Invigilator Management";
            case "marks":           return "Marks & Assessment";
            case "results":         return "Results & Transcripts";
            case "applications":    return "Applications & Rechecks";
            case "reports":         return "Reports & Analytics";
            case "history":         return "Activity History";
            case "users":           return "User & Role Management";
            case "settings":        return "System Settings";
            default:                return "EduAsset";
        }
    }
}
