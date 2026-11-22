package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.FormDialog;
import com.examsphere.components.RoundedPanel;
import com.examsphere.model.Course;
import com.examsphere.model.Department;
import com.examsphere.service.CourseService;
import com.examsphere.util.Constants;
import com.examsphere.util.ThemeManager;
import com.examsphere.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Course and department management panel offering tabs for course curriculums,
 * credit assignments, instructor allocations, and department program structures.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class CoursePanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final CourseService courseService = CourseService.getInstance();

    private DataTablePanel courseTablePanel;
    private DataTablePanel deptTablePanel;
    private JComboBox<String> deptFilterCombo;

    public CoursePanel(MainFrame mainFrame) {
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

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.setFont(theme.getBoldFont(14));
        tabbedPane.setBackground(theme.getMainSurface());
        tabbedPane.setForeground(theme.getPrimaryText());

        tabbedPane.addTab("Academic Courses", createCoursesTab());
        tabbedPane.addTab("Academic Departments", createDepartmentsTab());

        add(tabbedPane, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    // ─── TAB 1: Academic Courses ────────────────────────────────────────

    private JPanel createCoursesTab() {
        JPanel panel = new JPanel(new BorderLayout(Constants.SPACING, Constants.SPACING));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        // Top bar
        RoundedPanel topBar = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        topBar.setLayout(new BorderLayout(12, 12));
        topBar.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("Curriculum Courses & Instructor Assignments");
        title.setFont(theme.getBoldFont(16));
        title.setForeground(theme.getPrimaryText());
        topBar.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        deptFilterCombo = new JComboBox<>(new String[]{"All Departments", "CSE", "SWE", "EEE", "BBA"});
        deptFilterCombo.setFont(theme.getFont(13));
        deptFilterCombo.addActionListener(e -> applyCourseFilter());
        actions.add(new JLabel("Department:"));
        actions.add(deptFilterCombo);

        CustomButton addCourseBtn = new CustomButton("+ Add Course", theme.getPrimaryAccent(), Color.WHITE);
        addCourseBtn.addActionListener(e -> openAddCourseDialog());
        actions.add(addCourseBtn);

        CustomButton assignTeacherBtn = new CustomButton("Assign Teacher", theme.getSecondarySurface(), theme.getPrimaryText());
        assignTeacherBtn.addActionListener(e -> openAssignTeacherDialog());
        actions.add(assignTeacherBtn);

        CustomButton deleteCourseBtn = new CustomButton("Delete", theme.getDanger(), Color.WHITE);
        deleteCourseBtn.addActionListener(e -> deleteSelectedCourse());
        actions.add(deleteCourseBtn);

        topBar.add(actions, BorderLayout.EAST);
        panel.add(topBar, BorderLayout.NORTH);

        // Table
        String[] cols = {"Course Code", "Course Title", "Department", "Credits", "Semester", "Assigned Teacher", "Enrolled Students"};
        courseTablePanel = new DataTablePanel(cols, getCourseData(courseService.getAllCourses()));
        courseTablePanel.addSearchBar("Search courses by code or title...");

        RoundedPanel tableCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        tableCard.add(courseTablePanel, BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private Object[][] getCourseData(List<Course> list) {
        Object[][] data = new Object[list.size()][7];
        for (int i = 0; i < list.size(); i++) {
            Course c = list.get(i);
            data[i][0] = c.getCourseCode();
            data[i][1] = c.getCourseName();
            data[i][2] = c.getDepartmentId();
            data[i][3] = c.getCredits() + " Credits";
            data[i][4] = "Semester " + c.getSemester();
            data[i][5] = c.getTeacherId() != null ? c.getTeacherId() : "Unassigned";
            data[i][6] = c.getEnrolledStudentIds().size() + " Students";
        }
        return data;
    }

    private void applyCourseFilter() {
        String dept = (String) deptFilterCombo.getSelectedItem();
        List<Course> list = courseService.getAllCourses();
        if (dept != null && !dept.startsWith("All")) {
            list = courseService.getCoursesByDepartment(dept);
        }
        courseTablePanel.setData(getCourseData(list));
    }

    private void openAddCourseDialog() {
        FormDialog dialog = new FormDialog(mainFrame, "Create Academic Course", 480, 420);

        JTextField codeField = new JTextField();
        JTextField titleField = new JTextField();
        JComboBox<String> deptCombo = new JComboBox<>(new String[]{"CSE", "SWE", "EEE", "BBA", "ENG"});
        JComboBox<Double> creditsCombo = new JComboBox<>(new Double[]{0.75, 1.0, 1.5, 2.0, 3.0, 4.0});
        creditsCombo.setEditable(true);
        creditsCombo.setSelectedItem(3.0);
        JComboBox<Integer> semCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8});
        JTextField teacherIdField = new JTextField("TCH001");

        dialog.addFormField("Course Code *:", codeField);
        dialog.addFormField("Course Title *:", titleField);
        dialog.addFormField("Department *:", deptCombo);
        dialog.addFormField("Credit Hours * (e.g. 1.5):", creditsCombo);
        dialog.addFormField("Target Semester *:", semCombo);
        dialog.addFormField("Teacher User ID:", teacherIdField);

        CustomButton saveBtn = new CustomButton("Create Course", theme.getPrimaryAccent(), Color.WHITE);
        saveBtn.addActionListener(e -> {
            String code = codeField.getText().trim().toUpperCase();
            String name = titleField.getText().trim();
            String dept = (String) deptCombo.getSelectedItem();
            double creds;
            try {
                creds = Double.parseDouble(creditsCombo.getSelectedItem().toString().trim());
            } catch (NumberFormatException ex) {
                creds = 3.0;
            }
            int sem = (Integer) semCombo.getSelectedItem();
            String teacher = teacherIdField.getText().trim();

            if (!ValidationUtil.isValidId(code)) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid alphanumeric Course Code (e.g. CSE221).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Course title cannot be blank.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Course course = new Course(code, name, dept, creds, sem);
            if (!teacher.isEmpty()) {
                course.setTeacherId(teacher);
            }
            courseService.addCourse(course);
            dialog.dispose();
            applyCourseFilter();
            JOptionPane.showMessageDialog(mainFrame, "Course created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        CustomButton cancelBtn = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.getButtonPanel().add(cancelBtn);
        dialog.getButtonPanel().add(saveBtn);
        dialog.setVisible(true);
    }

    private void openAssignTeacherDialog() {
        int row = courseTablePanel.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course to assign an instructor.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = (String) courseTablePanel.getTable().getValueAt(row, 0);
        String currentTeacher = (String) courseTablePanel.getTable().getValueAt(row, 5);

        String teacherId = JOptionPane.showInputDialog(mainFrame,
                "Enter Teacher User ID to assign to " + code + ":",
                currentTeacher.equals("Unassigned") ? "TCH001" : currentTeacher);

        if (teacherId != null && !teacherId.trim().isEmpty()) {
            courseService.assignTeacher(code, teacherId.trim());
            applyCourseFilter();
            JOptionPane.showMessageDialog(mainFrame, "Instructor assignment updated for " + code, "Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void deleteSelectedCourse() {
        int row = courseTablePanel.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a course to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String code = (String) courseTablePanel.getTable().getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to delete course " + code + "?",
                "Confirm Delete", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            courseService.deleteCourse(code);
            applyCourseFilter();
            JOptionPane.showMessageDialog(mainFrame, "Course deleted successfully.", "Removed", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    // ─── TAB 2: Academic Departments ────────────────────────────────────

    private JPanel createDepartmentsTab() {
        JPanel panel = new JPanel(new BorderLayout(Constants.SPACING, Constants.SPACING));
        panel.setOpaque(false);
        panel.setBorder(new EmptyBorder(12, 0, 0, 0));

        RoundedPanel topBar = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        topBar.setLayout(new BorderLayout(12, 12));
        topBar.setBorder(new EmptyBorder(10, 16, 10, 16));

        JLabel title = new JLabel("University Academic Departments & Degree Programs");
        title.setFont(theme.getBoldFont(16));
        title.setForeground(theme.getPrimaryText());
        topBar.add(title, BorderLayout.WEST);

        CustomButton addDeptBtn = new CustomButton("+ Add Department", theme.getPrimaryAccent(), Color.WHITE);
        addDeptBtn.addActionListener(e -> openAddDepartmentDialog());
        topBar.add(addDeptBtn, BorderLayout.EAST);

        panel.add(topBar, BorderLayout.NORTH);

        String[] cols = {"Department ID", "Department Name", "Head of Department", "Offered Programs", "Active Courses"};
        deptTablePanel = new DataTablePanel(cols, getDeptData());

        RoundedPanel tableCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        tableCard.add(deptTablePanel, BorderLayout.CENTER);

        panel.add(tableCard, BorderLayout.CENTER);
        return panel;
    }

    private Object[][] getDeptData() {
        List<Department> list = courseService.getAllDepartments();
        Object[][] data = new Object[list.size()][5];
        for (int i = 0; i < list.size(); i++) {
            Department d = list.get(i);
            data[i][0] = d.getDepartmentId();
            data[i][1] = d.getDepartmentName();
            data[i][2] = d.getHeadOfDepartment() != null ? d.getHeadOfDepartment() : "N/A";
            data[i][3] = String.join(", ", d.getPrograms());
            data[i][4] = d.getCourseIds().size() + " Courses";
        }
        return data;
    }

    private void openAddDepartmentDialog() {
        FormDialog dialog = new FormDialog(mainFrame, "Add Academic Department", 460, 360);

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField headField = new JTextField();
        JTextField programField = new JTextField();

        dialog.addFormField("Department Code (e.g. CSE) *:", idField);
        dialog.addFormField("Department Name *:", nameField);
        dialog.addFormField("Head of Department:", headField);
        dialog.addFormField("Initial Program (e.g. B.Sc. in CSE):", programField);

        CustomButton saveBtn = new CustomButton("Save Department", theme.getPrimaryAccent(), Color.WHITE);
        saveBtn.addActionListener(e -> {
            String id = idField.getText().trim().toUpperCase();
            String name = nameField.getText().trim();
            String head = headField.getText().trim();
            String prog = programField.getText().trim();

            if (!ValidationUtil.isValidId(id)) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid Department ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Department name cannot be blank.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            Department dept = new Department(id, name);
            if (!head.isEmpty()) dept.setHeadOfDepartment(head);
            if (!prog.isEmpty()) dept.addProgram(prog);

            courseService.addDepartment(dept);
            dialog.dispose();
            deptTablePanel.setData(getDeptData());
            JOptionPane.showMessageDialog(mainFrame, "Department created successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
        });

        CustomButton cancelBtn = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.getButtonPanel().add(cancelBtn);
        dialog.getButtonPanel().add(saveBtn);
        dialog.setVisible(true);
    }

    public void refreshTheme() {
        buildUI();
    }
}

