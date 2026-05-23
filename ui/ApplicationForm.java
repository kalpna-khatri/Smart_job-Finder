package ui;

import model.Job;
import model.User;
import util.CSVHelper;
import util.EligibilityAnalyzer;
import util.Theme;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class ApplicationForm extends JDialog {

    private final Job job;
    private final User user;
    private final boolean isReviewMode;

    // ─── Personal Information ───
    private JTextField fullNameF, emailF, phoneF, dobF, locationF, addressF, linkedinF, websiteF;
    private JComboBox<String> genderBox;
    private JPanel genderTogglePanel;
    private ButtonGroup genderBtnGroup;
    private java.util.Map<String, JToggleButton> genderButtons;

    // ─── Education ───
    private JTextField qualificationF, degreeTitleF, instituteF, gpaF;
    private JComboBox<String> passingYearBox;

    // ─── Professional Experience ───
    private JTextField companyF, jobRoleF, totalExperienceF, industryF;

    // ─── Skills ───
    private JTextArea technicalSkillsA, softSkillsA, toolsA;

    // ─── Additional ───
    private JTextArea certificationsA, languagesA, achievementsA;

    // ─── Resume ───
    private JLabel resumeLabel;
    private File resumeFile;

    // Step tracking
    private int currentStep = 0;
    private static final String[] STEP_TITLES = {
        "Personal Information",
        "Education",
        "Professional Experience",
        "Skills",
        "Additional Information"
    };
    private JPanel[] stepPanels;
    private JPanel stepIndicatorPanel;
    private JPanel formContainer;
    private RoundedButton nextBtn, prevBtn, submitBtn;
    private JLabel stepCountLabel;

    public ApplicationForm(Window parent, Job job, User user) {
        super(parent, "Apply for " + job.getTitle(), ModalityType.APPLICATION_MODAL);
        this.job = job;
        this.user = user;
        this.isReviewMode = CSVHelper.hasApplied(user.getUsername(), job);

        setSize(880, 760);
        setLocationRelativeTo(parent);
        getContentPane().setBackground(Theme.BACKGROUND);
        setLayout(new BorderLayout());

        initAllFields();
        buildStepPanels();

        add(buildHeader(), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
        add(buildFooter(), BorderLayout.SOUTH);

        if (isReviewMode) populateSavedData();
        showStep(0);
    }

    // ──────────────────────────────────────────
    // FIELD INITIALIZATION
    // ──────────────────────────────────────────
    private void initAllFields() {
        fullNameF        = textField(user.getUsername());
        emailF           = textField(user.getEmail());
        phoneF           = textField("");
        dobF             = textField("");
        locationF        = textField("");
        addressF         = textField("");
        linkedinF        = textField("");
        websiteF         = textField("");
        genderBox        = styledCombo(new String[]{"Select Gender","Male","Female","Non-Binary","Prefer not to say"});
        genderTogglePanel = buildGenderToggle();

        qualificationF   = textField("");
        degreeTitleF     = textField("");
        instituteF       = textField("");
        gpaF             = textField("");
        String[] years   = buildYearList();
        passingYearBox   = styledCombo(years);

        companyF         = textField("");
        jobRoleF         = textField("");
        totalExperienceF = textField("");
        industryF        = textField("");

        technicalSkillsA = textArea(3);
        technicalSkillsA.setText(user.getSkills());
        softSkillsA      = textArea(3);
        toolsA           = textArea(3);

        certificationsA  = textArea(3);
        languagesA       = textArea(2);
        achievementsA    = textArea(3);
    }

    private String[] buildYearList() {
        int startYear = 2015;
        int endYear   = 2030;
        String[] years = new String[endYear - startYear + 2];
        years[0] = "Select Year";
        for (int i = 1; i < years.length; i++) {
            years[i] = String.valueOf(endYear - i + 1);
        }
        return years;
    }

    private JPanel buildGenderToggle() {
        genderBtnGroup = new ButtonGroup();
        genderButtons  = new java.util.LinkedHashMap<>();
        String[] options = {"Male", "Female", "Non-Binary", "Prefer not to say"};

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        panel.setOpaque(false);
        panel.setAlignmentX(Component.LEFT_ALIGNMENT);

        for (String opt : options) {
            JToggleButton btn = new JToggleButton(opt) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (isSelected()) {
                        g2.setColor(Theme.NEON_CYAN);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                        g2.setColor(Theme.BACKGROUND);
                    } else {
                        g2.setColor(Theme.INPUT_BG);
                        g2.fillRoundRect(0, 0, getWidth(), getHeight(), 22, 22);
                        g2.setColor(Theme.BORDER);
                        g2.setStroke(new BasicStroke(1.2f));
                        g2.drawRoundRect(0, 0, getWidth()-1, getHeight()-1, 22, 22);
                        g2.setColor(Theme.TEXT_SECONDARY);
                    }
                    g2.setFont(new Font("Segoe UI", isSelected() ? Font.BOLD : Font.PLAIN, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    int tx = (getWidth()  - fm.stringWidth(getText())) / 2;
                    int ty = (getHeight() + fm.getAscent() - fm.getDescent()) / 2;
                    g2.drawString(getText(), tx, ty);
                    g2.dispose();
                }
            };
            btn.setPreferredSize(new Dimension(opt.length() > 7 ? 140 : 80, 36));
            btn.setBorderPainted(false);
            btn.setFocusPainted(false);
            btn.setContentAreaFilled(false);
            btn.setCursor(new java.awt.Cursor(java.awt.Cursor.HAND_CURSOR));
            genderBtnGroup.add(btn);
            genderButtons.put(opt, btn);
            panel.add(btn);
        }
        panel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        return panel;
    }
    // STEP PANELs
    private void buildStepPanels() {
        stepPanels = new JPanel[5];
        stepPanels[0] = buildPersonalStep();
        stepPanels[1] = buildEducationStep();
        stepPanels[2] = buildExperienceStep();
        stepPanels[3] = buildSkillsStep();
        stepPanels[4] = buildAdditionalStep();
    }

    private JPanel buildPersonalStep() {
        JPanel body = stepBody();
        body.add(sectionLabel("Personal Information"));
        body.add(Box.createVerticalStrut(18));

        body.add(twoCol(
            labeled("Full Name", fullNameF),
            labeled("Email Address", emailF)));
        body.add(Box.createVerticalStrut(16));
        body.add(twoCol(
            labeled("Phone Number", phoneF),
            labeled("Date of Birth (DD/MM/YYYY)", dobF)));
        body.add(Box.createVerticalStrut(16));
        body.add(twoCol(
            labeled("Gender", genderTogglePanel),
            labeled("Current Location", locationF)));
        body.add(Box.createVerticalStrut(16));
        body.add(fullRow(labeled("Full Address", addressF)));
        body.add(Box.createVerticalStrut(16));
        body.add(twoCol(
            labeled("LinkedIn Profile URL", linkedinF),
            labeled("Portfolio / GitHub URL", websiteF)));
        return body;
    }

    private JPanel buildEducationStep() {
        JPanel body = stepBody();
        body.add(sectionLabel("Educational Information"));
        body.add(Box.createVerticalStrut(18));

        body.add(twoCol(
            labeled("Highest Qualification", qualificationF),
            labeled("Degree Title", degreeTitleF)));
        body.add(Box.createVerticalStrut(16));
        body.add(fullRow(labeled("Institute / University Name", instituteF)));
        body.add(Box.createVerticalStrut(16));
        body.add(twoCol(
            labeled("Passing Year", passingYearBox),
            labeled("GPA / Grade", gpaF)));
        return body;
    }

    private JPanel buildExperienceStep() {
        JPanel body = stepBody();
        body.add(sectionLabel("Professional Experience"));
        body.add(Box.createVerticalStrut(18));

        body.add(twoCol(
            labeled("Current / Previous Company", companyF),
            labeled("Job Role / Position", jobRoleF)));
        body.add(Box.createVerticalStrut(16));
        body.add(twoCol(
            labeled("Total Experience (e.g. 3 Years)", totalExperienceF),
            labeled("Industry Type", industryF)));
        return body;
    }

    private JPanel buildSkillsStep() {
        JPanel body = stepBody();
        body.add(sectionLabel("Skills & Expertise"));
        body.add(Box.createVerticalStrut(18));

        body.add(fullRow(labeledArea("Technical Skills (comma-separated)", technicalSkillsA)));
        body.add(Box.createVerticalStrut(16));
        body.add(fullRow(labeledArea("Soft Skills (comma-separated)", softSkillsA)));
        body.add(Box.createVerticalStrut(16));
        body.add(fullRow(labeledArea("Tools & Technologies (comma-separated)", toolsA)));
        return body;
    }

    private JPanel buildAdditionalStep() {
        JPanel body = stepBody();
        body.add(sectionLabel("Additional Information"));
        body.add(Box.createVerticalStrut(18));

        body.add(fullRow(labeledArea("Certifications (e.g. AWS, Oracle Java SE)", certificationsA)));
        body.add(Box.createVerticalStrut(16));
        body.add(fullRow(labeledArea("Languages Known (e.g. English, Spanish)", languagesA)));
        body.add(Box.createVerticalStrut(16));
        body.add(fullRow(labeledArea("Achievements & Awards", achievementsA)));
        body.add(Box.createVerticalStrut(20));

        // Resume Upload
        JPanel resumeSection = new JPanel();
        resumeSection.setOpaque(false);
        resumeSection.setLayout(new BoxLayout(resumeSection, BoxLayout.Y_AXIS));
        resumeSection.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel resumeLabel2 = new JLabel("Resume / CV Upload");
        resumeLabel2.setFont(Theme.FONT_LABEL);
        resumeLabel2.setForeground(Theme.TEXT_LABEL);
        resumeLabel2.setAlignmentX(Component.LEFT_ALIGNMENT);
        resumeLabel2.setBorder(new EmptyBorder(0,0,8,0));

        JPanel uploadRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 0));
        uploadRow.setOpaque(false);
        uploadRow.setAlignmentX(Component.LEFT_ALIGNMENT);

        RoundedButton uploadBtn = new RoundedButton("\uD83D\uDCC4  Upload Resume (PDF/DOC)", Theme.NEON_PURPLE);
        uploadBtn.setLightText(true);
        uploadBtn.setPreferredSize(new Dimension(240, 42));
        uploadBtn.addActionListener(e -> chooseResume());

        resumeLabel = new JLabel("No file selected");
        resumeLabel.setForeground(Theme.TEXT_MUTED);
        resumeLabel.setFont(Theme.FONT_SMALL);

        uploadRow.add(uploadBtn);
        uploadRow.add(resumeLabel);

        resumeSection.add(resumeLabel2);
        resumeSection.add(uploadRow);
        body.add(resumeSection);
        return body;
    }

    // ──────────────────────────────────────────
    // UI STRUCTURE
    // ──────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel header = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setPaint(new GradientPaint(0,0,Theme.SIDEBAR,getWidth(),0,new Color(18,18,32)));
                g2.fillRect(0,0,getWidth(),getHeight());
                g2.dispose();
            }
        };
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.setBorder(new EmptyBorder(22, 30, 22, 30));

        String titleText = isReviewMode ? "\u270F\uFE0F  Review / Update Application" : "\uD83D\uDCC4  New Job Application";
        JLabel title = new JLabel(titleText);
        title.setFont(Theme.FONT_TITLE);
        title.setForeground(isReviewMode ? Theme.NEON_PURPLE : Theme.NEON_CYAN);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel sub = new JLabel(job.getTitle() + "   \u2022   " + job.getCompany() + "   \u2022   " + job.getLocation());
        sub.setFont(Theme.FONT_BODY);
        sub.setForeground(Theme.TEXT_SECONDARY);
        sub.setBorder(new EmptyBorder(6,0,0,0));
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(sub);

        if (isReviewMode) {
            JLabel badge = new JLabel("  \u2713 Previously applied — review and update your information below.");
            badge.setFont(Theme.FONT_SMALL);
            badge.setForeground(Theme.NEON_GREEN);
            badge.setBorder(new EmptyBorder(10,0,0,0));
            badge.setAlignmentX(Component.LEFT_ALIGNMENT);
            header.add(badge);
        }

        return header;
    }

    private JPanel buildBody() {
        JPanel body = new JPanel(new BorderLayout());
        body.setBackground(Theme.BACKGROUND);

        // Step indicator
        stepIndicatorPanel = buildStepIndicator();
        body.add(stepIndicatorPanel, BorderLayout.NORTH);

        // Form container (scrollable)
        formContainer = new JPanel(new CardLayout());
        formContainer.setBackground(Theme.BACKGROUND);

        for (int i = 0; i < stepPanels.length; i++) {
            JScrollPane sp = new JScrollPane(stepPanels[i]);
            sp.setBorder(null);
            sp.setBackground(Theme.BACKGROUND);
            sp.getViewport().setBackground(Theme.BACKGROUND);
            sp.getVerticalScrollBar().setUnitIncrement(18);
            formContainer.add(sp, "step" + i);
        }

        body.add(formContainer, BorderLayout.CENTER);
        return body;
    }

    private JPanel buildStepIndicator() {
        JPanel panel = new JPanel();
        panel.setBackground(Theme.SIDEBAR);
        panel.setLayout(new FlowLayout(FlowLayout.CENTER, 0, 14));
        panel.setBorder(new EmptyBorder(0, 24, 0, 24));

        for (int i = 0; i < STEP_TITLES.length; i++) {
            final int idx = i;
            // Step circle
            JLabel circle = new JLabel(String.valueOf(i + 1)) {
                @Override protected void paintComponent(Graphics g) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                    if (idx == currentStep) {
                        g2.setColor(Theme.NEON_CYAN);
                        g2.fillOval(0,0,26,26);
                        g2.setColor(Theme.BACKGROUND);
                    } else if (idx < currentStep) {
                        g2.setColor(Theme.NEON_GREEN);
                        g2.fillOval(0,0,26,26);
                        g2.setColor(Theme.BACKGROUND);
                    } else {
                        g2.setColor(Theme.BORDER);
                        g2.fillOval(0,0,26,26);
                        g2.setColor(Theme.TEXT_MUTED);
                    }
                    g2.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    FontMetrics fm = g2.getFontMetrics();
                    String txt = idx < currentStep ? "\u2713" : String.valueOf(idx+1);
                    g2.drawString(txt, (26-fm.stringWidth(txt))/2, (26+fm.getAscent()-fm.getDescent())/2);
                    g2.dispose();
                }
            };
            circle.setPreferredSize(new Dimension(26, 26));
            circle.setOpaque(false);
            panel.add(circle);

            // Step label
            JLabel lbl = new JLabel(STEP_TITLES[i]);
            lbl.setFont(i == currentStep ? new Font("Segoe UI", Font.BOLD, 11) : Theme.FONT_CAPTION);
            lbl.setForeground(i == currentStep ? Theme.NEON_CYAN : (i < currentStep ? Theme.NEON_GREEN : Theme.TEXT_MUTED));
            lbl.setBorder(new EmptyBorder(0, 6, 0, 0));
            panel.add(lbl);

            // Connector
            if (i < STEP_TITLES.length - 1) {
                JLabel conn = new JLabel("————");
                conn.setFont(Theme.FONT_CAPTION);
                conn.setForeground(i < currentStep ? Theme.NEON_GREEN : Theme.BORDER);
                conn.setBorder(new EmptyBorder(0, 8, 0, 8));
                panel.add(conn);
            }
        }

        // Step count label
        stepCountLabel = new JLabel("Step " + (currentStep+1) + " of " + STEP_TITLES.length);
        stepCountLabel.setFont(Theme.FONT_CAPTION);
        stepCountLabel.setForeground(Theme.TEXT_MUTED);
        panel.add(Box.createHorizontalStrut(20));
        panel.add(stepCountLabel);

        return panel;
    }

    private JPanel buildFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(Theme.SIDEBAR);
        footer.setBorder(new EmptyBorder(14, 28, 14, 28));

        // Left: Cancel
        RoundedButton cancelBtn = new RoundedButton("Cancel", Theme.BORDER);
        cancelBtn.setLightText(true);
        cancelBtn.setPreferredSize(new Dimension(110, 42));
        cancelBtn.addActionListener(e -> dispose());

        // Right: Prev / Next / Submit
        JPanel rightBtns = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        rightBtns.setOpaque(false);

        prevBtn = new RoundedButton("\u2190  Previous", Theme.BORDER);
        prevBtn.setLightText(true);
        prevBtn.setPreferredSize(new Dimension(130, 42));
        prevBtn.setEnabled(false);
        prevBtn.addActionListener(e -> { if (currentStep > 0) showStep(currentStep - 1); });

        nextBtn = new RoundedButton("Next  \u2192", Theme.NEON_CYAN);
        nextBtn.setPreferredSize(new Dimension(130, 42));
        nextBtn.addActionListener(e -> { if (currentStep < stepPanels.length - 1) showStep(currentStep + 1); });

        String submitLabel = isReviewMode ? "Update Application" : "Submit Application";
        Color submitColor  = isReviewMode ? Theme.NEON_PURPLE : Theme.NEON_GREEN;
        submitBtn = new RoundedButton(submitLabel, submitColor);
        submitBtn.setLightText(true);
        submitBtn.setPreferredSize(new Dimension(200, 42));
        submitBtn.setVisible(false);
        submitBtn.addActionListener(e -> submit());

        rightBtns.add(prevBtn);
        rightBtns.add(nextBtn);
        rightBtns.add(submitBtn);

        footer.add(cancelBtn, BorderLayout.WEST);
        footer.add(rightBtns, BorderLayout.EAST);
        return footer;
    }

    private void showStep(int step) {
        currentStep = step;
        CardLayout cl = (CardLayout) formContainer.getLayout();
        cl.show(formContainer, "step" + step);

        prevBtn.setEnabled(step > 0);
        nextBtn.setVisible(step < stepPanels.length - 1);
        submitBtn.setVisible(step == stepPanels.length - 1);

        // Rebuild step indicator
        Container parent = stepIndicatorPanel.getParent();
        if (parent != null) {
            int idx = -1;
            for (int i = 0; i < parent.getComponentCount(); i++) {
                if (parent.getComponent(i) == stepIndicatorPanel) { idx = i; break; }
            }
            parent.remove(stepIndicatorPanel);
            stepIndicatorPanel = buildStepIndicator();
            parent.add(stepIndicatorPanel, BorderLayout.NORTH);
            parent.revalidate();
            parent.repaint();
        }
    }

    // ──────────────────────────────────────────
    // POPULATE / SUBMIT
    // ──────────────────────────────────────────
    private void populateSavedData() {
        Map<String, String> data = CSVHelper.loadApplication(user.getUsername(), job);
        if (data == null) return;
        setField(fullNameF,        data.get("fullName"));
        setField(emailF,           data.get("email"));
        setField(phoneF,           data.get("phone"));
        setField(dobF,             data.get("dob"));
        setField(locationF,        data.get("location"));
        setField(addressF,         data.get("address"));
        setField(linkedinF,        data.get("linkedin"));
        setField(websiteF,         data.get("website"));
        setField(qualificationF,   data.get("highestQualification"));
        setField(degreeTitleF,     data.get("degreeTitle"));
        setField(instituteF,       data.get("institute"));
        setField(gpaF,             data.get("gpa"));
        setField(companyF,         data.get("company"));
        setField(jobRoleF,         data.get("jobRole"));
        setField(totalExperienceF, data.get("totalExperience"));
        setField(industryF,        data.get("industry"));
        setArea(technicalSkillsA,  data.get("skills"));
        setArea(softSkillsA,       data.get("softSkills"));
        setArea(toolsA,            data.get("tools"));
        setArea(certificationsA,   data.get("certifications"));
        setArea(languagesA,        data.get("languages"));
        setArea(achievementsA,     data.get("achievements"));

        String savedGender = data.get("gender");
        if (savedGender != null && genderButtons.containsKey(savedGender)) {
            genderButtons.get(savedGender).setSelected(true);
        }

        String resumeName = data.get("resume");
        if (resumeName != null && !resumeName.isEmpty()) {
            resumeLabel.setText("\u2713  " + resumeName + " (previously uploaded)");
            resumeLabel.setForeground(Theme.NEON_GREEN);
        }
    }

    private void chooseResume() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select Resume (PDF / DOC / DOCX)");
        if (chooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
            resumeFile = chooser.getSelectedFile();
            resumeLabel.setText("\u2713  " + resumeFile.getName());
            resumeLabel.setForeground(Theme.NEON_GREEN);
        }
    }

    private void submit() {
        // Validation
        if (fullNameF.getText().trim().isEmpty()) { showStep(0); warn("Full Name is required."); return; }
        if (emailF.getText().trim().isEmpty())    { showStep(0); warn("Email Address is required."); return; }
        if (phoneF.getText().trim().isEmpty())    { showStep(0); warn("Phone Number is required."); return; }
        if (technicalSkillsA.getText().trim().isEmpty()) { showStep(3); warn("Technical Skills are required."); return; }

        Map<String, String> data = new HashMap<>();
        data.put("fullName",            fullNameF.getText().trim());
        data.put("email",               emailF.getText().trim());
        data.put("phone",               phoneF.getText().trim());
        data.put("dob",                 dobF.getText().trim());
        data.put("gender",              getSelectedGender());
        data.put("location",            locationF.getText().trim());
        data.put("address",             addressF.getText().trim());
        data.put("linkedin",            linkedinF.getText().trim());
        data.put("website",             websiteF.getText().trim());
        data.put("highestQualification",qualificationF.getText().trim());
        data.put("degreeTitle",         degreeTitleF.getText().trim());
        data.put("institute",           instituteF.getText().trim());
        data.put("passingYear",         (String) passingYearBox.getSelectedItem());
        data.put("gpa",                 gpaF.getText().trim());
        data.put("company",             companyF.getText().trim());
        data.put("jobRole",             jobRoleF.getText().trim());
        data.put("totalExperience",     totalExperienceF.getText().trim());
        data.put("industry",            industryF.getText().trim());
        data.put("skills",              technicalSkillsA.getText().trim());
        data.put("softSkills",          softSkillsA.getText().trim());
        data.put("tools",               toolsA.getText().trim());
        data.put("certifications",      certificationsA.getText().trim());
        data.put("languages",           languagesA.getText().trim());
        data.put("achievements",        achievementsA.getText().trim());
        data.put("portfolio",           websiteF.getText().trim());

        if (resumeFile != null) {
            data.put("resume", resumeFile.getName());
        } else if (isReviewMode) {
            Map<String, String> old = CSVHelper.loadApplication(user.getUsername(), job);
            data.put("resume", old != null ? old.getOrDefault("resume", "") : "");
        } else {
            data.put("resume", "");
        }

        if (isReviewMode) {
            CSVHelper.updateApplication(user.getUsername(), job, data);
            showSuccessToast("Application updated successfully!");
            dispose();
        } else {
            CSVHelper.saveApplication(user.getUsername(), job, data);
            EligibilityAnalyzer.Result result = EligibilityAnalyzer.analyze(job, data);
            dispose();
            showApplicationSuccess();
            new EligibilityResultDialog((Window) getParent(), job, result).setVisible(true);
        }
    }

    private void showApplicationSuccess() {
        JDialog toast = new JDialog((Window) getParent(), "", ModalityType.MODELESS);
        toast.setUndecorated(true);
        toast.setSize(480, 130);
        toast.setLocationRelativeTo(getParent());

        JPanel panel = new JPanel() {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(20, 36, 28));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),16,16);
                g2.setColor(Theme.NEON_GREEN);
                g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,16,16);
                g2.dispose();
            }
        };
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(new EmptyBorder(20, 30, 20, 30));
        panel.setOpaque(false);

        JLabel icon = new JLabel("\u2714  Application Submitted Successfully!");
        icon.setFont(new Font("Segoe UI", Font.BOLD, 17));
        icon.setForeground(Theme.NEON_GREEN);
        icon.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel msg = new JLabel("The company will contact you via email. Please wait for their response.");
        msg.setFont(Theme.FONT_BODY);
        msg.setForeground(Theme.TEXT_SECONDARY);
        msg.setAlignmentX(Component.CENTER_ALIGNMENT);
        msg.setBorder(new EmptyBorder(8,0,0,0));

        panel.add(icon);
        panel.add(msg);

        toast.add(panel);
        toast.setVisible(true);

        Timer t = new Timer(3500, e -> toast.dispose());
        t.setRepeats(false);
        t.start();
    }

    private void showSuccessToast(String message) {
        JOptionPane.showMessageDialog(this, message, "Success", JOptionPane.INFORMATION_MESSAGE);
    }

    private void warn(String msg) {
        JOptionPane.showMessageDialog(this, msg, "Required Field", JOptionPane.WARNING_MESSAGE);
    }

    // ──────────────────────────────────────────
    // UI HELPERS
    // ──────────────────────────────────────────
    private JPanel stepBody() {
        JPanel p = new JPanel();
        p.setBackground(Theme.BACKGROUND);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(new EmptyBorder(28, 36, 28, 36));
        return p;
    }

    private JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(Theme.FONT_HEADING);
        l.setForeground(Theme.TEXT_PRIMARY);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        return l;
    }

    private JPanel labeled(String labelText, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(labelText);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.TEXT_LABEL);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 7, 0));

        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(field);
        return p;
    }

    private JPanel labeledArea(String labelText, JTextArea area) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel l = new JLabel(labelText);
        l.setFont(Theme.FONT_LABEL);
        l.setForeground(Theme.TEXT_LABEL);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(new EmptyBorder(0, 0, 7, 0));

        JScrollPane sp = new JScrollPane(area);
        sp.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1, true));
        sp.setAlignmentX(Component.LEFT_ALIGNMENT);
        sp.setMaximumSize(new Dimension(Integer.MAX_VALUE, area.getRows() * 22 + 20));
        sp.getViewport().setBackground(Theme.INPUT_BG);

        p.add(l);
        p.add(sp);
        return p;
    }

    private JPanel twoCol(JComponent left, JComponent right) {
        JPanel p = new JPanel(new GridLayout(1, 2, 18, 0));
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(left);
        p.add(right);
        return p;
    }

    private JPanel fullRow(JComponent comp) {
        JPanel p = new JPanel(new BorderLayout());
        p.setOpaque(false);
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(comp, BorderLayout.CENTER);
        return p;
    }

    private JTextField textField(String value) {
        JTextField f = new JTextField(value);
        f.setBackground(Theme.INPUT_BG);
        f.setForeground(Theme.TEXT_PRIMARY);
        f.setCaretColor(Theme.NEON_CYAN);
        f.setFont(Theme.FONT_BODY);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                new EmptyBorder(10, 13, 10, 13)));
        f.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
        f.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.NEON_CYAN, 1, true),
                        new EmptyBorder(10, 13, 10, 13)));
            }
            public void focusLost(java.awt.event.FocusEvent e) {
                f.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(Theme.BORDER, 1, true),
                        new EmptyBorder(10, 13, 10, 13)));
            }
        });
        return f;
    }

    private JComboBox<String> styledCombo(String[] items) {
    JComboBox<String> c = new JComboBox<>(items);
    c.setBackground(Theme.INPUT_BG);
    c.setForeground(Theme.TEXT_PRIMARY);
    c.setFont(Theme.FONT_BODY);
    c.setBorder(BorderFactory.createLineBorder(Theme.BORDER, 1, true));
    c.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));
    c.setOpaque(true);

    // Replace the entire UI so the arrow button stops overriding the background
    c.setUI(new javax.swing.plaf.basic.BasicComboBoxUI() {
        @Override
        protected JButton createArrowButton() {
            JButton btn = new JButton("\u25BE");
            btn.setBackground(Theme.INPUT_BG);
            btn.setForeground(Theme.TEXT_SECONDARY);
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 12));
            btn.setBorder(BorderFactory.createEmptyBorder(0, 8, 0, 8));
            btn.setContentAreaFilled(false);
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            return btn;
        }

        @Override
        public void paintCurrentValueBackground(Graphics g, Rectangle bounds, boolean hasFocus) {
            g.setColor(Theme.INPUT_BG);
            g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        }
    });

    // Force all child components to match
    for (Component comp : c.getComponents()) {
        comp.setBackground(Theme.INPUT_BG);
        comp.setForeground(Theme.TEXT_PRIMARY);
    }

    c.setRenderer(new DefaultListCellRenderer() {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                int index, boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            lbl.setFont(Theme.FONT_BODY);
            lbl.setBorder(new EmptyBorder(8, 12, 8, 12));
            if (isSelected) {
                lbl.setBackground(Theme.NEON_CYAN);
                lbl.setForeground(Theme.BACKGROUND);
            } else {
                lbl.setBackground(Theme.INPUT_BG);
                lbl.setForeground(Theme.TEXT_PRIMARY);
            }
            return lbl;
        }
    });

    c.addPopupMenuListener(new javax.swing.event.PopupMenuListener() {
        public void popupMenuWillBecomeVisible(javax.swing.event.PopupMenuEvent e) {
            Object popup = c.getUI().getAccessibleChild(c, 0);
            if (popup instanceof javax.swing.plaf.basic.ComboPopup) {
                JList<?> list = ((javax.swing.plaf.basic.ComboPopup) popup).getList();
                list.setBackground(Theme.INPUT_BG);
                list.setForeground(Theme.TEXT_PRIMARY);
                list.setSelectionBackground(Theme.NEON_CYAN);
                list.setSelectionForeground(Theme.BACKGROUND);
            }
        }
        public void popupMenuWillBecomeInvisible(javax.swing.event.PopupMenuEvent e) {}
        public void popupMenuCanceled(javax.swing.event.PopupMenuEvent e) {}
    });

    return c;
}

    private JTextArea textArea(int rows) {
        JTextArea a = new JTextArea(rows, 40);
        a.setBackground(Theme.INPUT_BG);
        a.setForeground(Theme.TEXT_PRIMARY);
        a.setCaretColor(Theme.NEON_CYAN);
        a.setFont(Theme.FONT_BODY);
        a.setLineWrap(true);
        a.setWrapStyleWord(true);
        a.setBorder(new EmptyBorder(10, 12, 10, 12));
        return a;
    }

    private String getSelectedGender() {
        for (java.util.Map.Entry<String, JToggleButton> e : genderButtons.entrySet()) {
            if (e.getValue().isSelected()) return e.getKey();
        }
        return "";
    }

    private void setField(JTextField f, String val) { if (f != null && val != null) f.setText(val); }
    private void setArea(JTextArea a, String val)   { if (a != null && val != null) a.setText(val); }
}
