package com.examsphere.ui;

import com.examsphere.model.User;
import com.examsphere.service.AuthService;
import com.examsphere.exception.InvalidLoginException;
import com.examsphere.util.ThemeManager;
import com.examsphere.util.Constants;
import com.examsphere.components.CustomButton;
import com.examsphere.components.RoundedPanel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

/**
 * Full-screen login dialog with branded left panel and credential entry right panel.
 */
public class LoginFrame extends JFrame {

    private JTextField userIdField;
    private JPasswordField passwordField;
    private CustomButton loginButton;
    private JLabel errorLabel;
    private final ThemeManager theme = ThemeManager.getInstance();

    public LoginFrame() {
        setTitle("EduAsset — Examination Management System");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(Constants.WINDOW_WIDTH, Constants.WINDOW_HEIGHT);
        setMinimumSize(new Dimension(Constants.MIN_WIDTH, Constants.MIN_HEIGHT));
        setLocationRelativeTo(null);
        setLayout(new GridLayout(1, 2));

        add(createBrandingPanel());
        add(createLoginPanel());

        theme.addThemeChangeListener(this::refreshTheme);
        setVisible(true);
    }

    /* ──────────────── LEFT: Branding Panel ──────────────── */
    private JPanel createBrandingPanel() {
        JPanel panel = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                Color accent = theme.getPrimaryAccent();
                Color darker = accent.darker();
                GradientPaint gp = new GradientPaint(0, 0, accent, getWidth(), getHeight(), darker);
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                g2.dispose();
            }
        };
        panel.setOpaque(false);

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.insets = new Insets(8, 20, 8, 20);
        gbc.anchor = GridBagConstraints.CENTER;

        // Logo circle placeholder
        JPanel logoCircle = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(255, 255, 255, 40));
                g2.fillOval(0, 0, getWidth(), getHeight());
                g2.setColor(Color.WHITE);
                g2.setFont(new Font("Segoe UI", Font.BOLD, 42));
                FontMetrics fm = g2.getFontMetrics();
                String txt = "EA";
                int tx = (getWidth() - fm.stringWidth(txt)) / 2;
                int ty = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();
                g2.drawString(txt, tx, ty);
                g2.dispose();
            }
        };
        logoCircle.setOpaque(false);
        logoCircle.setPreferredSize(new Dimension(110, 110));
        gbc.gridy = 0;
        panel.add(logoCircle, gbc);

        // Title
        JLabel title = new JLabel("EduAsset");
        title.setFont(new Font("Segoe UI", Font.BOLD, 44));
        title.setForeground(Color.WHITE);
        title.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 1;
        panel.add(title, gbc);

        // Tagline
        JLabel tagline = new JLabel("Examination Management System");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        tagline.setForeground(new Color(255, 255, 255, 200));
        tagline.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 2;
        panel.add(tagline, gbc);

        // Description
        JLabel desc = new JLabel("<html><div style='text-align:center;width:300px;'>"
                + "Streamline exam scheduling, attendance tracking, marks management, "
                + "and result publication — all in one place.</div></html>");
        desc.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        desc.setForeground(new Color(255, 255, 255, 170));
        desc.setHorizontalAlignment(SwingConstants.CENTER);
        gbc.gridy = 3;
        gbc.insets = new Insets(24, 40, 8, 40);
        panel.add(desc, gbc);

        return panel;
    }

    /* ──────────────── RIGHT: Login Form Panel ──────────────── */
    private JPanel createLoginPanel() {
        JPanel outer = new JPanel(new GridBagLayout());
        outer.setBackground(theme.getBackground());

        JPanel card = new RoundedPanel(Constants.CARD_RADIUS, theme.getMainSurface());
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(new EmptyBorder(40, 44, 40, 44));
        card.setPreferredSize(new Dimension(380, 420));

        // Welcome text
        JLabel welcomeLabel = new JLabel("Welcome Back");
        welcomeLabel.setFont(theme.getBoldFont(26));
        welcomeLabel.setForeground(theme.getPrimaryText());
        welcomeLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(welcomeLabel);

        card.add(Box.createVerticalStrut(4));

        JLabel subLabel = new JLabel("Sign in to your account");
        subLabel.setFont(theme.getFont(13));
        subLabel.setForeground(theme.getSecondaryText());
        subLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(subLabel);

        card.add(Box.createVerticalStrut(32));

        // User ID field
        card.add(createFieldLabel("User ID"));
        card.add(Box.createVerticalStrut(6));
        userIdField = createStyledTextField("Enter your User ID");
        card.add(userIdField);

        card.add(Box.createVerticalStrut(18));

        // Password field
        card.add(createFieldLabel("Password"));
        card.add(Box.createVerticalStrut(6));
        passwordField = createStyledPasswordField("Enter your password");
        card.add(passwordField);

        card.add(Box.createVerticalStrut(10));

        // Error label
        errorLabel = new JLabel(" ");
        errorLabel.setFont(theme.getFont(12));
        errorLabel.setForeground(theme.getDanger());
        errorLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(errorLabel);

        card.add(Box.createVerticalStrut(14));

        // Login button
        loginButton = new CustomButton("Sign In");
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginButton.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        loginButton.setPreferredSize(new Dimension(300, 44));
        loginButton.addActionListener(e -> doLogin());
        card.add(loginButton);

        card.add(Box.createVerticalStrut(18));

        // Demo hint
        JLabel hint = new JLabel("<html><div style='text-align:center;width:260px;'>"
                + "Demo: ADMIN001/admin123 · TCH001/teacher123 · STU001/student123</div></html>");
        hint.setFont(theme.getFont(11));
        hint.setForeground(theme.getSecondaryText());
        hint.setAlignmentX(Component.LEFT_ALIGNMENT);
        card.add(hint);

        outer.add(card);
        return outer;
    }

    private JLabel createFieldLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(theme.getBoldFont(13));
        label.setForeground(theme.getPrimaryText());
        label.setAlignmentX(Component.LEFT_ALIGNMENT);
        return label;
    }

    private JTextField createStyledTextField(String placeholder) {
        JTextField field = new JTextField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getText().isEmpty() && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(theme.getSecondaryText());
                    g2.setFont(theme.getFont(13));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        styleInputField(field);
        return field;
    }

    private JPasswordField createStyledPasswordField(String placeholder) {
        JPasswordField field = new JPasswordField() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (getPassword().length == 0 && !hasFocus()) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    g2.setColor(theme.getSecondaryText());
                    g2.setFont(theme.getFont(13));
                    Insets ins = getInsets();
                    g2.drawString(placeholder, ins.left, getHeight() / 2 + g2.getFontMetrics().getAscent() / 2 - 2);
                    g2.dispose();
                }
            }
        };
        styleInputField(field);
        // Allow Enter key to login
        field.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    doLogin();
                }
            }
        });
        return field;
    }

    private void styleInputField(JTextField field) {
        field.setFont(theme.getFont(14));
        field.setForeground(theme.getPrimaryText());
        field.setBackground(theme.getSecondarySurface());
        field.setCaretColor(theme.getPrimaryText());
        field.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(theme.getBorderColor(), 1, true),
                new EmptyBorder(10, 14, 10, 14)
        ));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        field.setPreferredSize(new Dimension(300, 44));
    }

    /* ──────────────── Login Logic ──────────────── */
    private void doLogin() {
        String userId = userIdField.getText().trim();
        String password = new String(passwordField.getPassword());

        if (userId.isEmpty() || password.isEmpty()) {
            errorLabel.setText("Please enter both User ID and password.");
            return;
        }

        loginButton.setEnabled(false);
        errorLabel.setText(" ");

        SwingWorker<User, Void> worker = new SwingWorker<>() {
            @Override
            protected User doInBackground() throws Exception {
                return AuthService.getInstance().login(userId, password);
            }

            @Override
            protected void done() {
                try {
                    User user = get();
                    dispose();
                    SwingUtilities.invokeLater(() -> new MainFrame(user));
                } catch (Exception ex) {
                    Throwable cause = ex.getCause();
                    String msg = (cause instanceof InvalidLoginException)
                            ? cause.getMessage()
                            : "Login failed. Please try again.";
                    errorLabel.setText(msg);
                    loginButton.setEnabled(true);
                    passwordField.setText("");
                }
            }
        };
        worker.execute();
    }

    private void refreshTheme() {
        SwingUtilities.invokeLater(() -> {
            getContentPane().removeAll();
            add(createBrandingPanel());
            add(createLoginPanel());
            revalidate();
            repaint();
        });
    }
}
