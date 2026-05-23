package ui;

import model.Job;
import util.EligibilityAnalyzer;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.geom.Arc2D;
import java.util.List;

public class EligibilityResultDialog extends JDialog {

    private final EligibilityAnalyzer.Result result;
    private final Job job;
    private int animatedScore = 0;

    public EligibilityResultDialog(Window parent, Job job, EligibilityAnalyzer.Result result) {
        super(parent, "AI Eligibility Analysis", ModalityType.APPLICATION_MODAL);
        this.job    = job;
        this.result = result;

        setSize(880, 780);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(),   BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        animateScore();
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0,0,Theme.SIDEBAR,getWidth(),0,new Color(20,20,38)));
                g2.fillRect(0,0,getWidth(),getHeight());
                // Bottom accent line
                g2.setPaint(new GradientPaint(0,0,Theme.NEON_GREEN,getWidth(),0,Theme.NEON_CYAN));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(0,getHeight()-1,getWidth(),getHeight()-1);
                g2.dispose();
            }
        };
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(22,30,22,30));

        JLabel t = new JLabel("\uD83E\uDD16  AI Eligibility Analysis");
        t.setFont(Theme.FONT_TITLE);
        t.setForeground(Theme.NEON_GREEN);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel s = new JLabel("How you match against: " + job.getTitle() + " at " + job.getCompany());
        s.setFont(Theme.FONT_BODY);
        s.setForeground(Theme.TEXT_SECONDARY);
        s.setBorder(new EmptyBorder(6,0,0,0));
        s.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(t);
        header.add(s);
        return header;
    }

    private JScrollPane buildBody() {
        JPanel body = new JPanel();
        body.setBackground(Theme.BACKGROUND);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(24, 30, 24, 30));

        // ── Score Card ──
        body.add(buildScoreCard());
        body.add(Box.createVerticalStrut(16));

        // ── Score Breakdown ──
        body.add(buildScoreBreakdown());
        body.add(Box.createVerticalStrut(16));

        // ── Sections ──
        if (!result.strengths.isEmpty()) {
            body.add(buildSection("\u2B50  Strong Matching Areas", result.strengths, Theme.NEON_GREEN));
            body.add(Box.createVerticalStrut(12));
        }
        if (!result.matchedSkills.isEmpty()) {
            body.add(buildSection("\u2714  Skills You Already Match", result.matchedSkills, Theme.NEON_CYAN));
            body.add(Box.createVerticalStrut(12));
        }
        if (!result.missingSkills.isEmpty()) {
            body.add(buildSection("\u26A0  Missing Required Skills", result.missingSkills, Theme.NEON_PINK));
            body.add(Box.createVerticalStrut(12));
        }
        if (!result.qualificationGaps.isEmpty()) {
            body.add(buildSection("\uD83D\uDCDA  Qualification Gaps", result.qualificationGaps, Theme.NEON_ORANGE));
            body.add(Box.createVerticalStrut(12));
        }
        if (!result.recommendedSkills.isEmpty()) {
            body.add(buildSection("\uD83D\uDCE6  Recommended Skills & Certifications", result.recommendedSkills, Theme.NEON_PURPLE));
            body.add(Box.createVerticalStrut(12));
        }
        if (!result.suggestions.isEmpty()) {
            body.add(buildSection("\uD83D\uDCA1  Improvement Suggestions", result.suggestions, Theme.NEON_CYAN));
            body.add(Box.createVerticalStrut(12));
        }
        if (!result.aiRecommendations.isEmpty()) {
            body.add(buildSection("\uD83E\uDD16  AI Recommendations", result.aiRecommendations, Theme.NEON_GREEN));
            body.add(Box.createVerticalStrut(12));
        }

        JScrollPane sp = new JScrollPane(body);
        sp.setBorder(null);
        sp.setBackground(Theme.BACKGROUND);
        sp.getViewport().setBackground(Theme.BACKGROUND);
        sp.getVerticalScrollBar().setUnitIncrement(18);
        return sp;
    }

    private JPanel buildScoreCard() {
        JPanel card = new JPanel(new BorderLayout(30, 0));
        card.setBackground(Theme.CARD);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(28, 30, 28, 30)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 210));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        CircularScore circle = new CircularScore();
        circle.setPreferredSize(new Dimension(170, 170));
        card.add(circle, BorderLayout.WEST);

        JPanel rt = new JPanel();
        rt.setOpaque(false);
        rt.setLayout(new BoxLayout(rt, BoxLayout.Y_AXIS));

        JLabel eligPct = new JLabel("Eligibility: " + result.score + "%");
        eligPct.setFont(new Font("Segoe UI", Font.BOLD, 24));
        eligPct.setForeground(scoreColor(result.score));
        eligPct.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel verdict = new JLabel("<html><body style='width:400px'>" + result.verdict + "</body></html>");
        verdict.setFont(Theme.FONT_BODY);
        verdict.setForeground(Theme.TEXT_SECONDARY);
        verdict.setBorder(new EmptyBorder(10,0,0,0));
        verdict.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel expCompat = new JLabel("\uD83D\uDCAC  Experience: " + result.experienceCompatibility);
        expCompat.setFont(Theme.FONT_SMALL);
        expCompat.setForeground(Theme.TEXT_MUTED);
        expCompat.setBorder(new EmptyBorder(14,0,4,0));
        expCompat.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel qualStatus = new JLabel("\uD83C\uDF93  Qualification: " + result.qualificationStatus);
        qualStatus.setFont(Theme.FONT_SMALL);
        qualStatus.setForeground(Theme.TEXT_MUTED);
        qualStatus.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel matchStat = new JLabel(
                "\u2714 " + result.matchedSkills.size() + " skills matched   \u2022   "
                + "\u2718 " + result.missingSkills.size() + " skills missing");
        matchStat.setFont(Theme.FONT_SMALL);
        matchStat.setForeground(Theme.TEXT_MUTED);
        matchStat.setBorder(new EmptyBorder(8,0,0,0));
        matchStat.setAlignmentX(Component.LEFT_ALIGNMENT);

        rt.add(eligPct);
        rt.add(verdict);
        rt.add(expCompat);
        rt.add(qualStatus);
        rt.add(matchStat);
        card.add(rt, BorderLayout.CENTER);
        return card;
    }

    private JPanel buildScoreBreakdown() {
        JPanel card = new JPanel();
        card.setBackground(Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(20, 26, 20, 26)));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        card.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel("\uD83D\uDCCA  Score Breakdown");
        heading.setFont(Theme.FONT_HEADING);
        heading.setForeground(Theme.TEXT_PRIMARY);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setBorder(new EmptyBorder(0,0,16,0));
        card.add(heading);

        card.add(progressRow("Skills Match",        result.skillMatchPercent, Theme.NEON_CYAN, 50));
        card.add(Box.createVerticalStrut(10));
        card.add(progressRow("Work Experience",     result.experienceScore,   Theme.NEON_PURPLE, 20));
        card.add(Box.createVerticalStrut(10));
        card.add(progressRow("Education",           result.educationScore,    Theme.NEON_GREEN, 15));
        card.add(Box.createVerticalStrut(10));
        card.add(progressRow("Certifications & Extras", result.extrasScore,  Theme.NEON_ORANGE, 15));
        return card;
    }

    private JPanel progressRow(String label, int pct, Color color, int weight) {
        JPanel row = new JPanel(new BorderLayout(12, 0));
        row.setOpaque(false);
        row.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label);
        l.setFont(Theme.FONT_SMALL);
        l.setForeground(Theme.TEXT_SECONDARY);
        l.setPreferredSize(new Dimension(200, 18));

        JPanel barBg = new JPanel(null) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BORDER);
                g2.fillRoundRect(0,3,getWidth(),10,6,6);
                int fill = (int)(getWidth() * pct / 100.0);
                g2.setPaint(new GradientPaint(0,0,color.darker(),fill,0,color));
                g2.fillRoundRect(0,3,fill,10,6,6);
                g2.dispose();
            }
        };
        barBg.setOpaque(false);
        barBg.setPreferredSize(new Dimension(300, 18));

        JLabel pctLabel = new JLabel(pct + "%  (" + weight + "% weight)");
        pctLabel.setFont(Theme.FONT_CAPTION);
        pctLabel.setForeground(color);
        pctLabel.setPreferredSize(new Dimension(130, 18));

        row.add(l, BorderLayout.WEST);
        row.add(barBg, BorderLayout.CENTER);
        row.add(pctLabel, BorderLayout.EAST);
        return row;
    }

    private JPanel buildSection(String title, List<String> items, Color accent) {
        JPanel p = new JPanel();
        p.setBackground(Theme.CARD);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        new Color(accent.getRed(), accent.getGreen(), accent.getBlue(), 80), 1, true),
                new EmptyBorder(18, 22, 18, 22)));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.setMaximumSize(new Dimension(Integer.MAX_VALUE, items.size() * 30 + 65));

        JLabel t = new JLabel(title);
        t.setFont(Theme.FONT_HEADING);
        t.setForeground(accent);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        t.setBorder(new EmptyBorder(0,0,12,0));
        p.add(t);

        for (String item : items) {
            JLabel l = new JLabel("  \u2022  " + item);
            l.setFont(Theme.FONT_BODY);
            l.setForeground(Theme.TEXT_PRIMARY);
            l.setAlignmentX(Component.LEFT_ALIGNMENT);
            l.setBorder(new EmptyBorder(3,6,3,0));
            p.add(l);
        }
        return p;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 12, 14));
        footer.setBackground(Theme.SIDEBAR);
        footer.setBorder(new EmptyBorder(8,20,8,20));

        RoundedButton close = new RoundedButton("Close", Theme.NEON_CYAN);
        close.setPreferredSize(new Dimension(140, 44));
        close.addActionListener(e -> dispose());
        footer.add(close);
        return footer;
    }

    private Color scoreColor(int s) {
        if (s >= 70) return Theme.NEON_GREEN;
        if (s >= 45) return Theme.NEON_CYAN;
        return Theme.NEON_PINK;
    }

    private void animateScore() {
        Timer t = new Timer(12, null);
        t.addActionListener(e -> {
            if (animatedScore < result.score) { animatedScore++; repaint(); }
            else t.stop();
        });
        t.start();
    }

    private class CircularScore extends JPanel {
        public CircularScore() { setOpaque(false); }

        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            int w = getWidth(), h = getHeight();
            int size = Math.min(w, h) - 24;
            int x = (w-size)/2, y = (h-size)/2;
            int stroke = 16;

            // Track
            g2.setStroke(new BasicStroke(stroke, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.setColor(Theme.BORDER);
            g2.drawArc(x, y, size, size, 0, 360);

            // Progress
            Color sc = scoreColor(result.score);
            g2.setColor(sc);
            float angle = (animatedScore / 100f) * 360f;
            g2.draw(new Arc2D.Float(x, y, size, size, 90, -angle, Arc2D.OPEN));

            // Glow
            g2.setColor(new Color(sc.getRed(), sc.getGreen(), sc.getBlue(), 30));
            g2.setStroke(new BasicStroke(stroke + 8, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new Arc2D.Float(x-2, y-2, size+4, size+4, 90, -angle, Arc2D.OPEN));

            // Center text
            String pctTxt = animatedScore + "%";
            g2.setFont(new Font("Segoe UI", Font.BOLD, 34));
            g2.setColor(Theme.TEXT_PRIMARY);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(pctTxt, (w-fm.stringWidth(pctTxt))/2, (h+fm.getAscent()-fm.getDescent())/2 - 6);

            g2.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            g2.setColor(Theme.TEXT_MUTED);
            String sub2 = "ELIGIBLE";
            g2.drawString(sub2, (w-g2.getFontMetrics().stringWidth(sub2))/2, (h+fm.getAscent()-fm.getDescent())/2 + 14);

            g2.dispose();
        }
    }
}
