package com.examsphere.components;

import com.examsphere.util.Constants;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A statistics display card with a colored accent bar, title, large value, and subtitle.
 * <p>
 * StatCard extends {@link RoundedPanel} to provide a visually distinct card component
 * commonly used on dashboard screens to highlight key metrics. It features:
 * <ul>
 *   <li>A 4-pixel colored accent bar at the top</li>
 *   <li>A title label in small secondary text (12pt)</li>
 *   <li>A large bold value label (28pt)</li>
 *   <li>A subtitle label in small secondary text (11pt)</li>
 * </ul>
 * The card automatically responds to theme changes by re-reading colors from
 * {@link ThemeManager}.
 * </p>
 *
 * @author ExamSphere
 * @version 1.0
 */
public class StatCard extends RoundedPanel {

    /** The accent color used for the top bar. */
    private Color accentColor;

    /** Label displaying the card title. */
    private final JLabel titleLabel;

    /** Label displaying the main value. */
    private final JLabel valueLabel;

    /** Label displaying the subtitle/description. */
    private final JLabel subtitleLabel;

    /**
     * Constructs a StatCard with the specified title, value, subtitle, and accent color.
     *
     * @param title       the descriptive title (e.g., "Total Students")
     * @param value       the main metric value (e.g., "1,234")
     * @param subtitle    additional context (e.g., "+12 this month")
     * @param accentColor the color for the top accent bar
     */
    public StatCard(String title, String value, String subtitle, Color accentColor) {
        super(Constants.CARD_RADIUS, ThemeManager.getInstance().getMainSurface());
        this.accentColor = accentColor;

        ThemeManager tm = ThemeManager.getInstance();

        setLayout(new BorderLayout());
        setBorder(BorderFactory.createEmptyBorder(
                Constants.SPACING + 4, Constants.SPACING, Constants.SPACING, Constants.SPACING));

        // Content panel with vertical box layout
        JPanel contentPanel = new JPanel();
        contentPanel.setOpaque(false);
        contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));

        // Title label — small secondary text
        titleLabel = new JLabel(title);
        titleLabel.setFont(tm.getFont(12));
        titleLabel.setForeground(tm.getSecondaryText());
        titleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Value label — large bold
        valueLabel = new JLabel(value);
        valueLabel.setFont(tm.getBoldFont(28));
        valueLabel.setForeground(tm.getPrimaryText());
        valueLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Subtitle label — small secondary text
        subtitleLabel = new JLabel(subtitle);
        subtitleLabel.setFont(tm.getFont(11));
        subtitleLabel.setForeground(tm.getSecondaryText());
        subtitleLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        contentPanel.add(titleLabel);
        contentPanel.add(Box.createVerticalStrut(Constants.SPACING_SM / 2));
        contentPanel.add(valueLabel);
        contentPanel.add(Box.createVerticalStrut(Constants.SPACING_SM / 3));
        contentPanel.add(subtitleLabel);

        add(contentPanel, BorderLayout.CENTER);

        // Register for theme changes
        tm.addThemeChangeListener(this::applyTheme);
    }

    /**
     * Updates the displayed value text.
     *
     * @param value the new value string to display
     */
    public void updateValue(String value) {
        valueLabel.setText(value);
    }

    /**
     * Updates the displayed subtitle text.
     *
     * @param subtitle the new subtitle string to display
     */
    public void updateSubtitle(String subtitle) {
        subtitleLabel.setText(subtitle);
    }

    /**
     * Returns the current accent color.
     *
     * @return the accent color
     */
    public Color getAccentColor() {
        return accentColor;
    }

    /**
     * Sets a new accent color and repaints.
     *
     * @param accentColor the new accent color
     */
    public void setAccentColor(Color accentColor) {
        this.accentColor = accentColor;
        repaint();
    }

    /**
     * Applies the current theme colors to all child components.
     * Called automatically when the application theme changes.
     */
    private void applyTheme() {
        ThemeManager tm = ThemeManager.getInstance();
        setBgColor(tm.getMainSurface());
        titleLabel.setFont(tm.getFont(12));
        titleLabel.setForeground(tm.getSecondaryText());
        valueLabel.setFont(tm.getBoldFont(28));
        valueLabel.setForeground(tm.getPrimaryText());
        subtitleLabel.setFont(tm.getFont(11));
        subtitleLabel.setForeground(tm.getSecondaryText());
        repaint();
    }

    /**
     * Paints the card background with the colored accent bar at the top.
     * The accent bar is rendered as a 4-pixel strip at the top of the rounded rectangle,
     * clipped to the card's rounded shape.
     *
     * @param g the Graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            int w = getWidth() - 1;
            int h = getHeight() - 1;
            RoundRectangle2D roundedRect = new RoundRectangle2D.Double(0, 0, w, h, radius, radius);

            // Fill card background
            g2.setColor(bgColor);
            g2.fill(roundedRect);

            // Draw accent bar at top, clipped to rounded shape
            Shape oldClip = g2.getClip();
            g2.setClip(roundedRect);
            g2.setColor(accentColor);
            g2.fillRect(0, 0, w + 1, 4);
            g2.setClip(oldClip);
        } finally {
            g2.dispose();
        }

        // Paint children (skip RoundedPanel's super paintComponent to avoid double-painting bg)
        paintChildren(g);
    }
}
