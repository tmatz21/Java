package ui;

import dao.UserDAO;
import utils.Session;
import utils.UIFactory;
import utils.UITheme;

import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class LoginForm {

    public LoginForm() {
        JFrame frame = new JFrame("Internship Management System");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setMinimumSize(new Dimension(1040, 700));

        JTextField email = UIFactory.createTextField("Email");
        JPasswordField password = UIFactory.createPasswordField("Password");
        JButton loginButton = UIFactory.createPrimaryButton("Sign In");

        loginButton.addActionListener(e -> handleLogin(frame, email, password));

        JPanel root = UIFactory.createAppPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(28, 28, 28, 28));

        JPanel shell = new JPanel(new GridBagLayout());
        shell.setOpaque(false);
        shell.setPreferredSize(new Dimension(980, 620));

        GridBagConstraints shellGbc = new GridBagConstraints();
        shellGbc.gridy = 0;
        shellGbc.fill = GridBagConstraints.BOTH;
        shellGbc.insets = new Insets(0, 0, 0, 24);
        shellGbc.weighty = 1;

        shellGbc.gridx = 0;
        shellGbc.weightx = 0.52;
        shell.add(buildBrandPanel(), shellGbc);

        shellGbc.gridx = 1;
        shellGbc.weightx = 0.48;
        shellGbc.insets = new Insets(0, 0, 0, 0);
        shell.add(buildLoginPanel(frame, email, password, loginButton), shellGbc);

        root.add(shell, BorderLayout.CENTER);

        frame.setContentPane(root);
        frame.setSize(1120, 760);
        frame.setLocationRelativeTo(null);
        frame.getRootPane().setDefaultButton(loginButton);
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowOpened(WindowEvent e) {
                SwingUtilities.invokeLater(() -> email.requestFocusInWindow());
            }
        });
        frame.setVisible(true);
    }

    private JPanel buildBrandPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 24));
        panel.setBackground(UITheme.SIDEBAR);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SIDEBAR, 1, true),
                BorderFactory.createEmptyBorder(36, 36, 36, 36)
        ));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel badge = UIFactory.createPillLabel("Internship operations", UITheme.PRIMARY, UITheme.TEXT_LIGHT);
        badge.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel title = new JLabel("A cleaner way to manage internships.");
        title.setFont(new Font("Segoe UI", Font.BOLD, 30));
        title.setForeground(UITheme.TEXT_LIGHT);
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        JTextArea body = UIFactory.createWrappedBodyText("Track openings, applications, and review work from one workspace designed to feel calm and easy to navigate.");
        body.setForeground(new Color(226, 233, 230));
        body.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(badge);
        header.add(Box.createVerticalStrut(20));
        header.add(title);
        header.add(Box.createVerticalStrut(12));
        header.add(body);

        JPanel highlights = new JPanel(new GridLayout(3, 1, 0, 14));
        highlights.setOpaque(false);
        highlights.add(createHighlight("Clear application tracking", "Review intern activity and internship progress in one place."));
        highlights.add(createHighlight("Focused intern workflow", "Browse opportunities, apply quickly, and keep status visible."));
        highlights.add(createHighlight("Practical business tools", "Post openings and manage incoming applications with less friction."));

        panel.add(header, BorderLayout.NORTH);
        panel.add(highlights, BorderLayout.CENTER);

        return panel;
    }

    private JPanel buildLoginPanel(JFrame frame, JTextField email, JPasswordField password, JButton loginButton) {
        JPanel card = UIFactory.createSurfacePanel(new BorderLayout(0, 24));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));

        JLabel title = UIFactory.createDisplayLabel("Welcome back");
        JTextArea body = UIFactory.createWrappedBodyText("Sign in to continue to your internship workspace.");

        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.setAlignmentX(Component.LEFT_ALIGNMENT);

        header.add(title);
        header.add(Box.createVerticalStrut(8));
        header.add(body);

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        UIFactory.setBoxFieldSize(email, 72);
        UIFactory.setBoxFieldSize(password, 72);
        loginButton.setAlignmentX(Component.LEFT_ALIGNMENT);
        UIFactory.setButtonHeight(loginButton, 50);

        JButton internSignUpButton = UIFactory.createSecondaryButton("Sign Up as Intern");
        JButton businessSignUpButton = UIFactory.createSecondaryButton("Sign Up as Business");
        UIFactory.setButtonHeight(internSignUpButton, 50);
        UIFactory.setButtonHeight(businessSignUpButton, 50);
        internSignUpButton.addActionListener(e -> new SignUpForm(frame, "student"));
        businessSignUpButton.addActionListener(e -> new SignUpForm(frame, "business"));

        form.add(email);
        form.add(Box.createVerticalStrut(14));
        form.add(password);
        form.add(Box.createVerticalStrut(20));
        form.add(loginButton);
        form.add(Box.createVerticalStrut(14));
        form.add(UIFactory.createSectionLabel("New here?"));
        form.add(Box.createVerticalStrut(10));

        JPanel signUpButtons = new JPanel(new GridLayout(1, 2, 12, 0));
        signUpButtons.setOpaque(false);
        signUpButtons.setAlignmentX(Component.LEFT_ALIGNMENT);
        signUpButtons.add(internSignUpButton);
        signUpButtons.add(businessSignUpButton);

        form.add(signUpButtons);
        form.add(Box.createVerticalStrut(14));
        form.add(UIFactory.createWrappedBodyText("Use your registered email and password to access the dashboard."));

        JPanel footer = UIFactory.createMutedPanel(new BorderLayout(0, 8));
        footer.add(UIFactory.createSectionLabel("Need access?"), BorderLayout.NORTH);
        footer.add(UIFactory.createWrappedBodyText("Interns and businesses can sign in here, or create a new account directly from this screen."), BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        frame.getRootPane().setDefaultButton(loginButton);
        return card;
    }

    private JPanel createHighlight(String title, String body) {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(63, 92, 89), 1, true),
                BorderFactory.createEmptyBorder(16, 16, 16, 16)
        ));

        JLabel titleView = new JLabel(title);
        titleView.setFont(UITheme.SECTION_FONT);
        titleView.setForeground(UITheme.TEXT_LIGHT);

        JLabel bodyView = new JLabel("<html><div style='width:300px;'>" + body + "</div></html>");
        bodyView.setFont(UITheme.BODY_FONT);
        bodyView.setForeground(new Color(214, 223, 221));

        panel.add(titleView, BorderLayout.NORTH);
        panel.add(bodyView, BorderLayout.CENTER);
        return panel;
    }

    private void handleLogin(JFrame frame, JTextField email, JPasswordField password) {
        boolean success = UserDAO.login(email.getText().trim(), new String(password.getPassword()));

        if (!success) {
            JOptionPane.showMessageDialog(frame, "Invalid login details. Please try again.");
            password.setText("");
            email.requestFocusInWindow();
            return;
        }

        Session.userName = UserDAO.getUserName(Session.userId);
        new Dashboard(Session.role);
        frame.dispose();
    }
}