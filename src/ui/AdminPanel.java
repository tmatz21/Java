package ui;

import dao.ApplicationDAO;
import dao.InternshipDAO;
import dao.UserDAO;
import model.Internship;
import model.User;
import utils.UIFactory;
import utils.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * Admin CRUD panel — three tabs: Users, Internships, Applications.
 */
public class AdminPanel extends JPanel {

    public AdminPanel() {
        setOpaque(false);
        setLayout(new BorderLayout(0, 12));

        JPanel header = new JPanel();
        header.setOpaque(false);
        header.setLayout(new BoxLayout(header, BoxLayout.Y_AXIS));
        header.add(UIFactory.createSectionLabel("Admin Control Panel"));
        header.add(Box.createVerticalStrut(4));
        header.add(UIFactory.createWrappedBodyText("Create, edit, and delete users, internships, and applications."));

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(UITheme.SECTION_FONT);
        tabs.addTab("Users", buildUsersTab());
        tabs.addTab("Internships", buildInternshipsTab());
        tabs.addTab("Applications", buildApplicationsTab());

        add(header, BorderLayout.NORTH);
        add(tabs, BorderLayout.CENTER);
    }

    //  USERS TAB
    private JPanel buildUsersTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Role"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIFactory.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hideIdColumn(table);

        Runnable load = () -> {
            model.setRowCount(0);
            ResultSet rs = null;
            try {
                rs = UserDAO.getAllUsers();
                while (rs != null && rs.next()) {
                    model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("email"), rs.getString("role")});
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(panel, "Could not load users.");
            } finally { closeRS(rs); }
        };
        load.run();

        JButton addBtn = UIFactory.createPrimaryButton("Add User");
        JButton editBtn = UIFactory.createSecondaryButton("Edit User");
        JButton deleteBtn = UIFactory.createDangerButton("Delete User");
        addBtn.setPreferredSize(new Dimension(130, 44));
        editBtn.setPreferredSize(new Dimension(130, 44));
        deleteBtn.setPreferredSize(new Dimension(130, 44));

        addBtn.addActionListener(e -> showAddUserDialog(panel, load));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "Select a user first."); return; }
            int modelRow = table.convertRowIndexToModel(row);
            int id = (int) model.getValueAt(modelRow, 0);
            String name = (String) model.getValueAt(modelRow, 1);
            String email = (String) model.getValueAt(modelRow, 2);
            String role = (String) model.getValueAt(modelRow, 3);
            showEditUserDialog(panel, id, name, email, role, load);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "Select a user first."); return; }
            int modelRow = table.convertRowIndexToModel(row);
            int id = (int) model.getValueAt(modelRow, 0);
            String name = (String) model.getValueAt(modelRow, 1);
            int confirm = JOptionPane.showConfirmDialog(panel, "Delete user \"" + name + "\"? This cannot be undone.", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                UserDAO.deleteUser(id);
                load.run();
            }
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);
        toolbar.add(addBtn);
        toolbar.add(editBtn);
        toolbar.add(deleteBtn);
        JButton refreshBtn = UIFactory.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(110, 44));
        refreshBtn.addActionListener(e -> load.run());
        toolbar.add(refreshBtn);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(UIFactory.createScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void showAddUserDialog(JPanel parent, Runnable reload) {
        JTextField nameF = UIFactory.createTextField("Full name");
        JTextField emailF = UIFactory.createTextField("Email");
        JPasswordField passF = UIFactory.createPasswordField("Password");
        String[] roles = {"student", "company", "admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);

        JPanel form = buildFormPanel(new JComponent[]{nameF, emailF, passF, roleBox});
        int res = JOptionPane.showConfirmDialog(parent, form, "Add User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        String name = nameF.getText().trim();
        String email = emailF.getText().trim();
        String pass = new String(passF.getPassword());
        String role = (String) roleBox.getSelectedItem();

        if (name.isEmpty() || email.isEmpty() || pass.isEmpty()) {
            JOptionPane.showMessageDialog(parent, "Fill in all fields."); return;
        }
        if (UserDAO.emailExists(email)) {
            JOptionPane.showMessageDialog(parent, "Email already registered."); return;
        }
        UserDAO.register(new User(name, email, pass, role));
        reload.run();
    }

    private void showEditUserDialog(JPanel parent, int id, String name, String email, String role, Runnable reload) {
        JTextField nameF = UIFactory.createTextField("Full name");
        nameF.setText(name);
        JTextField emailF = UIFactory.createTextField("Email");
        emailF.setText(email);
        String[] roles = {"student", "company", "admin"};
        JComboBox<String> roleBox = new JComboBox<>(roles);
        roleBox.setSelectedItem(role);

        JPanel form = buildFormPanel(new JComponent[]{nameF, emailF, roleBox});
        int res = JOptionPane.showConfirmDialog(parent, form, "Edit User", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        UserDAO.updateUser(id, nameF.getText().trim(), emailF.getText().trim(), (String) roleBox.getSelectedItem());
        reload.run();
    }

    //  INTERNSHIPS TAB
    private JPanel buildInternshipsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Title", "Company", "Location"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIFactory.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hideIdColumn(table);

        Runnable load = () -> {
            model.setRowCount(0);
            for (Internship i : InternshipDAO.getAllInternships()) {
                model.addRow(new Object[]{i.getId(), i.getTitle(), i.getCompany(), i.getLocation()});
            }
        };
        load.run();

        JButton addBtn = UIFactory.createPrimaryButton("Add Internship");
        JButton editBtn = UIFactory.createSecondaryButton("Edit");
        JButton deleteBtn = UIFactory.createDangerButton("Delete");
        addBtn.setPreferredSize(new Dimension(150, 44));
        editBtn.setPreferredSize(new Dimension(110, 44));
        deleteBtn.setPreferredSize(new Dimension(110, 44));

        addBtn.addActionListener(e -> new AddInternshipForm(SwingUtilities.getWindowAncestor(panel), load));
        editBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "Select an internship first."); return; }
            int modelRow = table.convertRowIndexToModel(row);
            int id = (int) model.getValueAt(modelRow, 0);
            String title = (String) model.getValueAt(modelRow, 1);
            String company = (String) model.getValueAt(modelRow, 2);
            String location = (String) model.getValueAt(modelRow, 3);
            showEditInternshipDialog(panel, id, title, company, location, load);
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "Select an internship first."); return; }
            int modelRow = table.convertRowIndexToModel(row);
            int id = (int) model.getValueAt(modelRow, 0);
            String title = (String) model.getValueAt(modelRow, 1);
            int confirm = JOptionPane.showConfirmDialog(panel, "Delete internship \"" + title + "\"?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                InternshipDAO.deleteInternship(id);
                load.run();
            }
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);
        toolbar.add(addBtn); toolbar.add(editBtn); toolbar.add(deleteBtn);
        JButton refreshBtn = UIFactory.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(110, 44));
        refreshBtn.addActionListener(e -> load.run());
        toolbar.add(refreshBtn);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(UIFactory.createScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    private void showEditInternshipDialog(JPanel parent, int id, String title, String company, String location, Runnable reload) {
        JTextField titleF = UIFactory.createTextField("Title"); titleF.setText(title);
        JTextField compF = UIFactory.createTextField("Company"); compF.setText(company);
        JTextField locF = UIFactory.createTextField("Location"); locF.setText(location);

        JPanel form = buildFormPanel(new JComponent[]{titleF, compF, locF});
        int res = JOptionPane.showConfirmDialog(parent, form, "Edit Internship", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
        if (res != JOptionPane.OK_OPTION) return;

        InternshipDAO.updateInternship(id, titleF.getText().trim(), compF.getText().trim(), locF.getText().trim());
        reload.run();
    }

    // ─────────────────────────────── APPLICATIONS TAB ───────────────
    private JPanel buildApplicationsTab() {
        JPanel panel = new JPanel(new BorderLayout(0, 12));
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(12, 0, 0, 0));

        DefaultTableModel model = new DefaultTableModel(new String[]{"ID", "Intern", "Internship", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = new JTable(model);
        UIFactory.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        hideIdColumn(table);

        Runnable load = () -> {
            model.setRowCount(0);
            ResultSet rs = null;
            try {
                rs = ApplicationDAO.getAllApplications();
                while (rs != null && rs.next()) {
                    model.addRow(new Object[]{rs.getInt("id"), rs.getString("name"), rs.getString("title"), rs.getString("status")});
                }
            } catch (Exception e) {
                JOptionPane.showMessageDialog(panel, "Could not load applications.");
            } finally { closeRS(rs); }
        };
        load.run();

        String[] statuses = {"Pending", "Approved", "Rejected"};
        JComboBox<String> statusBox = new JComboBox<>(statuses);
        JButton updateBtn = UIFactory.createPrimaryButton("Update Status");
        JButton deleteBtn = UIFactory.createDangerButton("Delete");
        updateBtn.setPreferredSize(new Dimension(160, 44));
        deleteBtn.setPreferredSize(new Dimension(110, 44));
        statusBox.setPreferredSize(new Dimension(140, 44));

        updateBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "Select an application first."); return; }
            int modelRow = table.convertRowIndexToModel(row);
            int id = (int) model.getValueAt(modelRow, 0);
            ApplicationDAO.updateStatus(id, (String) statusBox.getSelectedItem());
            load.run();
        });
        deleteBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(panel, "Select an application first."); return; }
            int modelRow = table.convertRowIndexToModel(row);
            int id = (int) model.getValueAt(modelRow, 0);
            int confirm = JOptionPane.showConfirmDialog(panel, "Delete this application permanently?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                ApplicationDAO.deleteApplication(id);
                load.run();
            }
        });

        JPanel toolbar = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 0));
        toolbar.setOpaque(false);
        JLabel label = new JLabel("Set status:");
        label.setFont(UITheme.BODY_FONT);
        toolbar.add(label);
        toolbar.add(statusBox);
        toolbar.add(updateBtn);
        toolbar.add(deleteBtn);
        JButton refreshBtn = UIFactory.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(110, 44));
        refreshBtn.addActionListener(e -> load.run());
        toolbar.add(refreshBtn);

        panel.add(toolbar, BorderLayout.NORTH);
        panel.add(UIFactory.createScrollPane(table), BorderLayout.CENTER);
        return panel;
    }

    //  Helpers
    private JPanel buildFormPanel(JComponent[] fields) {
        JPanel form = new JPanel();
        form.setLayout(new BoxLayout(form, BoxLayout.Y_AXIS));
        for (JComponent f : fields) {
            f.setMaximumSize(new Dimension(Integer.MAX_VALUE, f instanceof JComboBox ? 40 : 44));
            form.add(f);
            form.add(Box.createVerticalStrut(10));
        }
        form.setPreferredSize(new Dimension(380, fields.length * 60));
        return form;
    }

    private void hideIdColumn(JTable table) {
        table.getColumnModel().getColumn(0).setMinWidth(0);
        table.getColumnModel().getColumn(0).setMaxWidth(0);
        table.getColumnModel().getColumn(0).setWidth(0);
    }

    private void closeRS(ResultSet rs) {
        if (rs == null) return;
        try {
            Statement st = rs.getStatement();
            Connection conn = st != null ? st.getConnection() : null;
            rs.close();
            if (st != null) st.close();
            if (conn != null) conn.close();
        } catch (Exception ignored) {}
    }
}