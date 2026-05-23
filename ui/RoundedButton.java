package ui;

import util.Theme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RoundedButton extends JButton {

    private final Color baseColor;
    private boolean lightText = false;
    private boolean hovered = false;
    private boolean pressed = false;
    private boolean ghost = false;

    public RoundedButton(String text, Color color) {
        super(text);
        this.baseColor = color;
        setOpaque(false);
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setFont(Theme.FONT_BUTTON);
        setForeground(isDark(color) ? Color.WHITE : Theme.BACKGROUND);
        setCursor(new Cursor(Cursor.HAND_CURSOR));

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e)  { hovered = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)   { hovered = false; pressed = false; repaint(); }
            @Override public void mousePressed(MouseEvent e)  { pressed = true;  repaint(); }
            @Override public void mouseReleased(MouseEvent e) { pressed = false; repaint(); }
        });
    }

    public void setLightText(boolean b) { this.lightText = b; setForeground(Color.WHITE); }
    public void setGhost(boolean g) { this.ghost = g; }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int w = getWidth(), h = getHeight();
        int arc = 10;

        if (ghost) {
            // Ghost/outline style
            g2.setColor(hovered ? baseColor.darker() : new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 30));
            g2.fillRoundRect(0, 0, w, h, arc, arc);
            g2.setColor(baseColor);
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, w - 1, h - 1, arc, arc);
        } else {
            // Filled style
            Color fill = pressed ? baseColor.darker().darker()
                    : hovered ? baseColor.brighter()
                    : baseColor;
            g2.setColor(fill);
            g2.fillRoundRect(0, 0, w, h, arc, arc);

            // Subtle top highlight
            if (!pressed) {
                g2.setColor(new Color(255, 255, 255, 25));
                g2.fillRoundRect(1, 1, w - 2, h / 2, arc, arc);
            }
        }

        // Glow effect on hover
        if (hovered && !ghost) {
            g2.setColor(new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 60));
            g2.setStroke(new BasicStroke(4f));
            g2.drawRoundRect(-2, -2, w + 3, h + 3, arc + 4, arc + 4);
        }

        g2.dispose();
        super.paintComponent(g);
    }

    private boolean isDark(Color c) {
        double lum = 0.299 * c.getRed() + 0.587 * c.getGreen() + 0.114 * c.getBlue();
        return lum < 150;
    }
}
