package ui;

import dao.ApplicationDAO;
import utils.UIFactory;
import utils.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.Desktop;
import java.io.File;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class ViewApplicationsPanel extends JPanel {

    // Columns: 0=ID(hidden), 1=Intern, 2=Internship, 3=Status, 4=CV label, 5=cv_path(hidden)
    private final DefaultTableModel model;
    private final JTable table;
    private final JButton approveButton;
    private final JButton rejectButton;
    private final JButton viewCVButton;

    public ViewApplicationsPanel() {
        this.model = new DefaultTableModel(
                new String[]{"ID", "Intern", "Internship", "Status", "CV", "cv_path"}, 0) {
            @Override
            public boolean isCellEditable(int row, int column) { return false; }
        };
        this.table = new JTable(model);
        this.approveButton = UIFactory.createPrimaryButton("Approve");
        this.rejectButton  = UIFactory.createDangerButton("Reject");
        this.viewCVButton  = UIFactory.createSecondaryButton("View CV");

        setOpaque(false);
        setLayout(new BorderLayout(0, 18));

        UIFactory.styleTable(table);
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        table.getSelectionModel().addListSelectionListener(e -> updateActionState());
        table.getColumnModel().getColumn(3).setCellRenderer(new StatusRenderer());

        // Hide ID column (0) and raw cv_path column (5)
        hideColumn(0);
        hideColumn(5);

        approveButton.addActionListener(e -> updateStatus("Approved"));
        rejectButton.addActionListener(e  -> updateStatus("Rejected"));
        viewCVButton.addActionListener(e  -> openCV());

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);

        loadData();
        updateActionState();
    }

    private void hideColumn(int col) {
        table.getColumnModel().getColumn(col).setMinWidth(0);
        table.getColumnModel().getColumn(col).setMaxWidth(0);
        table.getColumnModel().getColumn(col).setWidth(0);
    }

    private JPanel buildToolbar() {
        JPanel toolbar = UIFactory.createSurfacePanel(new BorderLayout(16, 0));
        toolbar.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = UIFactory.createSectionLabel("Application review");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub = UIFactory.createBodyLabel("Select a row, then approve, reject, or view the attached CV.");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(sub);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        actions.setOpaque(false);

        int btnH = 42;
        setBtn(viewCVButton,  120, btnH);
        setBtn(approveButton, 110, btnH);
        setBtn(rejectButton,  100, btnH);
        JButton refreshButton = UIFactory.createSecondaryButton("Refresh");
        setBtn(refreshButton, 100, btnH);
        refreshButton.addActionListener(e -> loadData());

        actions.add(viewCVButton);
        actions.add(approveButton);
        actions.add(rejectButton);
        actions.add(refreshButton);

        toolbar.add(text, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);
        return toolbar;
    }

    private void setBtn(JButton btn, int w, int h) {
        btn.setPreferredSize(new Dimension(Math.max(btn.getPreferredSize().width, w), h));
    }

    private JPanel buildTableCard() {
        JPanel card = UIFactory.createSurfacePanel(new BorderLayout());
        card.add(UIFactory.createScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        ResultSet rs = null;
        try {
            rs = ApplicationDAO.getAllApplications();
            model.setRowCount(0);
            while (rs != null && rs.next()) {
                String cvPath = "";
                try { cvPath = rs.getString("cv_path"); } catch (Exception ignored) {}
                boolean hasCV = cvPath != null && !cvPath.trim().isEmpty();
                model.addRow(new Object[]{
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("title"),
                        rs.getString("status"),
                        hasCV ? "Attached" : "—",
                        cvPath != null ? cvPath : ""   // raw path stored hidden
                });
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to load applications right now.");
        } finally {
            closeResultSet(rs);
            updateActionState();
        }
    }

    private void updateStatus(String status) {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an application first."); return; }
        int modelRow = table.convertRowIndexToModel(row);
        int id = (int) model.getValueAt(modelRow, 0);
        ApplicationDAO.updateStatus(id, status);
        loadData();
        JOptionPane.showMessageDialog(this, "Status updated to " + status + ".");
    }

    private void openCV() {
        int row = table.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select an application first."); return; }
        int modelRow = table.convertRowIndexToModel(row);

        // Read path directly from hidden column 5 — no extra DB call needed
        String cvPath = model.getValueAt(modelRow, 5) != null
                ? model.getValueAt(modelRow, 5).toString().trim()
                : "";

        if (cvPath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "No CV was attached to this application.");
            return;
        }

        File file = new File(cvPath);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "CV file not found:\n" + cvPath);
            return;
        }

        try {
            Desktop.getDesktop().open(file);
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not open CV:\n" + e.getMessage());
        }
    }

    private void updateActionState() {
        boolean sel = table.getSelectedRow() >= 0;
        approveButton.setEnabled(sel);
        rejectButton.setEnabled(sel);

        if (sel) {
            int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
            String path = model.getValueAt(modelRow, 5) != null
                    ? model.getValueAt(modelRow, 5).toString().trim() : "";
            viewCVButton.setEnabled(!path.isEmpty());
        } else {
            viewCVButton.setEnabled(false);
        }
    }

    private void closeResultSet(ResultSet rs) {
        if (rs == null) return;
        try {
            Statement st = rs.getStatement();
            Connection conn = st != null ? st.getConnection() : null;
            rs.close();
            if (st != null) st.close();
            if (conn != null) conn.close();
        } catch (Exception ignored) {}
    }

    private static class StatusRenderer extends DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable tbl, Object value,
                                                       boolean isSelected, boolean hasFocus, int row, int col) {
            JLabel lbl = (JLabel) super.getTableCellRendererComponent(tbl, value, isSelected, hasFocus, row, col);
            lbl.setHorizontalAlignment(SwingConstants.CENTER);
            lbl.setFont(UITheme.LABEL_FONT);
            if (isSelected) return lbl;
            String s = value == null ? "" : value.toString().toLowerCase();
            if ("approved".equals(s))      lbl.setForeground(UITheme.SUCCESS);
            else if ("rejected".equals(s)) lbl.setForeground(UITheme.DANGER);
            else                           lbl.setForeground(UITheme.WARNING);
            return lbl;
        }
    }
}