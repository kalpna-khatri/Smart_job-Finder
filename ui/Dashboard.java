package ui;

import model.Job;
import model.User;
import util.CSVHelper;
import util.JobDatabase;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.stream.Collectors;

public class Dashboard extends JFrame {

    private final User user;
    private JPanel contentArea;
    private JTextField searchField;
    private JLabel activeMenuLabel = null;

    public Dashboard(User user) {
        this.user = user;
        setTitle("Smart Job Finder — Dashboard");
        setSize(1250, 780);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());
        add(buildSidebar(), BorderLayout.WEST);
        add(buildMainArea(), BorderLayout.CENTER);
        showDashboard();
    }

    private JPanel buildSidebar() {
        JPanel sidebar = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0,0,Theme.SIDEBAR,0,getHeight(),new Color(14,14,22)));
                g2.fillRect(0,0,getWidth(),getHeight());
                // Right border accent
                g2.setPaint(new GradientPaint(0,0,Theme.NEON_CYAN,0,getHeight(),Theme.NEON_PURPLE));
                g2.setStroke(new BasicStroke(1.5f));
                g2.drawLine(getWidth()-1,0,getWidth()-1,getHeight());
                g2.dispose();
            }
        };
        sidebar.setPreferredSize(new Dimension(255, 0));
        sidebar.setLayout(new BoxLayout(sidebar, BoxLayout.Y_AXIS));
        sidebar.setBorder(new EmptyBorder(30, 0, 30, 0));

        // Logo
        JPanel logoPanel = new JPanel();
        logoPanel.setOpaque(false);
        logoPanel.setLayout(new BoxLayout(logoPanel, BoxLayout.Y_AXIS));
        logoPanel.setBorder(new EmptyBorder(0, 24, 36, 24));
        logoPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logoIcon = new JLabel("\u25C8");
        logoIcon.setFont(new Font("Segoe UI Symbol", Font.PLAIN, 20));
        logoIcon.setForeground(Theme.NEON_CYAN);
        logoIcon.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel logo = new JLabel("JobFinder");
        logo.setFont(new Font("Segoe UI", Font.BOLD, 22));
        logo.setForeground(Theme.TEXT_PRIMARY);
        logo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel edition = new JLabel("Professional Edition");
        edition.setFont(Theme.FONT_CAPTION);
        edition.setForeground(Theme.TEXT_MUTED);
        edition.setAlignmentX(Component.LEFT_ALIGNMENT);

        logoPanel.add(logoIcon);
        logoPanel.add(Box.createVerticalStrut(2));
        logoPanel.add(logo);
        logoPanel.add(Box.createVerticalStrut(2));
        logoPanel.add(edition);
        sidebar.add(logoPanel);

        // Nav label
        JLabel navLabel = new JLabel("NAVIGATION");
        navLabel.setFont(new Font("Segoe UI", Font.BOLD, 10));
        navLabel.setForeground(Theme.TEXT_MUTED);
        navLabel.setBorder(new EmptyBorder(0, 24, 10, 0));
        navLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(navLabel);

        sidebar.add(menuItem("\uD83D\uDCCA  Dashboard", this::showDashboard, true));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(menuItem("\uD83D\uDCCC  Saved Jobs", this::showSavedJobs, false));
        sidebar.add(Box.createVerticalStrut(4));
        sidebar.add(menuItem("\uD83D\uDC64  My Profile", this::showProfile, false));
        sidebar.add(Box.createVerticalGlue());

        // User info at bottom
        JPanel userInfo = new JPanel();
        userInfo.setOpaque(false);
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));
        userInfo.setBorder(new EmptyBorder(0, 24, 0, 24));
        userInfo.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel uName = new JLabel(user.getUsername());
        uName.setFont(new Font("Segoe UI", Font.BOLD, 14));
        uName.setForeground(Theme.TEXT_PRIMARY);
        uName.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel uEmail = new JLabel(user.getEmail());
        uEmail.setFont(Theme.FONT_CAPTION);
        uEmail.setForeground(Theme.TEXT_MUTED);
        uEmail.setAlignmentX(Component.LEFT_ALIGNMENT);

        userInfo.add(uName);
        userInfo.add(Box.createVerticalStrut(2));
        userInfo.add(uEmail);
        userInfo.add(Box.createVerticalStrut(14));

        sidebar.add(userInfo);
        sidebar.add(menuItem("\uD83D\uDEAA  Logout", this::logout, false));

        return sidebar;
    }

    private JPanel menuItem(String text, Runnable action, boolean selected) {
        JPanel[] wrapper = {null};
        JLabel[] label = {null};
        boolean[] isSelected = {selected};

        JPanel item = new JPanel(new BorderLayout()) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                if (isSelected[0]) {
                    g2.setColor(new Color(0, 180, 220, 20));
                    g2.fillRoundRect(8, 2, getWidth()-16, getHeight()-4, 8, 8);
                    g2.setColor(Theme.NEON_CYAN);
                    g2.setStroke(new BasicStroke(2.5f));
                    g2.drawLine(8, 2, 8, getHeight()-2);
                }
                g2.dispose();
                super.paintComponent(g);
            }
        };
        item.setOpaque(false);
        item.setMaximumSize(new Dimension(255, 44));
        item.setBorder(new EmptyBorder(8, 24, 8, 24));
        item.setCursor(new Cursor(Cursor.HAND_CURSOR));

        JLabel l = new JLabel(text);
        l.setForeground(selected ? Theme.NEON_CYAN : Theme.TEXT_SECONDARY);
        l.setFont(selected ? new Font("Segoe UI", Font.BOLD, 14) : Theme.FONT_BODY);
        item.add(l, BorderLayout.WEST);
        wrapper[0] = item;
        label[0] = l;

        item.addMouseListener(new MouseAdapter() {
            @Override public void mouseEntered(MouseEvent e) {
                if (!isSelected[0]) { item.setOpaque(true); item.setBackground(new Color(28, 28, 42)); l.setForeground(Theme.TEXT_PRIMARY); item.repaint(); }
            }
            @Override public void mouseExited(MouseEvent e) {
                if (!isSelected[0]) { item.setOpaque(false); l.setForeground(Theme.TEXT_SECONDARY); item.repaint(); }
            }
            @Override public void mouseClicked(MouseEvent e) {
                action.run();
            }
        });
        return item;
    }

    private JPanel buildMainArea() {
        JPanel main = new JPanel(new BorderLayout());
        main.setBackground(Theme.BACKGROUND);
        main.setBorder(new EmptyBorder(28, 32, 28, 32));

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout(20, 0));
        topBar.setOpaque(false);
        topBar.setBorder(new EmptyBorder(0, 0, 24, 0));

        JPanel greet = new JPanel();
        greet.setOpaque(false);
        greet.setLayout(new BoxLayout(greet, BoxLayout.Y_AXIS));

        JLabel hi = new JLabel("Hello, " + user.getUsername() + " \uD83D\uDC4B");
        hi.setFont(Theme.FONT_TITLE);
        hi.setForeground(Theme.TEXT_PRIMARY);

        JLabel tip = new JLabel("Find your next opportunity — " + JobDatabase.getAllJobs().size() + " jobs available");
        tip.setFont(Theme.FONT_BODY);
        tip.setForeground(Theme.TEXT_SECONDARY);
        tip.setBorder(new EmptyBorder(4, 0, 0, 0));

        greet.add(hi);
        greet.add(tip);

        // Search bar
        JPanel searchPanel = new JPanel(new BorderLayout(10, 0));
        searchPanel.setOpaque(false);
        searchPanel.setPreferredSize(new Dimension(350, 46));

        searchField = new JTextField();
        searchField.setFont(Theme.FONT_BODY);
        searchField.setBackground(Theme.CARD);
        searchField.setForeground(Theme.TEXT_PRIMARY);
        searchField.setCaretColor(Theme.NEON_CYAN);
        searchField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(10, 14, 10, 14)));

        RoundedButton searchBtn = new RoundedButton("Search", Theme.NEON_CYAN);
        searchBtn.setPreferredSize(new Dimension(100, 46));
        searchBtn.addActionListener(e -> performSearch());
        searchField.addActionListener(e -> performSearch());

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchBtn, BorderLayout.EAST);

        topBar.add(greet, BorderLayout.WEST);
        topBar.add(searchPanel, BorderLayout.EAST);

        // Content area
        contentArea = new JPanel();
        contentArea.setBackground(Theme.BACKGROUND);
        contentArea.setLayout(new BoxLayout(contentArea, BoxLayout.Y_AXIS));

        JScrollPane scroll = new JScrollPane(contentArea);
        scroll.setBorder(null);
        scroll.setBackground(Theme.BACKGROUND);
        scroll.getViewport().setBackground(Theme.BACKGROUND);
        scroll.getVerticalScrollBar().setUnitIncrement(18);

        main.add(topBar, BorderLayout.NORTH);
        main.add(scroll, BorderLayout.CENTER);
        return main;
    }

    private void showDashboard() {
        contentArea.removeAll();
        addSection("\uD83D\uDCBC  Recommended For You");
        for (Job j : JobDatabase.getAllJobs()) addCard(j);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void showSavedJobs() {
        contentArea.removeAll();
        addSection("\uD83D\uDCCC  Your Saved Jobs");
        List<Job> saved = CSVHelper.loadSavedJobs(user.getUsername());
        if (saved.isEmpty()) contentArea.add(emptyMessage("You haven't saved any jobs yet. Browse jobs and click Save Job."));
        else for (Job j : saved) addCard(j);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void showProfile() {
        contentArea.removeAll();
        addSection("\uD83D\uDC64  My Profile");

        JPanel profile = new JPanel();
        profile.setBackground(Theme.CARD);
        profile.setLayout(new BoxLayout(profile, BoxLayout.Y_AXIS));
        profile.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(28, 28, 28, 28)));
        profile.setMaximumSize(new Dimension(Integer.MAX_VALUE, 260));
        profile.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel heading = new JLabel("Account Information");
        heading.setFont(Theme.FONT_HEADING);
        heading.setForeground(Theme.NEON_CYAN);
        heading.setAlignmentX(Component.LEFT_ALIGNMENT);
        heading.setBorder(new EmptyBorder(0, 0, 20, 0));
        profile.add(heading);

        profile.add(profileRow("Username",       user.getUsername(), Theme.NEON_CYAN));   profile.add(Box.createVerticalStrut(14));
        profile.add(profileRow("Email Address",  user.getEmail(),    Theme.NEON_PURPLE)); profile.add(Box.createVerticalStrut(14));
        profile.add(profileRow("Skills",         user.getSkills(),   Theme.NEON_GREEN));

        contentArea.add(profile);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private JPanel profileRow(String label, String value, Color accent) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(label.toUpperCase());
        l.setForeground(Theme.TEXT_MUTED);
        l.setFont(new Font("Segoe UI", Font.BOLD, 10));

        JLabel v = new JLabel(value.isEmpty() ? "—" : value);
        v.setForeground(accent);
        v.setFont(Theme.FONT_HEADING);

        JPanel left = new JPanel();
        left.setOpaque(false);
        left.setLayout(new BoxLayout(left, BoxLayout.Y_AXIS));
        left.add(l);
        left.add(Box.createVerticalStrut(3));
        left.add(v);
        p.add(left, BorderLayout.WEST);
        return p;
    }

    private void logout() {
        int c = JOptionPane.showConfirmDialog(this,
                "Are you sure you want to logout?", "Logout",
                JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (c == JOptionPane.YES_OPTION) { new WelcomeScreen().setVisible(true); dispose(); }
    }

    private void performSearch() {
        String q = searchField.getText().trim().toLowerCase();
        if (q.isEmpty()) { showDashboard(); return; }
        contentArea.removeAll();
        addSection("\uD83D\uDD0D  Results for: \"" + q + "\"");
        List<Job> results = JobDatabase.getAllJobs().stream()
                .filter(j -> j.getTitle().toLowerCase().contains(q)
                          || j.getCompany().toLowerCase().contains(q)
                          || j.getLocation().toLowerCase().contains(q)
                          || j.getRequiredSkills().toLowerCase().contains(q))
                .collect(Collectors.toList());
        if (results.isEmpty()) contentArea.add(emptyMessage("No jobs found for \"" + q + "\". Try different keywords."));
        else for (Job j : results) addCard(j);
        contentArea.revalidate();
        contentArea.repaint();
    }

    private void addSection(String text) {
        JPanel row = new JPanel(new BorderLayout());
        row.setOpaque(false);
        row.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        row.setAlignmentX(Component.LEFT_ALIGNMENT);
        row.setBorder(new EmptyBorder(0, 2, 16, 0));

        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEADING);
        l.setForeground(Theme.TEXT_PRIMARY);
        row.add(l, BorderLayout.WEST);

        // Decorative line
        JPanel line = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0,1,Theme.BORDER,getWidth(),1,new Color(0,0,0,0)));
                g2.setStroke(new BasicStroke(1f));
                g2.drawLine(0, getHeight()/2, getWidth(), getHeight()/2);
                g2.dispose();
            }
        };
        line.setOpaque(false);
        row.add(line, BorderLayout.CENTER);

        contentArea.add(row);
    }

    private void addCard(Job j) {
        JobCard card = new JobCard(j, user);
        card.setAlignmentX(Component.LEFT_ALIGNMENT);
        contentArea.add(card);
        contentArea.add(Box.createVerticalStrut(10));
    }

    private JLabel emptyMessage(String msg) {
        JLabel l = new JLabel(msg);
        l.setForeground(Theme.TEXT_MUTED);
        l.setFont(Theme.FONT_BODY);
        l.setBorder(new EmptyBorder(20, 4, 20, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }
}
