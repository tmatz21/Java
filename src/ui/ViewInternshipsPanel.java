package ui;

import dao.ApplicationDAO;
import dao.InternshipDAO;
import model.Application;
import model.Internship;
import utils.RoleUtils;
import utils.Session;
import utils.UIFactory;
import utils.UITheme;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;

public class ViewInternshipsPanel extends JPanel {

    private final String role;
    private final JTextField searchField;
    private final DefaultTableModel model;
    private final JTable table;
    private final JTextArea selectedTitle;
    private final JTextArea selectedMeta;
    private final JTextArea descriptionArea;
    private final JButton actionButton;

    private ArrayList<Internship> internships = new ArrayList<>();
    private Internship selectedInternship;

    public ViewInternshipsPanel(String role) {
        this.role         = role == null ? "student" : role;
        this.searchField  = UIFactory.createTextField("Search by title");
        this.model        = new DefaultTableModel(new String[]{"Title", "Company", "Location"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        this.table           = new JTable(model);
        this.selectedTitle   = UIFactory.createWrappedTitleText("Select an internship");
        this.selectedMeta    = UIFactory.createWrappedBodyText("Choose a row to see the details.");
        this.descriptionArea = new JTextArea();
        this.actionButton    = UIFactory.createPrimaryButton("Apply");

        setOpaque(false);
        setLayout(new BorderLayout(0, 14));

        UIFactory.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(this::handleSelection);

        descriptionArea.setEditable(false);
        descriptionArea.setOpaque(false);
        descriptionArea.setLineWrap(true);
        descriptionArea.setWrapStyleWord(true);
        descriptionArea.setFont(UITheme.BODY_FONT);
        descriptionArea.setForeground(UITheme.TEXT_SECONDARY);

        actionButton.addActionListener(e -> applyToSelectedInternship());
        actionButton.setVisible(!RoleUtils.canManageInternships(this.role));

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildContent(), BorderLayout.CENTER);

        loadInternships(InternshipDAO.getAllInternships());
    }

    private JPanel buildToolbar() {
        JPanel toolbar = UIFactory.createSurfacePanel(new BorderLayout(16, 0));
        toolbar.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = UIFactory.createSectionLabel("Opportunity browser");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub = UIFactory.createBodyLabel("Search listings and apply directly from this view.");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(sub);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        controls.setOpaque(false);

        searchField.setPreferredSize(new Dimension(220, 42));

        JButton searchButton = UIFactory.createPrimaryButton("Search");
        JButton resetButton  = UIFactory.createSecondaryButton("Reset");
        searchButton.setPreferredSize(new Dimension(90, 42));
        resetButton.setPreferredSize(new Dimension(80, 42));

        searchButton.addActionListener(e -> searchInternships());
        resetButton.addActionListener(e -> {
            searchField.setText("");
            loadInternships(InternshipDAO.getAllInternships());
        });

        controls.add(searchField);
        controls.add(searchButton);
        controls.add(resetButton);

        if (RoleUtils.canManageInternships(role)) {
            JButton postButton = UIFactory.createSecondaryButton("Post Internship");
            postButton.setPreferredSize(new Dimension(140, 42));
            postButton.addActionListener(e -> new AddInternshipForm(
                    SwingUtilities.getWindowAncestor(this),
                    () -> loadInternships(InternshipDAO.getAllInternships())
            ));
            controls.add(postButton);
        }

        toolbar.add(text, BorderLayout.WEST);
        toolbar.add(controls, BorderLayout.EAST);
        return toolbar;
    }

    private Component buildContent() {
        JPanel tableCard = UIFactory.createSurfacePanel(new BorderLayout(0, 10));
        tableCard.add(UIFactory.createSectionLabel("Current listings"), BorderLayout.NORTH);
        tableCard.add(UIFactory.createScrollPane(table), BorderLayout.CENTER);

        JPanel detailsCard = UIFactory.createSurfacePanel(new BorderLayout(0, 12));

        JPanel detailsHeader = new JPanel();
        detailsHeader.setOpaque(false);
        detailsHeader.setLayout(new BoxLayout(detailsHeader, BoxLayout.Y_AXIS));
        selectedTitle.setAlignmentX(Component.LEFT_ALIGNMENT);
        selectedMeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        detailsHeader.add(selectedTitle);
        detailsHeader.add(Box.createVerticalStrut(4));
        detailsHeader.add(selectedMeta);

        JPanel descriptionPanel = UIFactory.createMutedPanel(new BorderLayout());
        descriptionArea.setText("Select a listing to see its description.");
        descriptionPanel.add(descriptionArea, BorderLayout.CENTER);

        JPanel actions = new JPanel(new BorderLayout());
        actions.setOpaque(false);
        actions.add(actionButton, BorderLayout.WEST);

        if (RoleUtils.canManageInternships(role)) {
            JLabel hint = UIFactory.createBodyLabel("Use 'Post Internship' in the toolbar to add listings.");
            actions.add(hint, BorderLayout.CENTER);
        }

        detailsCard.add(detailsHeader, BorderLayout.NORTH);
        detailsCard.add(descriptionPanel, BorderLayout.CENTER);
        detailsCard.add(actions, BorderLayout.SOUTH);

        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tableCard, detailsCard);
        splitPane.setOpaque(false);
        splitPane.setBorder(BorderFactory.createEmptyBorder());
        splitPane.setDividerSize(6);
        splitPane.setResizeWeight(0.55);
        return splitPane;
    }

    private void searchInternships() {
        String keyword = searchField.getText().trim();
        loadInternships(keyword.isEmpty() ? InternshipDAO.getAllInternships() : InternshipDAO.search(keyword));
    }

    private void loadInternships(ArrayList<Internship> results) {
        internships = results;
        model.setRowCount(0);
        for (Internship i : internships) {
            model.addRow(new Object[]{i.getTitle(), i.getCompany(), i.getLocation()});
        }
        if (model.getRowCount() > 0) table.setRowSelectionInterval(0, 0);
        else clearSelectionState();
    }

    private void handleSelection(ListSelectionEvent event) {
        if (event.getValueIsAdjusting()) return;
        int viewRow = table.getSelectedRow();
        if (viewRow < 0) { clearSelectionState(); return; }
        int modelRow = table.convertRowIndexToModel(viewRow);
        if (modelRow < 0 || modelRow >= internships.size()) { clearSelectionState(); return; }

        selectedInternship = internships.get(modelRow);
        selectedTitle.setText(selectedInternship.getTitle());
        selectedMeta.setText(selectedInternship.getCompany() + "  |  " + selectedInternship.getLocation());

        String desc = selectedInternship.getDescription();
        descriptionArea.setText(desc == null || desc.isBlank()
                ? "No description added for this internship yet." : desc);

        actionButton.setEnabled(selectedInternship.getId() > 0 && !RoleUtils.canManageInternships(role));
    }

    private void clearSelectionState() {
        selectedInternship = null;
        selectedTitle.setText("No internship selected");
        selectedMeta.setText("Choose a listing from the table to inspect it.");
        descriptionArea.setText("Select a listing to see its description.");
        actionButton.setEnabled(false);
    }

    private void applyToSelectedInternship() {
        if (selectedInternship == null || selectedInternship.getId() <= 0) {
            JOptionPane.showMessageDialog(this, "Select an internship first.");
            return;
        }
        String cvPath = showCVApplicationDialog();
        if (cvPath == null) return;
        ApplicationDAO.apply(new Application(Session.userId, selectedInternship.getId(), cvPath));
        JOptionPane.showMessageDialog(this, "Application submitted successfully.");
    }

    private String showCVApplicationDialog() {
        Window owner = SwingUtilities.getWindowAncestor(this);
        JDialog dialog = new JDialog(owner, "Apply — " + selectedInternship.getTitle(),
                Dialog.ModalityType.APPLICATION_MODAL);

        final String[] result = {null};

        JPanel root = UIFactory.createAppPanel(new BorderLayout());
        root.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        JPanel card = UIFactory.createSurfacePanel(new BorderLayout(0, 16));

        JPanel content = new JPanel();
        content.setOpaque(false);
        content.setLayout(new BoxLayout(content, BoxLayout.Y_AXIS));

        JLabel title = UIFactory.createTitleLabel("Apply to internship");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(title);
        content.add(Box.createVerticalStrut(10));

        JLabel info = new JLabel("<html><body style='width:340px;'>"
                + "Applying to <b>" + selectedInternship.getTitle() + "</b> at <b>"
                + selectedInternship.getCompany() + "</b>.</body></html>");
        info.setFont(UITheme.BODY_FONT);
        info.setForeground(UITheme.TEXT_SECONDARY);
        info.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(info);
        content.add(Box.createVerticalStrut(16));

        JLabel cvLabel = UIFactory.createSectionLabel("Attach your CV (optional)");
        cvLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        content.add(cvLabel);
        content.add(Box.createVerticalStrut(8));

        final File[] selectedCV = {null};
        JLabel fileNameLabel = new JLabel("No file selected");
        fileNameLabel.setFont(UITheme.BODY_FONT);
        fileNameLabel.setForeground(UITheme.TEXT_SECONDARY);

        JButton browseButton = UIFactory.createSecondaryButton("Browse…");
        browseButton.setPreferredSize(new Dimension(110, 38));
        browseButton.addActionListener(e -> {
            JFileChooser chooser = new JFileChooser();
            chooser.setDialogTitle("Select your CV");
            chooser.setFileFilter(new javax.swing.filechooser.FileNameExtensionFilter(
                    "PDF & Word files", "pdf", "doc", "docx"));
            if (chooser.showOpenDialog(dialog) == JFileChooser.APPROVE_OPTION) {
                selectedCV[0] = chooser.getSelectedFile();
                fileNameLabel.setText(selectedCV[0].getName());
                fileNameLabel.setForeground(UITheme.TEXT_PRIMARY);
            }
        });

        JPanel cvRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        cvRow.setOpaque(false);
        cvRow.setAlignmentX(Component.LEFT_ALIGNMENT);
        cvRow.add(browseButton);
        cvRow.add(fileNameLabel);
        content.add(cvRow);

        JPanel actions = new JPanel(new GridLayout(1, 2, 10, 0));
        actions.setOpaque(false);
        JButton cancelButton  = UIFactory.createSecondaryButton("Cancel");
        JButton confirmButton = UIFactory.createPrimaryButton("Submit Application");
        cancelButton.setPreferredSize(new Dimension(0, 44));
        confirmButton.setPreferredSize(new Dimension(0, 44));

        cancelButton.addActionListener(e -> dialog.dispose());
        confirmButton.addActionListener(e -> {
            String savedPath = "";
            if (selectedCV[0] != null) {
                savedPath = saveCVFile(selectedCV[0]);
                if (savedPath == null) {
                    JOptionPane.showMessageDialog(dialog, "Could not save CV. Please try again.");
                    return;
                }
            }
            result[0] = savedPath;
            dialog.dispose();
        });

        actions.add(cancelButton);
        actions.add(confirmButton);

        card.add(content, BorderLayout.CENTER);
        card.add(actions, BorderLayout.SOUTH);
        root.add(card, BorderLayout.CENTER);

        dialog.setContentPane(root);
        dialog.getRootPane().setDefaultButton(confirmButton);
        dialog.setSize(520, 320);
        dialog.setLocationRelativeTo(owner);
        dialog.setVisible(true);

        return result[0];
    }

    private String saveCVFile(File sourceFile) {
        try {
            File dir = new File("cv_uploads");
            if (!dir.exists()) dir.mkdirs();
            String destName = "user" + Session.userId + "_" + System.currentTimeMillis()
                    + "_" + sourceFile.getName();
            File dest = new File(dir, destName);
            Files.copy(sourceFile.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return dest.getAbsolutePath();
        } catch (Exception e) {
            return null;
        }
    }
}