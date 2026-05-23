package ui;

import model.User;
import util.CSVHelper;
import util.Theme;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class RegisterScreen extends JFrame {

    public RegisterScreen() {
        setTitle("Create Account — Smart Job Finder");
        setSize(960, 680);
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
                g2.setPaint(new GradientPaint(0,0,Theme.SIDEBAR,getWidth(),getHeight(),new Color(22,18,40)));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.setColor(new Color(140,80,255,15));
                for(int x=25;x<getWidth();x+=35) for(int y=25;y<getHeight();y+=35) g2.fillOval(x-2,y-2,4,4);
                g2.setColor(new Color(140,80,255,10));
                g2.fillOval(-80,getHeight()/2-160,320,320);
                g2.setColor(Theme.NEON_PURPLE);
                g2.setFont(new Font("Segoe UI",Font.BOLD,34));
                g2.drawString("\u25C8 JobFinder",45,80);
                g2.setColor(Theme.TEXT_SECONDARY);
                g2.setFont(new Font("Segoe UI",Font.PLAIN,15));
                g2.drawString("Join thousands of professionals",45,118);
                g2.drawString("finding their dream jobs.",45,140);
                g2.setPaint(new GradientPaint(0,0,Theme.NEON_PURPLE,0,getHeight(),Theme.NEON_CYAN));
                g2.setStroke(new BasicStroke(2f));
                g2.drawLine(getWidth()-1,0,getWidth()-1,getHeight());
                g2.dispose();
            }
        };
        p.setPreferredSize(new Dimension(360, 0));
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
                new EmptyBorder(38,50,38,50)));
        card.setPreferredSize(new Dimension(420, 580));

        JLabel title = new JLabel("Create Account");
        title.setFont(new Font("Segoe UI",Font.BOLD,26));
        title.setForeground(Theme.NEON_PURPLE);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel("Join JobFinder and accelerate your career.");
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.TEXT_SECONDARY);
        sub.setBorder(new EmptyBorder(8,0,30,0));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextField userF  = LoginScreen.styledField();
        JTextField emailF = LoginScreen.styledField();
        JPasswordField passF = LoginScreen.styledPassword();
        JTextField skillsF = LoginScreen.styledField();

        RoundedButton regBtn = new RoundedButton("CREATE ACCOUNT", Theme.NEON_PURPLE);
        regBtn.setLightText(true);
        regBtn.setAlignmentX(Component.LEFT_ALIGNMENT);
        regBtn.setMaximumSize(new Dimension(320, 48));
        regBtn.setPreferredSize(new Dimension(320, 48));

        JLabel signIn = new JLabel("Already have an account? Sign In \u2192");
        signIn.setForeground(Theme.NEON_CYAN);
        signIn.setFont(Theme.FONT_SMALL);
        signIn.setAlignmentX(Component.LEFT_ALIGNMENT);
        signIn.setBorder(new EmptyBorder(20,0,0,0));
        signIn.setCursor(new Cursor(Cursor.HAND_CURSOR));
        signIn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) { new LoginScreen().setVisible(true); dispose(); }
            public void mouseEntered(MouseEvent e) { signIn.setForeground(Theme.NEON_PURPLE); }
            public void mouseExited(MouseEvent e)  { signIn.setForeground(Theme.NEON_CYAN); }
        });

        regBtn.addActionListener(e -> {
            String u = userF.getText().trim();
            String em = emailF.getText().trim();
            String p = new String(passF.getPassword());
            String s = skillsF.getText().trim();
            if (u.isEmpty()||em.isEmpty()||p.isEmpty()||s.isEmpty()) {
                JOptionPane.showMessageDialog(this,"Please fill all fields.","Missing Fields",JOptionPane.WARNING_MESSAGE);
                return;
            }
            if (CSVHelper.userExists(u)) {
                JOptionPane.showMessageDialog(this,"Username already taken. Please choose another.","Conflict",JOptionPane.WARNING_MESSAGE);
                return;
            }
            CSVHelper.saveUser(new User(u,em,p,s));
            JOptionPane.showMessageDialog(this,"Account created successfully! Please sign in.","Success",JOptionPane.INFORMATION_MESSAGE);
            new LoginScreen().setVisible(true);
            dispose();
        });

        card.add(title); card.add(sub);
        card.add(LoginScreen.fieldLabel("Username"));            card.add(userF);   card.add(Box.createVerticalStrut(14));
        card.add(LoginScreen.fieldLabel("Email Address"));       card.add(emailF);  card.add(Box.createVerticalStrut(14));
        card.add(LoginScreen.fieldLabel("Password"));            card.add(passF);   card.add(Box.createVerticalStrut(14));
        card.add(LoginScreen.fieldLabel("Skills (comma-separated)")); card.add(skillsF);
        card.add(Box.createVerticalStrut(28));
        card.add(regBtn); card.add(signIn);

        center.add(card);
        return center;
    }
}
