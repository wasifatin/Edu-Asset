package com.examsphere.components;

import com.examsphere.util.Constants;
import com.examsphere.util.ThemeManager;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.RoundRectangle2D;

/**
 * A custom-styled JButton with rounded corners, themed colors, and hover/press effects.
 * <p>
 * CustomButton renders a filled rounded rectangle as its background instead of
 * using the default Swing button painting. It supports:
 * <ul>
 *   <li>Custom background and text colors</li>
 *   <li>Hover effect (lightens the background)</li>
 *   <li>Press effect (darkens the background)</li>
 *   <li>Hand cursor on hover</li>
 * </ul>
 * </p>
 *
 * @author ExamSphere
 * @version 1.0
 */
public class CustomButton extends JButton {

    /** The base background color of the button. */
    private Color bgColor;

    /** The text color of the button. */
    private Color textColor;

    /** Whether the mouse is currently hovering over the button. */
    private boolean hovering = false;

    /** Whether the mouse button is currently pressed on this button. */
    private boolean pressing = false;

    /**
     * Constructs a CustomButton with default theme primary accent background and white text.
     *
     * @param text the button label text
     */
    public CustomButton(String text) {
        this(text, ThemeManager.getInstance().getPrimaryAccent(), Color.WHITE);
    }

    /**
     * Constructs a CustomButton with the specified text, background color, and text color.
     *
     * @param text      the button label text
     * @param bgColor   the background fill color
     * @param textColor the label text color
     */
    public CustomButton(String text, Color bgColor, Color textColor) {
        super(text);
        this.bgColor = bgColor;
        this.textColor = textColor;

        setContentAreaFilled(false);
        setBorderPainted(false);
        setOpaque(false);
        setFocusPainted(false);
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setForeground(textColor);
        setFont(ThemeManager.getInstance().getBoldFont(13));
        setBorder(BorderFactory.createEmptyBorder(
                Constants.SPACING_SM, Constants.SPACING_LG, Constants.SPACING_SM, Constants.SPACING_LG));

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                hovering = true;
                repaint();
            }

            @Override
            public void mouseExited(MouseEvent e) {
                hovering = false;
                pressing = false;
                repaint();
            }

            @Override
            public void mousePressed(MouseEvent e) {
                pressing = true;
                repaint();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                pressing = false;
                repaint();
            }
        });
    }

    /**
     * Returns the current background color of the button.
     *
     * @return the background color
     */
    public Color getBgColor() {
        return bgColor;
    }

    /**
     * Sets the background color and repaints.
     *
     * @param bgColor the new background color
     */
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        repaint();
    }

    /**
     * Returns the current text color of the button.
     *
     * @return the text color
     */
    public Color getTextColor() {
        return textColor;
    }

    /**
     * Sets the text color and repaints.
     *
     * @param textColor the new text color
     */
    public void setTextColor(Color textColor) {
        this.textColor = textColor;
        setForeground(textColor);
        repaint();
    }

    /**
     * Computes the effective background color based on hover/press state.
     * <ul>
     *   <li>Press: darkens the base color by 30</li>
     *   <li>Hover: brightens the base color by 25</li>
     *   <li>Normal: base color</li>
     * </ul>
     *
     * @return the computed effective color
     */
    private Color getEffectiveColor() {
        if (pressing) {
            return darken(bgColor, 30);
        } else if (hovering) {
            return brighten(bgColor, 25);
        }
        return bgColor;
    }

    /**
     * Brightens a color by the given amount, clamping to 255.
     *
     * @param c      the source color
     * @param amount the amount to add to each channel
     * @return the brightened color
     */
    private static Color brighten(Color c, int amount) {
        return new Color(
                Math.min(255, c.getRed() + amount),
                Math.min(255, c.getGreen() + amount),
                Math.min(255, c.getBlue() + amount)
        );
    }

    /**
     * Darkens a color by the given amount, clamping to 0.
     *
     * @param c      the source color
     * @param amount the amount to subtract from each channel
     * @return the darkened color
     */
    private static Color darken(Color c, int amount) {
        return new Color(
                Math.max(0, c.getRed() - amount),
                Math.max(0, c.getGreen() - amount),
                Math.max(0, c.getBlue() - amount)
        );
    }

    /**
     * Paints the button as a filled rounded rectangle with the effective color,
     * then draws the text label centered within the button bounds.
     *
     * @param g the Graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            // Draw rounded rectangle background
            g2.setColor(getEffectiveColor());
            g2.fill(new RoundRectangle2D.Double(
                    0, 0, getWidth() - 1, getHeight() - 1,
                    Constants.CONTROL_RADIUS, Constants.CONTROL_RADIUS));

            // Draw text
            g2.setColor(textColor);
            g2.setFont(getFont());
            FontMetrics fm = g2.getFontMetrics();
            String text = getText();
            int textX = (getWidth() - fm.stringWidth(text)) / 2;
            int textY = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
            g2.drawString(text, textX, textY);
        } finally {
            g2.dispose();
        }
    }
}
