package com.examsphere;

import com.examsphere.service.AuthService;
import com.examsphere.storage.FileStorageService;
import com.examsphere.ui.LoginFrame;

import javax.swing.*;

/**
 * Main application entry point for EduAsset — University Examination & Academic
 * Assessment Management System.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class Main {

    public static void main(String[] args) {
        // Set modern system look and feel
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }

        // Initialize persistent file storage directory
        FileStorageService.getInstance().initializeDataDirectory();

        // Initialize authentication and seed default accounts
        AuthService.getInstance();

        // Launch Login UI on Swing Event Dispatch Thread (EDT)
        SwingUtilities.invokeLater(() -> {
            new LoginFrame();
        });
    }
}

