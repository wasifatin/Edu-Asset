package com.examsphere.util;

/**
 * Global application constants for layout, sizing, styling, and file storage.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public final class Constants {

    private Constants() {
        // Prevent instantiation
    }

    // ─── Window Dimensions ──────────────────────────────────────────────
    public static final int WINDOW_WIDTH = 1440;
    public static final int WINDOW_HEIGHT = 900;
    public static final int MIN_WIDTH = 1100;
    public static final int MIN_HEIGHT = 700;

    // ─── Component Dimensions ───────────────────────────────────────────
    public static final int SIDEBAR_EXPANDED = 256;
    public static final int SIDEBAR_COLLAPSED = 84;
    public static final int TOPBAR_HEIGHT = 62;

    // ─── Radii and Geometry ─────────────────────────────────────────────
    public static final int CARD_RADIUS = 18;
    public static final int CONTROL_RADIUS = 12;
    public static final int DIALOG_RADIUS = 16;

    // ─── Spacing and Padding ────────────────────────────────────────────
    public static final int SPACING_SM = 12;
    public static final int SPACING = 16;
    public static final int SPACING_LG = 20;

    // ─── Storage Directory ──────────────────────────────────────────────
    public static final String DATA_DIR = "data";
    public static final String BACKUP_DIR = "backup";
}

