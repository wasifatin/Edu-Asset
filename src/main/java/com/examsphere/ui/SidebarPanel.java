package com.examsphere.ui;

import com.examsphere.model.User;
import com.examsphere.util.ThemeManager;
import com.examsphere.util.Constants;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Left navigation sidebar with collapsible sections and role-based visibility.
 */
public class SidebarPanel extends JPanel {

    private final MainFrame mainFrame;
    private final User currentUser;
    private final ThemeManager theme = ThemeManager.getInstance();
    private boolean expanded = true;
    private String activeCard = "overview";
    private final List<NavItem> navItems = new ArrayList<>();
    private JPanel navContainer;
    private JScrollPane scrollPane;
    private JPanel profileSection;
    private JButton toggleButton;

    // ── Nav item data ──
    private static class NavItem {
        String label;
        String cardName;
        String iconChar;
        Color iconColor;
        String section;
        String[] allowedRoles; // null = all roles
        JPanel itemPanel;
        JLabel labelComponent;
        JLabel iconLabel;

        NavItem(String label, String cardName, String iconChar, Color iconColor,
                String section, String[] allowedRoles) {
            this.label = label;
            this.cardName = cardName;
            this.iconChar = iconChar;
            this.iconColor = iconColor;
            this.section = section;
            this.allowedRoles = allowedRoles;
        }
    }

    public SidebarPanel(MainFrame mainFrame, User currentUser) {
        this.mainFrame = mainFrame;
        this.currentUser = currentUser;
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(Constants.SIDEBAR_EXPANDED, 0));
        setBackground(theme.getSidebarBg());

        buildNavItems();
        buildUI();

        theme.addThemeChangeListener(this::refreshTheme);
    }

    private void buildNavItems() {
        String role = currentUser.getRole();

        // WORKSPACE
        navItems.add(new NavItem("Overview", "overview", "◉", new Color(0x2F, 0x5C, 0xF0),
                "WORKSPACE", null));
        navItems.add(new NavItem("Student Records", "students", "◈", new Color(0x0E, 0x9A, 0x78),
                "WORKSPACE", new String[]{"ADMIN", "COORDINATOR", "TEACHER"}));
        navItems.add(new NavItem("Departments & Courses", "departments", "◆", new Color(0xC2, 0x7C, 0x0E),
                "WORKSPACE", new String[]{"ADMIN", "COORDINATOR", "TEACHER"}));

        // EXAMINATION
        navItems.add(new NavItem("Exam Scheduling", "examScheduling", "▣", new Color(0xBE, 0x26, 0x39),
                "EXAMINATION", new String[]{"ADMIN", "COORDINATOR"}));
        navItems.add(new NavItem("Rooms & Seating", "rooms", "▦", new Color(0x15, 0x63, 0xB5),
                "EXAMINATION", new String[]{"ADMIN", "COORDINATOR"}));
        navItems.add(new NavItem("Attendance", "attendance", "▤", new Color(0x0E, 0x9A, 0x78),
                "EXAMINATION", new String[]{"ADMIN", "COORDINATOR", "TEACHER"}));
        navItems.add(new NavItem("Invigilators", "invigilators", "▧", new Color(0xC2, 0x7C, 0x0E),
                "EXAMINATION", new String[]{"ADMIN", "COORDINATOR"}));

        // ACADEMIC
        navItems.add(new NavItem("Marks & Assessment", "marks", "◧", new Color(0x2F, 0x5C, 0xF0),
                "ACADEMIC", new String[]{"ADMIN", "COORDINATOR", "TEACHER"}));
        navItems.add(new NavItem("Results & Transcripts", "results", "◨", new Color(0x0E, 0x9A, 0x78),
                "ACADEMIC", null));
        navItems.add(new NavItem("Applications", "applications", "◩", new Color(0xC2, 0x7C, 0x0E),
                "ACADEMIC", null));

        // INSIGHTS
        navItems.add(new NavItem("Reports & Analytics", "reports", "◮", new Color(0x15, 0x63, 0xB5),
                "INSIGHTS", new String[]{"ADMIN", "COORDINATOR"}));
        navItems.add(new NavItem("Activity History", "history", "◭", new Color(0xBE, 0x26, 0x39),
                "INSIGHTS", new String[]{"ADMIN", "COORDINATOR"}));

        // ADMINISTRATION
        navItems.add(new NavItem("User Management", "users", "◫", new Color(0xBE, 0x26, 0x39),
                "ADMINISTRATION", new String[]{"ADMIN"}));
        navItems.add(new NavItem("Settings", "settings", "◬", new Color(0x55, 0x61, 0x7C),
                "ADMINISTRATION", null));
    }

    private boolean isItemVisible(NavItem item) {
        if (item.allowedRoles == null) return true;
        String role = currentUser.getRole();
        for (String r : item.allowedRoles) {
            if (r.equals(role)) return true;
        }
        return false;
    }

    private void buildUI() {
        removeAll();
        setBackground(theme.getSidebarBg());

        // Toggle button at top
        JPanel topSection = new JPanel(new BorderLayout());
        topSection.setBackground(theme.getSidebarBg());
        topSection.setBorder(new EmptyBorder(12, 12, 8, 12));

        if (expanded) {
            JLabel brandLabel = new JLabel("  EduAsset");
            brandLabel.setFont(theme.getBoldFont(18));
            brandLabel.setForeground(theme.getPrimaryAccent());
            topSection.add(brandLabel, BorderLayout.CENTER);
        }

        toggleButton = new JButton(expanded ? "◀" : "▶");
        toggleButton.setFont(theme.getFont(14));
        toggleButton.setForeground(theme.getSecondaryText());
        toggleButton.setBackground(theme.getSidebarBg());
        toggleButton.setBorder(new EmptyBorder(4, 8, 4, 8));
        toggleButton.setFocusPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        toggleButton.addActionListener(e -> toggleExpand());
        topSection.add(toggleButton, BorderLayout.EAST);

        add(topSection, BorderLayout.NORTH);

        // Nav items container
        navContainer = new JPanel();
        navContainer.setLayout(new BoxLayout(navContainer, BoxLayout.Y_AXIS));
        navContainer.setBackground(theme.getSidebarBg());
        navContainer.setBorder(new EmptyBorder(4, 0, 4, 0));

        String currentSection = "";
        for (NavItem item : navItems) {
            if (!isItemVisible(item)) continue;

            // Section header
            if (!item.section.equals(currentSection)) {
                currentSection = item.section;
                if (expanded) {
                    JLabel sectionLabel = new JLabel("  " + currentSection);
                    sectionLabel.setFont(theme.getBoldFont(10));
                    sectionLabel.setForeground(theme.getSecondaryText());
                    sectionLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
                    sectionLabel.setBorder(new EmptyBorder(14, 16, 6, 8));
                    sectionLabel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
                    navContainer.add(sectionLabel);
                } else {
                    navContainer.add(Box.createVerticalStrut(10));
                    JSeparator sep = new JSeparator();
                    sep.setForeground(theme.getBorderColor());
                    sep.setMaximumSize(new Dimension(Integer.MAX_VALUE, 1));
                    navContainer.add(sep);
                    navContainer.add(Box.createVerticalStrut(6));
                }
            }

            JPanel itemPanel = createNavItemPanel(item);
            item.itemPanel = itemPanel;
            navContainer.add(itemPanel);
        }

        scrollPane = new JScrollPane(navContainer);
        scrollPane.setBorder(null);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.getVerticalScrollBar().setUnitIncrement(12);
        scrollPane.setBackground(theme.getSidebarBg());
        scrollPane.getViewport().setBackground(theme.getSidebarBg());
        add(scrollPane, BorderLayout.CENTER);

        // Bottom: profile section
        profileSection = createProfileSection();
        add(profileSection, BorderLayout.SOUTH);

        revalidate();
        repaint();
    }

    private JPanel createNavItemPanel(NavItem item) {
        boolean isActive = item.cardName.equals(activeCard);

        JPanel panel = new JPanel(new BorderLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (item.cardName.equals(activeCard)) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(theme.getPrimaryAccent());
                    g2.fill(new RoundRectangle2D.Float(8, 2, getWidth() - 16, getHeight() - 4, 10, 10));
                    g2.dispose();
                }
            }
        };
        panel.setOpaque(false);
        panel.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 42));
        panel.setPreferredSize(new Dimension(expanded ? Constants.SIDEBAR_EXPANDED : Constants.SIDEBAR_COLLAPSED, 42));
        panel.setBorder(new EmptyBorder(0, expanded ? 20 : 0, 0, 8));

        // Icon
        JLabel iconLabel = new JLabel(item.iconChar, SwingConstants.CENTER);
        iconLabel.setFont(theme.getFont(16));
        iconLabel.setForeground(isActive ? Color.WHITE : item.iconColor);
        iconLabel.setPreferredSize(new Dimension(36, 36));
        item.iconLabel = iconLabel;
        panel.add(iconLabel, BorderLayout.WEST);

        // Label
        if (expanded) {
            JLabel label = new JLabel("  " + item.label);
            label.setFont(theme.getFont(13));
            label.setForeground(isActive ? Color.WHITE : theme.getPrimaryText());
            item.labelComponent = label;
            panel.add(label, BorderLayout.CENTER);
        }

        // Hover effect
        panel.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                if (!item.cardName.equals(activeCard)) {
                    panel.setBackground(theme.getSecondarySurface());
                    panel.setOpaque(true);
                    panel.repaint();
                }
            }
            @Override
            public void mouseExited(MouseEvent e) {
                panel.setOpaque(false);
                panel.repaint();
            }
            @Override
            public void mouseClicked(MouseEvent e) {
                mainFrame.showPanel(item.cardName);
            }
        });

        return panel;
    }

    private JPanel createProfileSection() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(theme.getSidebarBg());
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, theme.getBorderColor()),
                new EmptyBorder(12, 16, 14, 16)
        ));

        // Avatar placeholder
        JPanel avatar = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(theme.getPrimaryAccent());
                g2.fillOval(0, 0, 38, 38);
                g2.setColor(Color.WHITE);
                g2.setFont(theme.getBoldFont(15));
                String initials = getInitials(currentUser.getFullName());
                FontMetrics fm = g2.getFontMetrics();
                int tx = (38 - fm.stringWidth(initials)) / 2;
                int ty = (38 - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(initials, tx, ty);
                g2.dispose();
            }
        };
        avatar.setOpaque(false);
        avatar.setPreferredSize(new Dimension(40, 40));
        panel.add(avatar, BorderLayout.WEST);

        if (expanded) {
            JPanel infoPanel = new JPanel();
            infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
            infoPanel.setBackground(theme.getSidebarBg());
            infoPanel.setBorder(new EmptyBorder(2, 10, 2, 0));

            JLabel nameLabel = new JLabel(currentUser.getFullName());
            nameLabel.setFont(theme.getBoldFont(13));
            nameLabel.setForeground(theme.getPrimaryText());
            infoPanel.add(nameLabel);

            JLabel roleLabel = new JLabel(currentUser.getDisplayRole());
            roleLabel.setFont(theme.getFont(11));
            roleLabel.setForeground(theme.getSecondaryText());
            infoPanel.add(roleLabel);

            panel.add(infoPanel, BorderLayout.CENTER);
        }

        return panel;
    }

    private String getInitials(String name) {
        if (name == null || name.isEmpty()) return "?";
        String[] parts = name.trim().split("\\s+");
        if (parts.length == 1) return parts[0].substring(0, 1).toUpperCase();
        return (parts[0].charAt(0) + "" + parts[parts.length - 1].charAt(0)).toUpperCase();
    }

    public void setActiveItem(String cardName) {
        this.activeCard = cardName;
        buildUI();
    }

    public void toggleExpand() {
        expanded = !expanded;
        int width = expanded ? Constants.SIDEBAR_EXPANDED : Constants.SIDEBAR_COLLAPSED;
        setPreferredSize(new Dimension(width, 0));
        buildUI();
        mainFrame.revalidate();
    }

    public boolean isExpanded() {
        return expanded;
    }

    private void refreshTheme() {
        SwingUtilities.invokeLater(this::buildUI);
    }
}
