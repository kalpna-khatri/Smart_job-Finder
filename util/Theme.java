package util;

import java.awt.*;

public class Theme {
    // Dark theme color palette
    public static final Color BACKGROUND     = new Color(12, 12, 18);
    public static final Color SIDEBAR        = new Color(18, 18, 28);
    public static final Color CARD           = new Color(24, 24, 36);
    public static final Color CARD_HOVER     = new Color(32, 32, 48);
    public static final Color BORDER         = new Color(42, 42, 60);
    public static final Color BORDER_FOCUS   = new Color(0, 200, 230);
    public static final Color INPUT_BG       = new Color(16, 16, 26);

    // Neon accents
    public static final Color NEON_CYAN      = new Color(0, 220, 255);
    public static final Color NEON_PURPLE    = new Color(140, 80, 255);
    public static final Color NEON_GREEN     = new Color(50, 240, 140);
    public static final Color NEON_PINK      = new Color(255, 80, 150);
    public static final Color NEON_ORANGE    = new Color(255, 160, 50);

    // Status
    public static final Color STATUS_SUCCESS = new Color(50, 240, 140);
    public static final Color STATUS_WARNING = new Color(255, 190, 50);
    public static final Color STATUS_ERROR   = new Color(255, 80, 100);
    public static final Color STATUS_INFO    = new Color(0, 180, 255);

    // Text
    public static final Color TEXT_PRIMARY   = new Color(240, 240, 252);
    public static final Color TEXT_SECONDARY = new Color(160, 160, 185);
    public static final Color TEXT_MUTED     = new Color(100, 100, 125);
    public static final Color TEXT_LABEL     = new Color(130, 130, 160);

    // Fonts
    public static final Font FONT_TITLE   = new Font("Segoe UI", Font.BOLD, 26);
    public static final Font FONT_HEADING = new Font("Segoe UI", Font.BOLD, 17);
    public static final Font FONT_BODY    = new Font("Segoe UI", Font.PLAIN, 14);
    public static final Font FONT_SMALL   = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_LABEL   = new Font("Segoe UI", Font.BOLD, 12);
    public static final Font FONT_BUTTON  = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_CAPTION = new Font("Segoe UI", Font.PLAIN, 11);

    /** Returns a gradient accent from cyan to purple */
    public static GradientPaint accentGradient(int x1, int y1, int x2, int y2) {
        return new GradientPaint(x1, y1, NEON_CYAN, x2, y2, NEON_PURPLE);
    }
}
