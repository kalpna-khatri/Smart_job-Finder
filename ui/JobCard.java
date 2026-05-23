package ui;

import model.Job;
import model.User;
import util.CSVHelper;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class JobCard extends JPanel {

    private boolean hovered = false;

    public JobCard(Job job, User user) {
        setLayout(new BorderLayout(18, 0));
        setBackground(Theme.CARD);
        setMaximumSize(new Dimension(Integer.MAX_VALUE, 150));
        setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(18, 22, 18, 22)));
        setOpaque(false);

        addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) { hovered = true;  repaint(); }
            @Override public void mouseExited(MouseEvent e)  { hovered = false; repaint(); }
        });

        // ===== LEFT: Job info =====
        JPanel info = new JPanel();
        info.setOpaque(false);
        info.setLayout(new BoxLayout(info, BoxLayout.Y_AXIS));

        JLabel title = new JLabel(job.getTitle());
        title.setFont(new Font("Segoe UI", Font.BOLD, 16));
        title.setForeground(Theme.TEXT_PRIMARY);

        JLabel company = new JLabel("  " + job.getCompany() + "   \u2022   \uD83D\uDCCD " + job.getLocation());
        company.setFont(Theme.FONT_BODY);
        company.setForeground(Theme.TEXT_SECONDARY);
        company.setBorder(new EmptyBorder(5, 0, 0, 0));

        JLabel salary = new JLabel("\u2713 " + job.getSalary());
        salary.setFont(new Font("Segoe UI", Font.BOLD, 13));
        salary.setForeground(Theme.NEON_GREEN);
        salary.setBorder(new EmptyBorder(6, 0, 0, 0));

        // Skills as chips
        JPanel skillsPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        skillsPanel.setOpaque(false);
        skillsPanel.setBorder(new EmptyBorder(8, 0, 0, 0));
        for (String skill : job.getRequiredSkills().split(",")) {
            String s = skill.trim();
            if (!s.isEmpty()) skillsPanel.add(skillChip(s));
        }

        info.add(title);
        info.add(company);
        info.add(salary);
        info.add(skillsPanel);

        // ===== RIGHT: Buttons =====
        JPanel right = new JPanel();
        right.setOpaque(false);
        right.setLayout(new BoxLayout(right, BoxLayout.Y_AXIS));
        right.setAlignmentY(Component.CENTER_ALIGNMENT);

        boolean alreadyApplied = CSVHelper.hasApplied(user.getUsername(), job);
        String applyLabel = alreadyApplied ? "Applied \u2713" : "Apply Now";
        Color  applyColor = alreadyApplied ? Theme.NEON_GREEN : Theme.NEON_CYAN;

        RoundedButton applyBtn = new RoundedButton(applyLabel, applyColor);
        applyBtn.setPreferredSize(new Dimension(130, 36));
        applyBtn.setMaximumSize(new Dimension(130, 36));
        applyBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        applyBtn.addActionListener(e -> {
            Window parent = SwingUtilities.getWindowAncestor(JobCard.this);
            ApplicationForm form = new ApplicationForm(parent, job, user);
            form.setVisible(true);
            boolean nowApplied = CSVHelper.hasApplied(user.getUsername(), job);
            if (nowApplied) {
                applyBtn.setText("Applied \u2713");
                applyBtn.repaint();
            }
        });

        boolean alreadySaved = CSVHelper.isJobSaved(user.getUsername(), job);
        RoundedButton saveBtn = new RoundedButton(alreadySaved ? "Saved \u2713" : "Save Job", Theme.NEON_PURPLE);
        saveBtn.setLightText(true);
        saveBtn.setPreferredSize(new Dimension(130, 36));
        saveBtn.setMaximumSize(new Dimension(130, 36));
        saveBtn.setAlignmentX(Component.RIGHT_ALIGNMENT);
        saveBtn.addActionListener(e -> {
            if (CSVHelper.isJobSaved(user.getUsername(), job)) {
                JOptionPane.showMessageDialog(this, "This job is already in your saved list.", "Already Saved", JOptionPane.INFORMATION_MESSAGE);
            } else {
                CSVHelper.saveJob(user.getUsername(), job);
                saveBtn.setText("Saved \u2713");
                saveBtn.repaint();
            }
        });

        right.add(applyBtn);
        right.add(Box.createVerticalStrut(10));
        right.add(saveBtn);

        add(info, BorderLayout.CENTER);
        add(right, BorderLayout.EAST);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(hovered ? Theme.CARD_HOVER : Theme.CARD);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 12, 12);
        if (hovered) {
            g2.setColor(new Color(0, 200, 230, 60));
            g2.setStroke(new BasicStroke(1.5f));
            g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 12, 12);
        }
        g2.dispose();
    }

    private JLabel skillChip(String text) {
        JLabel chip = new JLabel(text) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(0, 180, 220, 25));
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 8, 8);
                g2.dispose();
                super.paintComponent(g);
            }
        };
        chip.setFont(Theme.FONT_CAPTION);
        chip.setForeground(new Color(0, 200, 240));
        chip.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(0, 180, 220, 60), 1, true),
                new EmptyBorder(2, 8, 2, 8)));
        chip.setOpaque(false);
        return chip;
    }
}
