package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.FormDialog;
import com.examsphere.components.RoundedPanel;
import com.examsphere.exception.DuplicateStudentIdException;
import com.examsphere.model.Student;
import com.examsphere.service.StudentService;
import com.examsphere.util.Constants;
import com.examsphere.util.PasswordUtil;
import com.examsphere.util.ThemeManager;
import com.examsphere.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Student management panel supporting comprehensive CRUD operations, departmental
 * and semester filtering, keyword searching, and academic detail inspection.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class StudentPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final StudentService studentService = StudentService.getInstance();

    private DataTablePanel tablePanel;
    private JComboBox<String> deptFilterCombo;
    private JComboBox<String> semesterFilterCombo;
    private JTextField searchField;

    public StudentPanel(MainFrame mainFrame) {
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

        // 1. Top Controls Bar
        RoundedPanel topBarCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        topBarCard.setLayout(new BorderLayout(Constants.SPACING, Constants.SPACING));
        topBarCard.setBorder(new EmptyBorder(12, 16, 12, 16));

        // Title and count
        JLabel titleLabel = new JLabel("Student Directory & Academic Records");
        titleLabel.setFont(theme.getBoldFont(18));
        titleLabel.setForeground(theme.getPrimaryText());
        topBarCard.add(titleLabel, BorderLayout.WEST);

        // Action Buttons: Search, Filters, Add
        JPanel controlsGroup = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        controlsGroup.setOpaque(false);

        // Search input
        searchField = new JTextField(12);
        searchField.setFont(theme.getFont(13));
        searchField.setToolTipText("Search student by ID or Name");
        searchField.addActionListener(e -> applyFilters());
        controlsGroup.add(new JLabel("Search:"));
        controlsGroup.add(searchField);

        // Department filter
        deptFilterCombo = new JComboBox<>(new String[]{"All Departments", "CSE", "SWE", "EEE", "BBA", "ENG"});
        deptFilterCombo.setFont(theme.getFont(13));
        deptFilterCombo.addActionListener(e -> applyFilters());
        controlsGroup.add(deptFilterCombo);

        // Semester filter
        semesterFilterCombo = new JComboBox<>(new String[]{"All Semesters", "1", "2", "3", "4", "5", "6", "7", "8"});
        semesterFilterCombo.setFont(theme.getFont(13));
        semesterFilterCombo.addActionListener(e -> applyFilters());
        controlsGroup.add(semesterFilterCombo);

        // Action Buttons
        CustomButton addBtn = new CustomButton("+ Add Student", theme.getPrimaryAccent(), Color.WHITE);
        addBtn.addActionListener(e -> openAddStudentDialog());
        controlsGroup.add(addBtn);

        CustomButton editBtn = new CustomButton("Edit", theme.getSecondarySurface(), theme.getPrimaryText());
        editBtn.addActionListener(e -> openEditStudentDialog());
        controlsGroup.add(editBtn);

        CustomButton deleteBtn = new CustomButton("Delete", theme.getDanger(), Color.WHITE);
        deleteBtn.addActionListener(e -> deleteSelectedStudent());
        controlsGroup.add(deleteBtn);

        topBarCard.add(controlsGroup, BorderLayout.EAST);
        add(topBarCard, BorderLayout.NORTH);

        // 2. Data Table
        String[] columns = {"Student ID", "Full Name", "Email Address", "Department", "Program", "Semester", "Contact", "Status"};
        tablePanel = new DataTablePanel(columns, getTableData(studentService.getAllStudents()));
        tablePanel.addSearchBar("Filter records in table...");

        RoundedPanel tableContainer = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        tableContainer.setLayout(new BorderLayout());
        tableContainer.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        tableContainer.add(tablePanel, BorderLayout.CENTER);

        add(tableContainer, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private Object[][] getTableData(List<Student> students) {
        Object[][] data = new Object[students.size()][8];
        for (int i = 0; i < students.size(); i++) {
            Student s = students.get(i);
            data[i][0] = s.getStudentId();
            data[i][1] = s.getFullName();
            data[i][2] = s.getEmail();
            data[i][3] = s.getDepartment();
            data[i][4] = s.getProgram();
            data[i][5] = "Semester " + s.getSemester();
            data[i][6] = s.getContactNumber() != null ? s.getContactNumber() : "N/A";
            data[i][7] = s.isActive() ? "ACTIVE" : "INACTIVE";
        }
        return data;
    }

    private void applyFilters() {
        String query = searchField.getText().trim();
        String dept = (String) deptFilterCombo.getSelectedItem();
        String sem = (String) semesterFilterCombo.getSelectedItem();

        List<Student> list = studentService.getAllStudents();

        if (!query.isEmpty()) {
            list = studentService.searchStudents(query);
        }

        if (dept != null && !dept.startsWith("All")) {
            final String d = dept;
            list.removeIf(s -> !d.equalsIgnoreCase(s.getDepartment()));
        }

        if (sem != null && !sem.startsWith("All")) {
            int semVal = Integer.parseInt(sem);
            list.removeIf(s -> s.getSemester() != semVal);
        }

        tablePanel.setData(getTableData(list));
    }

    private void openAddStudentDialog() {
        FormDialog dialog = new FormDialog(mainFrame, "Add New Student Record", 500, 520);

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<String> deptCombo = new JComboBox<>(new String[]{"CSE", "SWE", "EEE", "BBA", "ENG"});
        JTextField programField = new JTextField("B.Sc. in CSE");
        JComboBox<Integer> semCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        semCombo.setSelectedItem(1);
        JTextField contactField = new JTextField();
        JPasswordField passwordField = new JPasswordField("student123");

        dialog.addFormField("Student ID *:", idField);
        dialog.addFormField("Full Name *:", nameField);
        dialog.addFormField("Institutional Email *:", emailField);
        dialog.addFormField("Academic Department *:", deptCombo);
        dialog.addFormField("Program *:", programField);
        dialog.addFormField("Current Semester *:", semCombo);
        dialog.addFormField("Contact Number:", contactField);
        dialog.addFormField("Default Password *:", passwordField);

        CustomButton saveBtn = new CustomButton("Save Student", theme.getPrimaryAccent(), Color.WHITE);
        saveBtn.addActionListener(e -> {
            String sid = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String dept = (String) deptCombo.getSelectedItem();
            String prog = programField.getText().trim();
            int sem = (Integer) semCombo.getSelectedItem();
            String contact = contactField.getText().trim();
            String pwd = new String(passwordField.getPassword()).trim();

            if (!ValidationUtil.isValidId(sid)) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid Student ID (alphanumeric).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(dialog, "Full Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (!ValidationUtil.isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, "Please enter a valid email address.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                Student student = new Student(sid, name, email, PasswordUtil.hashPassword(pwd), dept, prog, sem, contact);
                studentService.addStudent(student);
                dialog.dispose();
                applyFilters();
                JOptionPane.showMessageDialog(mainFrame, "Student record successfully added!", "Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (DuplicateStudentIdException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Student ID", JOptionPane.ERROR_MESSAGE);
            }
        });

        CustomButton cancelBtn = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.getButtonPanel().add(cancelBtn);
        dialog.getButtonPanel().add(saveBtn);
        dialog.setVisible(true);
    }

    private void openEditStudentDialog() {
        int row = tablePanel.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to edit.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sid = (String) tablePanel.getTable().getValueAt(row, 0);
        Student student = studentService.getStudentById(sid);
        if (student == null) return;

        FormDialog dialog = new FormDialog(mainFrame, "Edit Student — " + student.getStudentId(), 500, 460);

        JTextField nameField = new JTextField(student.getFullName());
        JTextField emailField = new JTextField(student.getEmail());
        JComboBox<String> deptCombo = new JComboBox<>(new String[]{"CSE", "SWE", "EEE", "BBA", "ENG"});
        deptCombo.setSelectedItem(student.getDepartment());
        JTextField programField = new JTextField(student.getProgram());
        JComboBox<Integer> semCombo = new JComboBox<>(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12});
        semCombo.setSelectedItem(student.getSemester());
        JTextField contactField = new JTextField(student.getContactNumber());
        JCheckBox activeBox = new JCheckBox("Account Active", student.isActive());

        dialog.addFormField("Full Name *:", nameField);
        dialog.addFormField("Institutional Email *:", emailField);
        dialog.addFormField("Department *:", deptCombo);
        dialog.addFormField("Program *:", programField);
        dialog.addFormField("Current Semester *:", semCombo);
        dialog.addFormField("Contact Number:", contactField);
        dialog.addFormField("Status:", activeBox);

        CustomButton updateBtn = new CustomButton("Update Information", theme.getPrimaryAccent(), Color.WHITE);
        updateBtn.addActionListener(e -> {
            student.setFullName(nameField.getText().trim());
            student.setEmail(emailField.getText().trim());
            student.setDepartment((String) deptCombo.getSelectedItem());
            student.setProgram(programField.getText().trim());
            student.setSemester((Integer) semCombo.getSelectedItem());
            student.setContactNumber(contactField.getText().trim());
            student.setActive(activeBox.isSelected());

            studentService.updateStudent(student);
            dialog.dispose();
            applyFilters();
            JOptionPane.showMessageDialog(mainFrame, "Student details updated successfully.", "Saved", JOptionPane.INFORMATION_MESSAGE);
        });

        CustomButton cancelBtn = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
        cancelBtn.addActionListener(e -> dialog.dispose());

        dialog.getButtonPanel().add(cancelBtn);
        dialog.getButtonPanel().add(updateBtn);
        dialog.setVisible(true);
    }

    private void deleteSelectedStudent() {
        int row = tablePanel.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a student from the table to delete.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String sid = (String) tablePanel.getTable().getValueAt(row, 0);
        String sname = (String) tablePanel.getTable().getValueAt(row, 1);

        int confirm = JOptionPane.showConfirmDialog(mainFrame,
                "Are you sure you want to deactivate student: " + sname + " (" + sid + ")?\nThis action will deactivate their institutional account.",
                "Confirm Student Removal", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE);

        if (confirm == JOptionPane.YES_OPTION) {
            studentService.deleteStudent(sid);
            applyFilters();
            JOptionPane.showMessageDialog(mainFrame, "Student account deactivated.", "Status Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    public void refreshTheme() {
        buildUI();
    }
}

