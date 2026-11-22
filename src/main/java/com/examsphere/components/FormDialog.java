package com.examsphere.components;

import com.examsphere.util.Constants;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import java.awt.*;

/**
 * A themed modal dialog for form input with a content area and a button panel.
 * <p>
 * FormDialog extends {@link JDialog} to provide a consistent form layout used
 * throughout the application. It features:
 * <ul>
 *   <li>A scrollable content panel with {@link GridBagLayout} for form fields</li>
 *   <li>A helper method {@link #addFormField(String, JComponent)} to add labeled fields</li>
 *   <li>A button panel at the bottom with right-aligned {@link FlowLayout}</li>
 *   <li>Themed background, text, and border colors from {@link ThemeManager}</li>
 *   <li>Centered positioning relative to the parent frame</li>
 * </ul>
 * </p>
 *
 * @author ExamSphere
 * @version 1.0
 */
public class FormDialog extends JDialog {

    /** The main content panel with GridBagLayout for form fields. */
    private final JPanel contentPanel;

    /** The button panel at the bottom with right-aligned FlowLayout. */
    private final JPanel buttonPanel;

    /** Tracks the current row index for adding form fields. */
    private int currentRow = 0;

    /**
     * Constructs a modal FormDialog with the specified parent, title, and dimensions.
     * The dialog is centered on the parent frame and styled with the current theme.
     *
     * @param parent the parent JFrame (may be null)
     * @param title  the dialog title
     * @param width  the dialog width in pixels
     * @param height the dialog height in pixels
     */
    public FormDialog(JFrame parent, String title, int width, int height) {
        super(parent, title, true);

        ThemeManager tm = ThemeManager.getInstance();

        setSize(width, height);
        setLocationRelativeTo(parent);
        setResizable(false);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);

        // Main wrapper panel
        JPanel mainPanel = new JPanel(new BorderLayout(0, Constants.SPACING));
        mainPanel.setBackground(tm.getBackground());
        mainPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(tm.getBorderColor(), 1),
                BorderFactory.createEmptyBorder(
                        Constants.SPACING_LG, Constants.SPACING_LG,
                        Constants.SPACING, Constants.SPACING_LG)
        ));

        // Title label at the top
        JLabel titleLabel = new JLabel(title);
        titleLabel.setFont(tm.getBoldFont(18));
        titleLabel.setForeground(tm.getPrimaryText());
        titleLabel.setBorder(BorderFactory.createEmptyBorder(0, 0, Constants.SPACING_SM, 0));
        mainPanel.add(titleLabel, BorderLayout.NORTH);

        // Content panel — GridBagLayout for form fields
        contentPanel = new JPanel(new GridBagLayout());
        contentPanel.setBackground(tm.getBackground());

        JScrollPane contentScroll = new JScrollPane(contentPanel);
        contentScroll.setBorder(BorderFactory.createEmptyBorder());
        contentScroll.getViewport().setBackground(tm.getBackground());
        contentScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mainPanel.add(contentScroll, BorderLayout.CENTER);

        // Button panel — right-aligned FlowLayout at SOUTH
        buttonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, Constants.SPACING_SM, 0));
        buttonPanel.setBackground(tm.getBackground());
        buttonPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, tm.getBorderColor()),
                BorderFactory.createEmptyBorder(Constants.SPACING_SM, 0, 0, 0)
        ));
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        setContentPane(mainPanel);
    }

    /**
     * Returns the content panel where form fields are added.
     * The panel uses {@link GridBagLayout} for structured form layout.
     *
     * @return the content panel
     */
    public JPanel getContentPanel() {
        return contentPanel;
    }

    /**
     * Adds a labeled form field to the content panel.
     * The label appears on the left and the field component fills the remaining width.
     *
     * @param labelText the label text for the field
     * @param field     the input component (e.g., JTextField, JComboBox)
     */
    public void addFormField(String labelText, JComponent field) {
        ThemeManager tm = ThemeManager.getInstance();

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(Constants.SPACING_SM / 2, 0, Constants.SPACING_SM / 2, Constants.SPACING_SM);
        gbc.anchor = GridBagConstraints.WEST;

        // Label
        JLabel label = new JLabel(labelText);
        label.setFont(tm.getBoldFont(13));
        label.setForeground(tm.getPrimaryText());
        gbc.gridx = 0;
        gbc.gridy = currentRow;
        gbc.weightx = 0;
        gbc.fill = GridBagConstraints.NONE;
        contentPanel.add(label, gbc);

        // Style the field
        styleFormField(field);

        // Field
        gbc.gridx = 1;
        gbc.gridy = currentRow;
        gbc.weightx = 1.0;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.insets = new Insets(Constants.SPACING_SM / 2, 0, Constants.SPACING_SM / 2, 0);
        contentPanel.add(field, gbc);

        currentRow++;
    }

    /**
     * Returns the button panel at the bottom of the dialog.
     * Add buttons (e.g., Save, Cancel) to this panel.
     *
     * @return the button panel
     */
    public JPanel getButtonPanel() {
        return buttonPanel;
    }

    /**
     * Applies theme-consistent styling to a form input component.
     *
     * @param field the component to style
     */
    private void styleFormField(JComponent field) {
        ThemeManager tm = ThemeManager.getInstance();
        field.setFont(tm.getFont(13));

        if (field instanceof JTextField) {
            JTextField tf = (JTextField) field;
            tf.setForeground(tm.getPrimaryText());
            tf.setBackground(tm.getSecondarySurface());
            tf.setCaretColor(tm.getPrimaryText());
            tf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(tm.getBorderColor(), 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        } else if (field instanceof JComboBox) {
            field.setForeground(tm.getPrimaryText());
            field.setBackground(tm.getSecondarySurface());
        } else if (field instanceof JTextArea) {
            JTextArea ta = (JTextArea) field;
            ta.setForeground(tm.getPrimaryText());
            ta.setBackground(tm.getSecondarySurface());
            ta.setCaretColor(tm.getPrimaryText());
            ta.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(tm.getBorderColor(), 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        } else if (field instanceof JPasswordField) {
            JPasswordField pf = (JPasswordField) field;
            pf.setForeground(tm.getPrimaryText());
            pf.setBackground(tm.getSecondarySurface());
            pf.setCaretColor(tm.getPrimaryText());
            pf.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(tm.getBorderColor(), 1),
                    BorderFactory.createEmptyBorder(8, 10, 8, 10)
            ));
        }
    }
}
