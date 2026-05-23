package ui;

import util.Theme;
import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WelcomeScreen extends JFrame {

    public WelcomeScreen() {
        setTitle("Smart Job Finder");
        setSize(960, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        add(buildLeft(), BorderLayout.WEST);
        add(buildRight(), BorderLayout.CENTER);
    }

    private JPanel buildLeft() {
        JPanel left = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                // Subtle gradient overlay
                GradientPaint gp = new GradientPaint(0, 0, Theme.SIDEBAR,
                        getWidth(), getHeight(), new Color(20, 20, 36));
                g2.setPaint(gp);
                g2.fillRect(0, 0, getWidth(), getHeight());
                // Decorative grid dots
                g2.setColor(new Color(0, 180, 220, 20));
                for (int x = 20; x < getWidth(); x += 30) {
                    for (int y = 20; y < getHeight(); y += 30) {
                        g2.fillOval(x - 1, y - 1, 3, 3);
                    }
                }
                // Accent line on right
                GradientPaint line = new GradientPaint(0, 0, Theme.NEON_CYAN,
                        0, getHeight(), Theme.NEON_PURPLE);
                g2.setPaint(line);
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(getWidth() - 1, 0, getWidth() - 1, getHeight());
                g2.dispose();
            }
        };
        left.setPreferredSize(new Dimension(460, 0));
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.setBorder(BorderFactory.createEmptyBorder(70, 55, 55, 50));

        // Logo
        JLabel logoIcon = new JLabel("◈");
        logoIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 32));
        logoIcon.setForeground(Theme.NEON_CYAN);
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logo = new JLabel("JobFinder");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 38));
        logo.setForeground(Theme.TEXT_PRIMARY);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel tagline = new JLabel("<html><body style='width:310px; color:#a0a0c0'>Find your dream job.<br>Smarter. Faster. Better.</body></html>");
        tagline.setFont(new Font("Segoe UI", Font.PLAIN, 19));
        tagline.setBorder(BorderFactory.createEmptyBorder(18, 0, 45, 0));
        tagline.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Feature bullets
        String[] features = {
            "◆  AI-powered skill matching & analysis",
            "◆  Personalized job recommendations",
            "◆  Professional eligibility scoring",
            "◆  Real-time application tracking"
        };
        for (String f : features) {
            JLabel feat = new JLabel(f);
            feat.setFont(Theme.FONT_BODY);
            feat.setForeground(Theme.TEXT_SECONDARY);
            feat.setAlignmentX(Component.LEFT_ALIGNMENT);
            feat.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            left.add(feat.getBorder() != null ? feat : feat);
        }

        left.add(logoIcon);
        left.add(Box.createVerticalStrut(4));
        left.add(logo);
        left.add(tagline);
        for (String f : features) {
            JLabel feat = new JLabel(f);
            feat.setFont(Theme.FONT_BODY);
            feat.setForeground(Theme.TEXT_SECONDARY);
            feat.setAlignmentX(Component.LEFT_ALIGNMENT);
            feat.setBorder(BorderFactory.createEmptyBorder(0, 0, 12, 0));
            left.add(feat);
        }
        // Hack: rebuild cleanly
        left.removeAll();
        left.add(logoIcon);
        left.add(Box.createVerticalStrut(4));
        left.add(logo);
        left.add(tagline);
        for (String f : features) {
            JLabel feat = new JLabel(f);
            feat.setFont(Theme.FONT_BODY);
            feat.setForeground(Theme.TEXT_SECONDARY);
            feat.setAlignmentX(Component.LEFT_ALIGNMENT);
            feat.setBorder(BorderFactory.createEmptyBorder(0, 0, 13, 0));
            left.add(feat);
        }
        left.add(Box.createVerticalGlue());

        JLabel version = new JLabel("v2.0  •  Professional Edition");
        version.setFont(Theme.FONT_CAPTION);
        version.setForeground(Theme.TEXT_MUTED);
        version.setAlignmentX(Component.LEFT_ALIGNMENT);
        left.add(version);

        return left;
    }

    private JPanel buildRight() {
        JPanel right = new JPanel(new GridBagLayout());
        right.setBackground(Theme.BACKGROUND);

        JPanel card = new JPanel();
        card.setBackground(Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                BorderFactory.createEmptyBorder(45, 50, 45, 50)));
        card.setPreferredSize(new Dimension(360, 400));

        JLabel welcome = new JLabel("Welcome Back");
        welcome.setFont(Theme.FONT_TITLE);
        welcome.setForeground(Theme.TEXT_PRIMARY);
        welcome.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel sub = new JLabel("Your career journey starts here.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.TEXT_SECONDARY);
        sub.setAlignmentX(Component.CENTER_ALIGNMENT);
        sub.setBorder(BorderFactory.createEmptyBorder(8, 0, 40, 0));

        RoundedButton loginBtn = new RoundedButton("SIGN IN", Theme.NEON_CYAN);
        loginBtn.setPreferredSize(new Dimension(260, 50));
        loginBtn.setMaximumSize(new Dimension(260, 50));
        loginBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        loginBtn.addActionListener(e -> { new LoginScreen().setVisible(true); dispose(); });

        JLabel orLabel = new JLabel("— or —");
        orLabel.setFont(Theme.FONT_SMALL);
        orLabel.setForeground(Theme.TEXT_MUTED);
        orLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        orLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));

        RoundedButton regBtn = new RoundedButton("CREATE ACCOUNT", Theme.NEON_PURPLE);
        regBtn.setLightText(true);
        regBtn.setPreferredSize(new Dimension(260, 50));
        regBtn.setMaximumSize(new Dimension(260, 50));
        regBtn.setAlignmentX(Component.CENTER_ALIGNMENT);
        regBtn.addActionListener(e -> { new RegisterScreen().setVisible(true); dispose(); });

        card.add(welcome);
        card.add(sub);
        card.add(loginBtn);
        card.add(orLabel);
        card.add(regBtn);

        right.add(card);
        return right;
    }
}
