package ui;

import dao.ApplicationDAO;
import utils.Session;
import utils.UIFactory;
import utils.UITheme;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;

public class MyApplicationsPanel extends JPanel {

    private final DefaultTableModel model;
    private final JTable table;

    public MyApplicationsPanel() {
        this.model = new DefaultTableModel(new String[]{"Internship", "Status"}, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        this.table = new JTable(model);

        setOpaque(false);
        setLayout(new BorderLayout(0, 14));

        UIFactory.styleTable(table);
        table.getColumnModel().getColumn(1).setCellRenderer(new StatusRenderer());

        add(buildToolbar(), BorderLayout.NORTH);
        add(buildTableCard(), BorderLayout.CENTER);

        loadData();
    }

    private JPanel buildToolbar() {
        JPanel toolbar = UIFactory.createSurfacePanel(new BorderLayout(16, 0));
        toolbar.setBorder(BorderFactory.createEmptyBorder(14, 16, 14, 16));

        JPanel text = new JPanel();
        text.setOpaque(false);
        text.setLayout(new BoxLayout(text, BoxLayout.Y_AXIS));
        JLabel title = UIFactory.createSectionLabel("My applications");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        JLabel sub = UIFactory.createBodyLabel("Track every application and its current status.");
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        text.add(title);
        text.add(Box.createVerticalStrut(4));
        text.add(sub);

        JButton refreshButton = UIFactory.createSecondaryButton("Refresh");
        refreshButton.setPreferredSize(new Dimension(100, 42));
        refreshButton.addActionListener(e -> loadData());

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.RIGHT, 0, 0));
        actions.setOpaque(false);
        actions.add(refreshButton);

        toolbar.add(text, BorderLayout.WEST);
        toolbar.add(actions, BorderLayout.EAST);
        return toolbar;
    }

    private JPanel buildTableCard() {
        JPanel card = UIFactory.createSurfacePanel(new BorderLayout());
        card.add(UIFactory.createScrollPane(table), BorderLayout.CENTER);
        return card;
    }

    private void loadData() {
        ResultSet rs = null;
        try {
            rs = ApplicationDAO.getUserApplications(Session.userId);
            model.setRowCount(0);
            while (rs != null && rs.next()) {
                model.addRow(new Object[]{rs.getString("title"), rs.getString("status")});
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Unable to load your applications right now.");
        } finally {
            closeResultSet(rs);
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