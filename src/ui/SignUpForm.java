package ui;

import dao.UserDAO;
import model.User;
import utils.Session;
import utils.UIFactory;

import javax.swing.*;
import java.awt.*;

public class SignUpForm {

    private final JFrame parentFrame;
    private final String storedRole;
    private final String accountLabel;
    private final String nameLabel;

    public SignUpForm(JFrame parentFrame, String accountType) {
        this.parentFrame = parentFrame;
        this.storedRole = "business".equalsIgnoreCase(accountType) ? "company" : "student";
        this.accountLabel = "business".equalsIgnoreCase(accountType) ? "business" : "intern";
        this.nameLabel = "business".equalsIgnoreCase(accountType) ? "Business name" : "Full name";

        JDialog dialog = new JDialog(parentFrame, "Create " + capitalize(accountLabel) + " Account", true);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextField nameField = UIFactory.createTextField(nameLabel);
        JTextField emailField = UIFactory.createTextField("Email");
        JPasswordField passwordField = UIFactory.createPasswordField("Password");
        JPasswordField confirmPasswordField = UIFactory.createPasswordField("Confirm password");

        JButton createButton = UIFactory.createPrimaryButton("Create " + capitalize(accountLabel) + " Account");
        JButton backButton = UIFactory.createSecondaryButton("Back to Sign In");

        createButton.addActionListener(e -> register(dialog, nameField, emailField, passwordField, confirmPasswordField));
        backButton.addActionListener(e -> dialog.dispose());

        JPanel root = UIFactory.createAppPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JPanel card = UIFactory.createSurfacePanel(new BorderLayout(0, 24));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(UIFactory.createDisplayLabel("Create your " + accountLabel + " account"));
        header.add(Box.createVerticalStrut(8));
        header.add(UIFactory.createWrappedBodyText(
                "business".equals(accountLabel)
                        ? "Post internships and manage incoming applications from the same workspace."
                        : "Browse internships, apply faster, and keep your status updates in one place."
        ));

        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        UIFactory.setBoxFieldSize(nameField, 72);
        UIFactory.setBoxFieldSize(emailField, 72);
        UIFactory.setBoxFieldSize(passwordField, 72);
        UIFactory.setBoxFieldSize(confirmPasswordField, 72);

        form.add(nameField);
        form.add(Box.createVerticalStrut(14));
        form.add(emailField);
        form.add(Box.createVerticalStrut(14));
        form.add(passwordField);
        form.add(Box.createVerticalStrut(14));
        form.add(confirmPasswordField);

        UIFactory.setButtonHeight(backButton, 50);
        UIFactory.setButtonHeight(createButton, 50);

        JPanel actions = new JPanel();
        actions.setOpaque(false);
        actions.setLayout(new BoxLayout(actions, BoxLayout.Y_AXIS));
        actions.add(backButton);
        actions.add(Box.createVerticalStrut(10));
        actions.add(createButton);

        JPanel footer = UIFactory.createMutedPanel(new BorderLayout(0, 8));
        footer.add(UIFactory.createSectionLabel("What happens next?"), BorderLayout.NORTH);
        footer.add(UIFactory.createWrappedBodyText("Your new account signs in immediately after it is created."), BorderLayout.CENTER);

        card.add(header, BorderLayout.NORTH);
        card.add(form, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);
        root.add(footer, BorderLayout.SOUTH);

        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(createButton);
        dialog.setMinimumSize(new Dimension(640, 680));
        dialog.setSize(680, 700);
        dialog.setLocationRelativeTo(parentFrame);
        dialog.setVisible(true);
    }

    private void register(JDialog dialog, JTextField nameField, JTextField emailField, JPasswordField passwordField, JPasswordField confirmPasswordField) {
        String name = nameField.getText().trim();
        String email = emailField.getText().trim();
        String password = new String(passwordField.getPassword());
        String confirmPassword = new String(confirmPasswordField.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Fill in every field before creating your account.");
            return;
        }

        if (!email.contains("@") || !email.contains(".")) {
            JOptionPane.showMessageDialog(dialog, "Enter a valid email address.");
            emailField.requestFocusInWindow();
            return;
        }

        if (password.length() < 6) {
            JOptionPane.showMessageDialog(dialog, "Use a password with at least 6 characters.");
            passwordField.requestFocusInWindow();
            return;
        }

        if (!password.equals(confirmPassword)) {
            JOptionPane.showMessageDialog(dialog, "The passwords do not match.");
            confirmPasswordField.setText("");
            confirmPasswordField.requestFocusInWindow();
            return;
        }

        if (UserDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(dialog, "That email is already registered.");
            emailField.requestFocusInWindow();
            return;
        }

        boolean created = UserDAO.register(new User(name, email, password, storedRole));
        if (!created || !UserDAO.login(email, password)) {
            JOptionPane.showMessageDialog(dialog, "Unable to create the account right now.");
            return;
        }

        dialog.dispose();
        parentFrame.dispose();
        Session.userName = name;
        new Dashboard(Session.role);
    }

    private String capitalize(String value) {
        return Character.toUpperCase(value.charAt(0)) + value.substring(1);
    }
}