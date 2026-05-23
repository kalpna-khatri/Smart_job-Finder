package ui;

import model.User;
import util.CSVHelper;
import util.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class LoginScreen extends JFrame {

    public LoginScreen() {
        setTitle("Sign In — Smart Job Finder");
        setSize(920, 620);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());
        add(buildDecoPanel(), BorderLayout.WEST);
        add(buildForm(), BorderLayout.CENTER);
    }

    private JPanel buildDecoPanel() {
        JPanel p = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setPaint(new GradientPaint(0,0,Theme.SIDEBAR,getWidth(),getHeight(),new Color(20,20,40)));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(new Color(0,180,220,18));
                for(int x=25;x<getWidth();x+=35) for(int y=25;y<getHeight();y+=35) g2.fillOval(x-2,y-2,4,4);
                g2.setColor(new Color(0,220,255,12));
                g2.fillOval(-80,getHeight()/2-160,320,320);
                g2.setColor(Theme.NEON_CYAN);
                g2.setFont(new Font("Segoe UI",Font.BOLD,34));
                g2.drawString("\u25C8 JobFinder",45,80);
                g2.setColor(Theme.TEXT_SECONDARY);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,15));
                g2.drawString("Your AI-powered career",45,118);
                g2.drawString("companion.",45,140);
                g2.setPaint(new GradientPaint(0,0,Theme.NEON_CYAN,0,getHeight(),Theme.NEON_PURPLE));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(getWidth()-1,0,getWidth()-1,getHeight());
                g2.dispose();
            }
        };
        p.setPreferredSize(new Dimension(380, 0));
        return p;
    }

    private JPanel buildForm() {
        JPanel center = new JPanel(new GridBagLayout());
        center.setBackground(Theme.BACKGROUND);

        JPanel card = new JPanel();
        card.setBackground(Theme.CARD);
        card.setLayout(new BoxLayout(card, BoxLayout.Y_AXIS));
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER,1,true),
                new EmptyBorder(45,50,40,50)));
        card.setPreferredSize(new Dimension(400, 480));

        JLabel title = new JLabel("Sign In");
        title.setFont(new Font("Segoe UI", Font.BOLD, 28));
        title.setForeground(Theme.NEON_CYAN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Welcome back! Enter your credentials.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.TEXT_SECONDARY);
        sub.setBorder(new EmptyBorder(8,0,35,0));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField userF = styledField();
        JPasswordField passF = styledPassword();

        RoundedButton loginBtn = new RoundedButton("SIGN IN", Theme.NEON_CYAN);
        loginBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        loginBtn.setMaximumSize(new Dimension(300, 48));
        loginBtn.setPreferredSize(new Dimension(300, 48));

        JLabel backLink = new JLabel("\u2190 Back to Welcome");
        backLink.setForeground(Theme.NEON_PURPLE);
        backLink.setFont(Theme.FONT_SMALL);
        backLink.setAlignmentX(Component.LEFT_ALIGNMENT);
        backLink.setBorder(new EmptyBorder(22,0,0,0));
        backLink.setCursor(new Cursor(Cursor.HAND_CURSOR));
        backLink.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { new WelcomeScreen().setVisible(true); dispose(); }
            public void mouseEntered(MouseEvent e) { backLink.setForeground(Theme.NEON_CYAN); }
            public void mouseExited(MouseEvent e)  { backLink.setForeground(Theme.NEON_PURPLE); }
        });

        loginBtn.addActionListener(e -> {
            String u = userF.getText().trim();
            String p = new String(passF.getPassword());
            if (u.isEmpty() || p.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both username and password.", "Missing Fields", JOptionPane.WARNING_MESSAGE);
                return;
            }
            User user = CSVHelper.authenticate(u, p);
            if (user != null) { new Dashboard(user).setVisible(true); dispose(); }
            else JOptionPane.showMessageDialog(this, "Invalid credentials. Please try again.", "Error", JOptionPane.ERROR_MESSAGE);
        });

        card.add(title); card.add(sub);
        card.add(fieldLabel("Username")); card.add(userF);
        card.add(Box.createVerticalStrut(18));
        card.add(fieldLabel("Password")); card.add(passF);
        card.add(Box.createVerticalStrut(28));
        card.add(loginBtn); card.add(backLink);

        center.add(card);
        return center;
    }

    static JLabel fieldLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Theme.TEXT_LABEL);
        l.setFont(Theme.FONT_LABEL);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0,0,6,0));
        return l;
    }

    static JTextField styledField() {
        JTextField f = new JTextField();
        applyInputStyle(f);
        f.setMaximumSize(new Dimension(300, 44));
        return f;
    }

    static JPasswordField styledPassword() {
        JPasswordField f = new JPasswordField();
        applyInputStyle(f);
        f.setMaximumSize(new Dimension(300, 44));
        return f;
    }

    static void applyInputStyle(JComponent f) {
        ((JTextField)f).setBackground(Theme.INPUT_BG);
        ((JTextField)f).setForeground(Theme.TEXT_PRIMARY);
        ((JTextField)f).setCaretColor(Theme.NEON_CYAN);
        ((JTextField)f).setFont(Theme.FONT_BODY);
        ((JTextField)f).setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER,1,true),
                new EmptyBorder(10,14,10,14)));
        f.setAlignmentX(Component.LEFT_ALIGNMENT);
        f.addFocusListener(new FocusAdapter() {
            public void focusGained(FocusEvent e) {
                ((JTextField)f).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.NEON_CYAN,1,true),
                        new EmptyBorder(10,14,10,14)));
            }
            public void focusLost(FocusEvent e) {
                ((JTextField)f).setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.BORDER,1,true),
                        new EmptyBorder(10,14,10,14)));
            }
        });
    }
}
