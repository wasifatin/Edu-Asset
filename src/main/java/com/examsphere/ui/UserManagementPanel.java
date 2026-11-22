package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.DataTablePanel;
import com.examsphere.components.FormDialog;
import com.examsphere.components.RoundedPanel;
import com.examsphere.exception.DuplicateStudentIdException;
import com.examsphere.model.*;
import com.examsphere.service.AuthService;
import com.examsphere.util.Constants;
import com.examsphere.util.PasswordUtil;
import com.examsphere.util.ThemeManager;
import com.examsphere.util.ValidationUtil;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * Administrative user and role management panel supporting account provisioning,
 * role assignments, activation toggles, and security password resets.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class UserManagementPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private final AuthService authService = AuthService.getInstance();

    private DataTablePanel userTable;

    public UserManagementPanel(MainFrame mainFrame) {
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

        JLabel title = new JLabel("System User Accounts, Roles & Access Control");
        title.setFont(theme.getBoldFont(18));
        title.setForeground(theme.getPrimaryText());
        headerCard.add(title, BorderLayout.WEST);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        CustomButton addBtn = new CustomButton("+ Provision Account", theme.getPrimaryAccent(), Color.WHITE);
        addBtn.addActionListener(e -> openAddUserDialog());
        actions.add(addBtn);

        CustomButton toggleActiveBtn = new CustomButton("Toggle Status", theme.getSecondarySurface(), theme.getPrimaryText());
        toggleActiveBtn.addActionListener(e -> toggleUserStatus());
        actions.add(toggleActiveBtn);

        CustomButton resetPwBtn = new CustomButton("Reset Password", theme.getWarning(), Color.WHITE);
        resetPwBtn.addActionListener(e -> resetUserPassword());
        actions.add(resetPwBtn);

        headerCard.add(actions, BorderLayout.EAST);
        add(headerCard, BorderLayout.NORTH);

        // Data Table
        String[] cols = {"User ID", "Full Name", "Email Address", "Institutional Role", "Account Status", "Created Date"};
        userTable = new DataTablePanel(cols, getUserData());
        userTable.addSearchBar("Search accounts by ID or Name...");

        RoundedPanel tableCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        tableCard.setLayout(new BorderLayout());
        tableCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        tableCard.add(userTable, BorderLayout.CENTER);

        add(tableCard, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private Object[][] getUserData() {
        List<User> list = authService.getAllUsers();
        Object[][] data = new Object[list.size()][6];
        for (int i = 0; i < list.size(); i++) {
            User u = list.get(i);
            data[i][0] = u.getUserId();
            data[i][1] = u.getFullName();
            data[i][2] = u.getEmail();
            data[i][3] = u.getDisplayRole();
            data[i][4] = u.isActive() ? "ACTIVE" : "DEACTIVATED";
            data[i][5] = u.getCreatedDate() != null ? u.getCreatedDate() : "2026-11-12";
        }
        return data;
    }

    private void openAddUserDialog() {
        FormDialog dialog = new FormDialog(mainFrame, "Provision Institutional User Account", 480, 480);

        JTextField idField = new JTextField();
        JTextField nameField = new JTextField();
        JTextField emailField = new JTextField();
        JComboBox<String> roleCombo = new JComboBox<>(new String[]{"ADMIN", "COORDINATOR", "TEACHER", "STUDENT"});
        JTextField extraField = new JTextField("CSE"); // Dept or designation
        JPasswordField pwField = new JPasswordField("eduasset123");

        dialog.addFormField("User ID *:", idField);
        dialog.addFormField("Full Name *:", nameField);
        dialog.addFormField("Institutional Email *:", emailField);
        dialog.addFormField("Account Role *:", roleCombo);
        dialog.addFormField("Department / Designation:", extraField);
        dialog.addFormField("Temporary Password *:", pwField);

        CustomButton saveBtn = new CustomButton("Create Account", theme.getPrimaryAccent(), Color.WHITE);
        saveBtn.addActionListener(e -> {
            String uid = idField.getText().trim();
            String name = nameField.getText().trim();
            String email = emailField.getText().trim();
            String role = (String) roleCombo.getSelectedItem();
            String extra = extraField.getText().trim();
            String pw = new String(pwField.getPassword()).trim();

            if (!ValidationUtil.isValidId(uid) || name.isEmpty() || !ValidationUtil.isValidEmail(email)) {
                JOptionPane.showMessageDialog(dialog, "Please verify all required fields.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            String hashed = PasswordUtil.hashPassword(pw);
            User newUser;
            switch (role) {
                case "ADMIN":
                    newUser = new Admin(uid, name, email, hashed, extra.isEmpty() ? "Administrator" : extra);
                    break;
                case "COORDINATOR":
                    newUser = new DepartmentCoordinator(uid, name, email, hashed, extra.isEmpty() ? "CSE" : extra);
                    break;
                case "TEACHER":
                    newUser = new Teacher(uid, name, email, hashed, extra.isEmpty() ? "CSE" : extra, "Faculty Member");
                    break;
                case "STUDENT":
                default:
                    newUser = new Student(uid, name, email, hashed, extra.isEmpty() ? "CSE" : extra, "B.Sc. in " + extra, 1, "+8801700000000");
                    break;
            }

            try {
                authService.createUser(newUser);
                dialog.dispose();
                userTable.setData(getUserData());
                JOptionPane.showMessageDialog(mainFrame, "User account provisioned successfully!", "User Created", JOptionPane.INFORMATION_MESSAGE);
            } catch (DuplicateStudentIdException ex) {
                JOptionPane.showMessageDialog(dialog, ex.getMessage(), "Duplicate Identifier", JOptionPane.ERROR_MESSAGE);
            }
        });

        CustomButton cancelAction = new CustomButton("Cancel", theme.getSecondarySurface(), theme.getPrimaryText());
        cancelAction.addActionListener(e -> dialog.dispose());

        dialog.getButtonPanel().add(cancelAction);
        dialog.getButtonPanel().add(saveBtn);
        dialog.setVisible(true);
    }

    private void toggleUserStatus() {
        int row = userTable.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to toggle status.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String uid = (String) userTable.getTable().getValueAt(row, 0);
        User u = authService.getUserById(uid);
        if (u != null) {
            u.setActive(!u.isActive());
            authService.updateUser(u);
            userTable.setData(getUserData());
            JOptionPane.showMessageDialog(mainFrame, "Account status toggled to: " + (u.isActive() ? "ACTIVE" : "DEACTIVATED"), "Status Updated", JOptionPane.INFORMATION_MESSAGE);
        }
    }

    private void resetUserPassword() {
        int row = userTable.getTable().getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Select a user to reset password.", "Selection Required", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String uid = (String) userTable.getTable().getValueAt(row, 0);
        String newPw = JOptionPane.showInputDialog(mainFrame, "Enter new temporary password for user " + uid + ":", "reset123");
        if (newPw != null && !newPw.trim().isEmpty()) {
            User u = authService.getUserById(uid);
            if (u != null) {
                u.setPasswordHash(PasswordUtil.hashPassword(newPw.trim()));
                authService.updateUser(u);
                JOptionPane.showMessageDialog(mainFrame, "Password updated successfully for " + uid, "Password Reset", JOptionPane.INFORMATION_MESSAGE);
            }
        }
    }

    public void refreshTheme() {
        buildUI();
    }
}

