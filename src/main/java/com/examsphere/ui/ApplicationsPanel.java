package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.FormDialog;
import com.examsphere.components.RoundedPanel;
import com.examsphere.exception.AuthorizationException;
import com.examsphere.exception.InvalidApplicationException;
import com.examsphere.model.ApplicationRequest;
import com.examsphere.model.Course;
import com.examsphere.model.User;
import com.examsphere.service.ApplicationService;
import com.examsphere.service.AuthService;
import com.examsphere.service.CourseService;
import com.examsphere.util.Constants;
import com.examsphere.util.IdGenerator;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Applications and grievances panel facilitating student recheck submissions,
 * retake requests, and administrative status decisions with feedback remarks.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class ApplicationsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final ApplicationService applicationService = ApplicationService.getInstance();
    private final AuthService authService = AuthService.getInstance();
    private final CourseService courseService = CourseService.getInstance();

    private DataTablePanel appsTable;
    private JComboBox<String> statusFilterCombo;

    public ApplicationsPanel(MainFrame mainFrame) {
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

        JLabel title = new JLabel("Student Applications, Rechecks & Grievances");
        title.setFont(theme.getBoldFont(18));
        title.setForeground(theme.getPrimaryText());
        headerCard.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        actions.setOpaque(false);

        statusFilterCombo = new JComboBox<>(new String[]{"All Statuses", "PENDING", "UNDER_REVIEW", "APPROVED", "REJECTED", "COMPLETED"});
        statusFilterCombo.setFont(theme.getFont(13));
        statusFilterCombo.addActionListener(e -> applyFilter());
        actions.add(new JLabel("Status:"));
        actions.add(statusFilterCombo);

        CustomButton newAppBtn = new CustomButton("+ Submit Application", theme.getPrimaryAccent(), Color.WHITE);
        newAppBtn.addActionListener(e -> openSubmitApplicationDialog());
        actions.add(newAppBtn);

        CustomButton reviewBtn = new CustomButton("Review / Decision", theme.getSuccess(), Color.WHITE);
        reviewBtn.addActionListener(e -> openReviewDialog());
        actions.add(reviewBtn);

        headerCard.add(actions, BorderLayout.EAST);
        add(headerCard, BorderLayout.NORTH);

        // Data Table
        String[] cols = {"Application ID", "Student ID", "Application Category", "Related Course", "Status", "Date Submitted", "Admin Remarks"};
        appsTable = new DataTablePanel(cols, getAppsData(applicationService.getAllApplications()));
        appsTable.addSearchBar("Filter student applications...");

        RoundedPanel tableCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        tableCard.add(appsTable, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private Object[][] getAppsData(List<ApplicationRequest> list) {
        Object[][] data = new Object[list.size()][7];
        for (int i = 0; i < list.size(); i++) {
            ApplicationRequest a = list.get(i);
            data[i][0] = a.getApplicationId();
            data[i][1] = a.getStudentId();
            data[i][2] = a.getApplicationType();
            data[i][3] = a.getCourseCode() != null ? a.getCourseCode() : "N/A";
            data[i][4] = a.getStatus();
            data[i][5] = a.getSubmissionDate();
            data[i][6] = a.getAdminComments() != null ? a.getAdminComments() : "None";
        }
        return data;
    }

    private void applyFilter() {
        String status = (String) statusFilterCombo.getSelectedItem();
        List<ApplicationRequest> list = applicationService.getAllApplications();
        if (status != null && !status.startsWith("All")) {
            list = applicationService.getApplicationsByStatus(status);
        }
        appsTable.setData(getAppsData(list));
    }

    private void openSubmitApplicationDialog() {
        FormDialog dialog = new FormDialog(mainFrame, "Submit Academic Grievance or Recheck", 500, 420);

        User current = authService.getCurrentUser();
        String defaultStudentId = current != null ? current.getUserId() : "STU001";
        JTextField studentIdField = new JTextField(defaultStudentId);

        JComboBox<String> typeCombo = new JComboBox<>(new String[]{"RECHECK", "RETAKE", "MISSING_MARKS", "COMPLAINT"});

        List<Course> courses = courseService.getAllCourses();
        String[] courseOpts = courses.stream().map(Course::getCourseCode).toArray(String[]::new);
        JComboBox<String> courseCombo = new JComboBox<>(courseOpts);

        JTextArea descArea = new JTextArea(4, 25);
        descArea.setLineWrap(true);
        descArea.setWrapStyleWord(true);
        JScrollPane descScroll = new JScrollPane(descArea);

        dialog.addFormField("Student ID *:", studentIdField);
        dialog.addFormField("Application Type *:", typeCombo);
        dialog.addFormField("Related Course:", courseCombo);
        dialog.addFormField("Detailed Statement *:", descScroll);

        CustomButton submitBtn = new CustomButton("Submit Form", theme.getPrimaryAccent(), Color.WHITE);
        submitBtn.addActionListener(e -> {
            String sid = studentIdField.getText().trim();
            String type = (String) typeCombo.getSelectedItem();
            String course = (String) courseCombo.getSelectedItem();
            String desc = descArea.getText().trim();

            if (sid.isEmpty() || desc.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Please fill in all mandatory fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            ApplicationRequest app = new ApplicationRequest(IdGenerator.generateId("APP"), sid, type, course, desc);
            try {
                applicationService.submitApplication(app);
                dialog.dispose();
                applyFilter();
                JOptionPane.showMessageDialog(mainFrame, "Application registered with ID: " + app.getApplicationId(), "Application Lodged", JOptionPane.INFORMATION_MESSAGE);
            } catch (InvalidApplicationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Submission Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        CustomButton cancelAction = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
        cancelAction.addActionListener(e -> dialog.dispose());

        dialog.getButtonPanel().add(cancelAction);
        dialog.getButtonPanel().add(submitBtn);
        dialog.setVisible(true);
    }

    private void openReviewDialog() {
        int row = appsTable.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select an application to review.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String appId = (String) appsTable.getTable().getValueAt(row, 0);
        ApplicationRequest app = applicationService.getApplicationById(appId);
        if (app == null) return;

        FormDialog dialog = new FormDialog(mainFrame, "Administrative Decision — " + app.getApplicationId(), 520, 440);

        JLabel info = new JLabel("Student: " + app.getStudentId() + " | Type: " + app.getApplicationType() + " | Course: " + app.getCourseCode());
        info.setFont(theme.getBoldFont(13));

        JTextArea descPreview = new JTextArea(app.getDescription(), 3, 25);
        descPreview.setEditable(false);
        descPreview.setLineWrap(true);

        JComboBox<String> decisionCombo = new JComboBox<>(new String[]{"UNDER_REVIEW", "APPROVED", "REJECTED", "COMPLETED"});
        decisionCombo.setSelectedItem(app.getStatus());

        JTextArea commentsArea = new JTextArea(app.getAdminComments() != null ? app.getAdminComments() : "", 3, 25);
        commentsArea.setLineWrap(true);

        dialog.addFormField("Application Info:", info);
        dialog.addFormField("Student Statement:", new JScrollPane(descPreview));
        dialog.addFormField("Administrative Decision *:", decisionCombo);
        dialog.addFormField("Controller Remarks:", new JScrollPane(commentsArea));

        CustomButton saveDecisionBtn = new CustomButton("Save Decision", theme.getPrimaryAccent(), Color.WHITE);
        saveDecisionBtn.addActionListener(e -> {
            String newStatus = (String) decisionCombo.getSelectedItem();
            String comments = commentsArea.getText().trim();

            try {
                applicationService.updateApplicationStatus(appId, newStatus, comments);
                dialog.dispose();
                applyFilter();
                JOptionPane.showMessageDialog(mainFrame, "Application status successfully updated to " + newStatus, "Decision Recorded", JOptionPane.INFORMATION_MESSAGE);
            } catch (AuthorizationException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Authorization Blocked", JOptionPane.ERROR_MESSAGE);
            }
        });

        CustomButton cancelAction = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
        cancelAction.addActionListener(e -> dialog.dispose());

        dialog.getButtonPanel().add(cancelAction);
        dialog.getButtonPanel().add(saveDecisionBtn);
        dialog.setVisible(true);
    }

    public void refreshTheme() {
        buildUI();
    }
}

