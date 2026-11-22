package com.examsphere.ui;

import com.examsphere.components.CustomButton;
import com.examsphere.components.RoundedPanel;
import com.examsphere.storage.FileStorageService;
import com.examsphere.util.Constants;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;

/**
 * System settings and configuration panel supporting light/dark theme toggles,
 * palette inspection, institutional metadata review, and manual data backup triggers.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class SettingsPanel extends JPanel {

    private final MainFrame mainFrame;
    private final ThemeManager theme = ThemeManager.getInstance();

    public SettingsPanel(MainFrame mainFrame) {
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

        JLabel title = new JLabel("System Settings, Personalization & Data Maintenance");
        title.setFont(theme.getBoldFont(18));
        title.setForeground(theme.getPrimaryText());
        headerCard.add(title, BorderLayout.WEST);

        add(headerCard, BorderLayout.NORTH);

        // Center Content
        JPanel content = new JPanel();
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));
        content.setOpaque(false);

        // 1. Appearance / Theme Card
        RoundedPanel themeCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        themeCard.setLayout(new BorderLayout(Constants.SPACING, Constants.SPACING));
        themeCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        themeCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        JLabel themeHeader = new JLabel("Visual Appearance & Display Mode");
        themeHeader.setFont(theme.getBoldFont(16));
        themeHeader.setForeground(theme.getPrimaryText());
        themeCard.add(themeHeader, BorderLayout.NORTH);

        JPanel themeBody = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        themeBody.setOpaque(false);

        JLabel currentMode = new JLabel("Active Interface Mode: " + (theme.isDarkTheme() ? "Dark Obsidian" : "Light Porcelain"));
        currentMode.setFont(theme.getFont(14));
        currentMode.setForeground(theme.getSecondaryText());
        themeBody.add(currentMode);

        CustomButton toggleBtn = new CustomButton(theme.isDarkTheme() ? "☀ Switch to Light Mode" : "🌙 Switch to Dark Mode",
                theme.getPrimaryAccent(), Color.WHITE);
        toggleBtn.addActionListener(e -> {
            theme.toggleTheme();
            theme.notifyThemeChanged();
        });
        themeBody.add(toggleBtn);

        themeCard.add(themeBody, BorderLayout.CENTER);
        content.add(themeCard);
        content.add(Box.createVerticalStrut(Constants.SPACING));

        // 2. Data Persistence & Backup Card
        RoundedPanel backupCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        backupCard.setLayout(new BorderLayout(Constants.SPACING, Constants.SPACING));
        backupCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));
        backupCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));

        JLabel backupHeader = new JLabel("Data Persistence & Serialization Maintenance");
        backupHeader.setFont(theme.getBoldFont(16));
        backupHeader.setForeground(theme.getPrimaryText());
        backupCard.add(backupHeader, BorderLayout.NORTH);

        JPanel backupBody = new JPanel(new FlowLayout(FlowLayout.LEFT, 16, 10));
        backupBody.setOpaque(false);

        JLabel backupDesc = new JLabel("Persistent storage format: Java Object Serialization (.dat binary files).");
        backupDesc.setFont(theme.getFont(14));
        backupDesc.setForeground(theme.getSecondaryText());
        backupBody.add(backupDesc);

        CustomButton backupBtn = new CustomButton("⤓ Trigger Immediate Local Data Backup", theme.getSuccess(), Color.WHITE);
        backupBtn.addActionListener(e -> {
            boolean ok = FileStorageService.getInstance().backupData(Constants.BACKUP_DIR);
            if (ok) {
                JOptionPane.showMessageDialog(mainFrame, "Data successfully backed up to directory: '" + Constants.BACKUP_DIR + "/'", "Backup Successful", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(mainFrame, "Could not complete backup operation.", "Backup Error", JOptionPane.ERROR_MESSAGE);
            }
        });
        backupBody.add(backupBtn);

        backupCard.add(backupBody, BorderLayout.CENTER);
        content.add(backupCard);
        content.add(Box.createVerticalStrut(Constants.SPACING));

        // 3. About & Institutional Credentials Card
        RoundedPanel aboutCard = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        aboutCard.setLayout(new BorderLayout(Constants.SPACING, Constants.SPACING));
        aboutCard.setBorder(new EmptyBorder(Constants.SPACING, Constants.SPACING, Constants.SPACING, Constants.SPACING));

        JLabel aboutHeader = new JLabel("Institutional Project Identity & Academic Metadata");
        aboutHeader.setFont(theme.getBoldFont(16));
        aboutHeader.setForeground(theme.getPrimaryText());
        aboutCard.add(aboutHeader, BorderLayout.NORTH);

        JPanel aboutBody = new JPanel();
        aboutBody.setLayout(new BoxLayout(aboutBody, BoxLayout.Y_AXIS));
        aboutBody.setOpaque(false);

        aboutBody.add(createMetaLabel("Project Title:", "EduAsset — University Examination & Academic Assessment Management System"));
        aboutBody.add(createMetaLabel("Tagline:", "Smart Examination Administration. Reliable Academic Assessment."));
        aboutBody.add(createMetaLabel("Course:", "CSE 221 — Object Oriented Programming"));
        aboutBody.add(createMetaLabel("Institution:", "Daffodil International University (DIU)"));
        aboutBody.add(createMetaLabel("GUI Framework:", "Native Java Swing (Modern Flat Nimbra Architecture)"));
        aboutBody.add(createMetaLabel("Core OOP Architecture:", "Encapsulation, Inheritance, Polymorphism, Abstraction, Custom Exceptions"));
        aboutBody.add(createMetaLabel("Data Tier:", "Native Java ObjectOutputStream / ObjectInputStream File I/O"));

        aboutCard.add(aboutBody, BorderLayout.CENTER);
        content.add(aboutCard);

        JScrollPane scroll = new JScrollPane(content);
        scroll.setBorder(null);
        scroll.setOpaque(false);
        scroll.getViewport().setOpaque(false);
        scroll.getVerticalScrollBar().setUnitIncrement(16);

        add(scroll, BorderLayout.CENTER);
        revalidate();
        repaint();
    }

    private JPanel createMetaLabel(String key, String value) {
        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 4));
        row.setOpaque(false);
        JLabel k = new JLabel(key);
        k.setFont(theme.getBoldFont(13));
        k.setForeground(theme.getPrimaryText());
        k.setPreferredSize(new Dimension(180, 20));

        JLabel v = new JLabel(value);
        v.setFont(theme.getFont(13));
        v.setForeground(theme.getSecondaryText());

        row.add(k);
        row.add(v);
        return row;
    }

    public void refreshTheme() {
        buildUI();
    }
}

