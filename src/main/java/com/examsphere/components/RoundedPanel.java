package com.examsphere.components;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.RoundRectangle2D;

/**
 * A custom JPanel that renders with rounded corners using Graphics2D.
 * <p>
 * This panel draws a filled rounded rectangle as its background,
 * providing a modern card-like appearance used throughout the application.
 * </p>
 *
 * @author ExamSphere
 * @version 1.0
 */
public class RoundedPanel extends JPanel {

    /** The corner radius in pixels. */
    protected int radius;

    /** The background fill color for the rounded rectangle. */
    protected Color bgColor;

    /**
     * Constructs a RoundedPanel with the specified corner radius and background color.
     *
     * @param radius  the corner arc radius in pixels
     * @param bgColor the fill color for the rounded rectangle background
     */
    public RoundedPanel(int radius, Color bgColor) {
        super();
        this.radius = radius;
        this.bgColor = bgColor;
        setOpaque(false);
        setLayout(new BorderLayout());
    }

    /**
     * Returns the current corner radius.
     *
     * @return the corner radius in pixels
     */
    public int getRadius() {
        return radius;
    }

    /**
     * Sets a new corner radius and repaints the panel.
     *
     * @param radius the new corner radius in pixels
     */
    public void setRadius(int radius) {
        this.radius = radius;
        repaint();
    }

    /**
     * Returns the current background fill color.
     *
     * @return the background color
     */
    public Color getBgColor() {
        return bgColor;
    }

    /**
     * Sets a new background fill color and repaints the panel.
     *
     * @param bgColor the new background color
     */
    public void setBgColor(Color bgColor) {
        this.bgColor = bgColor;
        repaint();
    }

    /**
     * Paints the rounded rectangle background with anti-aliasing enabled.
     * The rounded rectangle fills the entire component bounds minus one pixel
     * to avoid clipping at the edges.
     *
     * @param g the Graphics context
     */
    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        try {
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

            g2.setColor(bgColor);
            g2.fill(new RoundRectangle2D.Double(0, 0, getWidth() - 1, getHeight() - 1, radius, radius));
        } finally {
            g2.dispose();
        }

        super.paintComponent(g);
    }
}
