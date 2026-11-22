package com.examsphere.ui;

import com.examsphere.model.User;
import com.examsphere.util.ThemeManager;
import com.examsphere.util.Constants;
import com.examsphere.service.AuthService;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Top navigation bar — page title, search, theme toggle, user info.
 */
public class TopBarPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();
    private JLabel titleLabel;
    private JTextField searchField;
    private JButton themeToggle;
    private JLabel userLabel;

    public TopBarPanel(MainFrame mainFrame) {
        this.mainFrame = mainFrame;
        setPreferredSize(new Dimension(0, Constants.TOPBAR_HEIGHT));
        setLayout(new BorderLayout());
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(0, 0, 1, 0, theme.getBorderColor()),
                new EmptyBorder(0, Constants.SPACING_LG, 0, Constants.SPACING_LG)
        ));
        setBackground(theme.getMainSurface());

        buildUI();
        theme.addThemeChangeListener(this::refreshTheme);
    }

    private void buildUI() {
        removeAll();

        // ──── Left: Page title ────
        titleLabel = new JLabel("Dashboard Overview");
        titleLabel.setFont(theme.getBoldFont(18));
        titleLabel.setForeground(theme.getPrimaryText());
        titleLabel.setBorder(new EmptyBorder(0, 4, 0, 0));
        add(titleLabel, BorderLayout.WEST);

        // ──── Right: controls ────
        JPanel rightPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 0));
        rightPanel.setOpaque(false);
        rightPanel.setBorder(new EmptyBorder(0, 0, 0, 0));

        // Search field
        searchField = new JTextField(18) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(theme.getSecondaryText());
                    g2.setFont(theme.getFont(13));
                    Insets ins = getInsets();
                    g2.drawString("🔍  Search...", ins.left + 2,
                            getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        searchField.setFont(theme.getFont(13));
        searchField.setForeground(theme.getPrimaryText());
        searchField.setBackground(theme.getSecondarySurface());
        searchField.setCaretColor(theme.getPrimaryText());
        searchField.setPreferredSize(new Dimension(220, 36));
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getBorderColor(), 1, true),
                new EmptyBorder(6, 12, 6, 12)
        ));
        rightPanel.add(searchField);

        // Vertical spacer
        rightPanel.add(Box.createHorizontalStrut(4));

        // Theme toggle button
        themeToggle = new JButton(theme.isDarkTheme() ? "☀" : "🌙");
        themeToggle.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 18));
        themeToggle.setForeground(theme.getSecondaryText());
        themeToggle.setPreferredSize(new Dimension(40, 36));
        themeToggle.setBackground(theme.getSecondarySurface());
        themeToggle.setFocusPainted(false);
        themeToggle.setBorderPainted(false);
        themeToggle.setContentAreaFilled(false);
        themeToggle.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        themeToggle.setToolTipText("Toggle theme");
        themeToggle.addActionListener(e -> {
            theme.toggleTheme();
            theme.notifyThemeChanged();
        });
        themeToggle.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                themeToggle.setOpaque(true);
                themeToggle.setBackground(theme.getSecondarySurface());
                themeToggle.repaint();
            }
            @Override
            public void mouseExited(MouseEvent e) {
                themeToggle.setOpaque(false);
                themeToggle.repaint();
            }
        });
        rightPanel.add(themeToggle);

        // Notification placeholder
        JButton notifBtn = new JButton("🔔");
        notifBtn.setFont(new Font("Segoe UI Emoji", Font.PLAIN, 16));
        notifBtn.setForeground(theme.getSecondaryText());
        notifBtn.setPreferredSize(new Dimension(40, 36));
        notifBtn.setFocusPainted(false);
        notifBtn.setBorderPainted(false);
        notifBtn.setContentAreaFilled(false);
        notifBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        rightPanel.add(notifBtn);

        rightPanel.add(Box.createHorizontalStrut(4));

        // User avatar + name
        User user = mainFrame.getCurrentUser();
        JPanel userSection = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        userSection.setOpaque(false);

        // Mini avatar
        JPanel miniAvatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(theme.getPrimaryAccent());
                g2.fillOval(0, 2, 32, 32);
                g2.setColor(Color.WHITE);
                g2.setFont(theme.getBoldFont(12));
                String initials = getInitials(user.getFullName());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (32 - fm.stringWidth(initials)) / 2;
                int ty = 2 + (32 - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initials, tx, ty);
                g2.dispose();
            }
        };
        miniAvatar.setOpaque(false);
        miniAvatar.setPreferredSize(new Dimension(34, 36));
        userSection.add(miniAvatar);

        userLabel = new JLabel(user.getFullName());
        userLabel.setFont(theme.getBoldFont(13));
        userLabel.setForeground(theme.getPrimaryText());
        userSection.add(userLabel);

        // Dropdown arrow
        JLabel arrow = new JLabel(" ▾");
        arrow.setFont(theme.getFont(12));
        arrow.setForeground(theme.getSecondaryText());
        userSection.add(arrow);

        // Dropdown popup on click
        userSection.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        userSection.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                showUserMenu(userSection, e);
            }
        });

        rightPanel.add(userSection);
        add(rightPanel, BorderLayout.EAST);

        revalidate();
        repaint();
    }

    private void showUserMenu(Component anchor, MouseEvent e) {
        JPopupMenu popup = new JPopupMenu();
        popup.setBackground(theme.getMainSurface());
        popup.setBorder(BorderFactory.createLineBorder(theme.getBorderColor()));

        JMenuItem profileItem = new JMenuItem("Profile");
        profileItem.setFont(theme.getFont(13));
        profileItem.setForeground(theme.getPrimaryText());
        profileItem.setBackground(theme.getMainSurface());
        popup.add(profileItem);

        JMenuItem logoutItem = new JMenuItem("Logout");
        logoutItem.setFont(theme.getFont(13));
        logoutItem.setForeground(theme.getDanger());
        logoutItem.setBackground(theme.getMainSurface());
        logoutItem.addActionListener(ev -> {
            AuthService.getInstance().logout();
            mainFrame.dispose();
            SwingUtilities.invokeLater(LoginFrame::new);
        });
        popup.add(logoutItem);

        popup.show(anchor, 0, anchor.getHeight());
    }

    public void updateTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    private void refreshTheme() {
        SwingUtilities.invokeLater(() -> {
            setBackground(theme.getMainSurface());
            setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createMatteBorder(0, 0, 1, 0, theme.getBorderColor()),
                    new EmptyBorder(0, Constants.SPACING_LG, 0, Constants.SPACING_LG)
            ));
            buildUI();
        });
    }
}
