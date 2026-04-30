package ui;

import dao.InternshipDAO;
import model.Internship;
import utils.UIFactory;
import utils.UITheme;

import javax.swing.*;
import java.awt.*;

public class AddInternshipForm {

    public AddInternshipForm() {
        this(null, null);
    }

    public AddInternshipForm(Window owner, Runnable onSuccess) {
        JDialog dialog = new JDialog(owner, "Post Internship", Dialog.ModalityType.APPLICATION_MODAL);
        dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JTextField titleField    = UIFactory.createTextField("Role title");
        JTextField companyField  = UIFactory.createTextField("Company");
        JTextField locationField = UIFactory.createTextField("Location");

        // Description — plain JTextArea with explicit minimum size so it is always visible
        JTextArea descriptionArea = new JTextArea();
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(UITheme.BODY_FONT);
        descriptionArea.setForeground(UITheme.TEXT_PRIMARY);
        descriptionArea.setRows(8);  // ensures visible height regardless of dialog size

        JScrollPane descScroll = UIFactory.createScrollPane(descriptionArea);
        descScroll.setMinimumSize(new Dimension(0, 160));
        descScroll.setPreferredSize(new Dimension(0, 200));

        JButton publishButton = UIFactory.createPrimaryButton("Publish Internship");
        JButton closeButton   = UIFactory.createSecondaryButton("Close");

        publishButton.addActionListener(e -> publishInternship(
                dialog, titleField, companyField, locationField, descriptionArea, onSuccess));
        closeButton.addActionListener(e -> dialog.dispose());

        // ── Root ────────────────────────────────────────────────────
        JPanel root = UIFactory.createAppPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(22, 22, 22, 22));

        JPanel card = UIFactory.createSurfacePanel(new BorderLayout(0, 18));

        // ── Header ──────────────────────────────────────────────────
        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(UIFactory.createDisplayLabel("Post a new internship"));
        header.add(Box.createVerticalStrut(6));
        header.add(UIFactory.createWrappedBodyText(
                "Fill in every field so applicants understand the role clearly."));

        // ── Form — BoxLayout so each field stacks with guaranteed height ──
        JPanel form = new JPanel();
        form.setOpaque(false);
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));

        int fieldH = 52;
        setFieldSize(titleField,    fieldH);
        setFieldSize(companyField,  fieldH);
        setFieldSize(locationField, fieldH);

        JLabel descLabel = UIFactory.createSectionLabel("Description");
        descLabel.setAlignmentX(Component.LEFT_ALIGNMENT);

        form.add(titleField);
        form.add(Box.createVerticalStrut(10));
        form.add(companyField);
        form.add(Box.createVerticalStrut(10));
        form.add(locationField);
        form.add(Box.createVerticalStrut(14));
        form.add(descLabel);
        form.add(Box.createVerticalStrut(6));
        form.add(descScroll);

        // ── Footer ──────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        footer.setOpaque(false);
        publishButton.setPreferredSize(new Dimension(
                Math.max(publishButton.getPreferredSize().width, 200), 48));
        closeButton.setPreferredSize(new Dimension(
                Math.max(closeButton.getPreferredSize().width, 140), 48));
        footer.add(closeButton);
        footer.add(publishButton);

        card.add(header, BorderLayout.NORTH);
        card.add(form,   BorderLayout.CENTER);
        card.add(footer, BorderLayout.SOUTH);

        root.add(card, BorderLayout.CENTER);

        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(publishButton);
        dialog.setSize(680, 580);
        dialog.setMinimumSize(new Dimension(560, 520));
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);
    }

    private void setFieldSize(JComponent field, int height) {
        field.setMaximumSize(new Dimension(Integer.MAX_VALUE, height));
        field.setPreferredSize(new Dimension(field.getPreferredSize().width, height));
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
    }

    private void publishInternship(
            JDialog dialog,
            JTextField titleField,
            JTextField companyField,
            JTextField locationField,
            JTextArea descriptionArea,
            Runnable onSuccess
    ) {
        String title       = titleField.getText().trim();
        String company     = companyField.getText().trim();
        String location    = locationField.getText().trim();
        String description = descriptionArea.getText().trim();

        if (title.isEmpty() || company.isEmpty() || location.isEmpty() || description.isEmpty()) {
            JOptionPane.showMessageDialog(dialog, "Fill in every field before publishing.");
            return;
        }

        boolean created = InternshipDAO.addInternship(
                new Internship(title, company, location, description));
        if (!created) {
            JOptionPane.showMessageDialog(dialog,
                    "The internship could not be published. Please try again.");
            return;
        }

        JOptionPane.showMessageDialog(dialog, "Internship published successfully.");
        dialog.dispose();

        if (onSuccess != null) {
            onSuccess.run();
        }
    }
}
