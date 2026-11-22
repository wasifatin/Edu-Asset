package com.examsphere.util;

import java.awt.Color;
import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

/**
 * Singleton theme manager providing centralized Light and Dark theme palettes,
 * typography management, and observer notification for runtime theme switching.
 *
 * @author EduAsset Team
 * @version 1.0
 */
public class ThemeManager {

    private static ThemeManager instance;

    private boolean darkTheme = false;
    private final List<Runnable> listeners = new ArrayList<>();

    // ─── Light Palette (Nimbra Reference) ──────────────────────────────
    private static final Color LIGHT_BACKGROUND       = new Color(0xE9, 0xEE, 0xF8); // #E9EEF8
    private static final Color LIGHT_MAIN_SURFACE     = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF
    private static final Color LIGHT_SECONDARY_SURFACE= new Color(0xF5, 0xF7, 0xFB); // #F5F7FB
    private static final Color LIGHT_PRIMARY_TEXT     = new Color(0x0D, 0x16, 0x30); // #0D1630
    private static final Color LIGHT_SECONDARY_TEXT   = new Color(0x55, 0x61, 0x7C); // #55617C
    private static final Color LIGHT_PRIMARY_ACCENT   = new Color(0x2F, 0x5C, 0xF0); // #2F5CF0
    private static final Color LIGHT_SUCCESS          = new Color(0x0E, 0x9A, 0x78); // #0E9A78
    private static final Color LIGHT_WARNING          = new Color(0xC2, 0x7C, 0x0E); // #C27C0E
    private static final Color LIGHT_DANGER           = new Color(0xBE, 0x26, 0x39); // #BE2639
    private static final Color LIGHT_INFO             = new Color(0x15, 0x63, 0xB5); // #1563B5
    private static final Color LIGHT_BORDER           = new Color(0xD6, 0xDE, 0xEE); // #D6DEEE
    private static final Color LIGHT_SIDEBAR_BG       = new Color(0xFF, 0xFF, 0xFF); // #FFFFFF

    // ─── Dark Palette (Nimbra Reference) ───────────────────────────────
    private static final Color DARK_BACKGROUND        = new Color(0x05, 0x0A, 0x18); // #050A18
    private static final Color DARK_MAIN_SURFACE      = new Color(0x10, 0x1A, 0x36); // #101A36
    private static final Color DARK_SECONDARY_SURFACE = new Color(0x0B, 0x13, 0x2A); // #0B132A
    private static final Color DARK_PRIMARY_TEXT      = new Color(0xEE, 0xF3, 0xFF); // #EEF3FF
    private static final Color DARK_SECONDARY_TEXT    = new Color(0x95, 0xA2, 0xC2); // #95A2C2
    private static final Color DARK_PRIMARY_ACCENT    = new Color(0x3D, 0x6B, 0xFF); // #3D6BFF
    private static final Color DARK_SUCCESS           = new Color(0x4F, 0xE0, 0xA0); // #4FE0A0
    private static final Color DARK_WARNING           = new Color(0xFB, 0xC3, 0x5E); // #FBC35E
    private static final Color DARK_DANGER            = new Color(0xFF, 0x7D, 0x8D); // #FF7D8D
    private static final Color DARK_INFO              = new Color(0x38, 0x8B, 0xEA); // #388BEA
    private static final Color DARK_BORDER            = new Color(0x1B, 0x29, 0x4D); // #1B294D
    private static final Color DARK_SIDEBAR_BG        = new Color(0x0B, 0x13, 0x2A); // #0B132A

    private ThemeManager() {
    }

    public static synchronized ThemeManager getInstance() {
        if (instance == null) {
            instance = new ThemeManager();
        }
        return instance;
    }

    public void toggleTheme() {
        this.darkTheme = !this.darkTheme;
        notifyThemeChanged();
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean dark) {
        this.darkTheme = dark;
        notifyThemeChanged();
    }

    public Color getBackground() {
        return darkTheme ? DARK_BACKGROUND : LIGHT_BACKGROUND;
    }

    public Color getMainSurface() {
        return darkTheme ? DARK_MAIN_SURFACE : LIGHT_MAIN_SURFACE;
    }

    public Color getSecondarySurface() {
        return darkTheme ? DARK_SECONDARY_SURFACE : LIGHT_SECONDARY_SURFACE;
    }

    public Color getPrimaryText() {
        return darkTheme ? DARK_PRIMARY_TEXT : LIGHT_PRIMARY_TEXT;
    }

    public Color getSecondaryText() {
        return darkTheme ? DARK_SECONDARY_TEXT : LIGHT_SECONDARY_TEXT;
    }

    public Color getPrimaryAccent() {
        return darkTheme ? DARK_PRIMARY_ACCENT : LIGHT_PRIMARY_ACCENT;
    }

    public Color getSuccess() {
        return darkTheme ? DARK_SUCCESS : LIGHT_SUCCESS;
    }

    public Color getWarning() {
        return darkTheme ? DARK_WARNING : LIGHT_WARNING;
    }

    public Color getDanger() {
        return darkTheme ? DARK_DANGER : LIGHT_DANGER;
    }

    public Color getInfo() {
        return darkTheme ? DARK_INFO : LIGHT_INFO;
    }

    public Color getBorderColor() {
        return darkTheme ? DARK_BORDER : LIGHT_BORDER;
    }

    public Color getSidebarBg() {
        return darkTheme ? DARK_SIDEBAR_BG : LIGHT_SIDEBAR_BG;
    }

    public Font getFont(int size) {
        return new Font("Segoe UI", Font.PLAIN, size);
    }

    public Font getBoldFont(int size) {
        return new Font("Segoe UI", Font.BOLD, size);
    }

    public void addThemeChangeListener(Runnable listener) {
        if (listener != null && !listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeThemeChangeListener(Runnable listener) {
        listeners.remove(listener);
    }

    public void notifyThemeChanged() {
        for (Runnable listener : new ArrayList<>(listeners)) {
            try {
                listener.run();
            } catch (Exception ignored) {
            }
        }
    }
}

