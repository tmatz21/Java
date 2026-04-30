package ui;

import dao.ChatDAO;
import model.ConversationEntry;
import utils.Session;
import utils.UIFactory;
import utils.UITheme;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.sql.ResultSet;
import java.util.List;

public class ChatPanel extends JPanel {

    private final int userId;
    private final String role;

    private final DefaultListModel<ConversationEntry> convListModel = new DefaultListModel<>();
    private final JList<ConversationEntry> convList = new JList<>(convListModel);
    private ConversationEntry activeConversation;

    private final JPanel messagesPanel = new JPanel();
    private final JScrollPane messagesScroll;
    private final JTextArea inputField = new JTextArea(3, 1);
    private final JButton sendButton = UIFactory.createPrimaryButton("Send");
    private final JLabel conversationTitle = UIFactory.createSectionLabel("Select a conversation");

    public ChatPanel(int userId, String role) {
        this.userId = userId;
        this.role   = role != null ? role : "student";

        setOpaque(false);
        setLayout(new BorderLayout(0, 0));

        messagesPanel.setLayout(new BoxLayout(messagesPanel, BoxLayout.Y_AXIS));
        messagesPanel.setOpaque(false);
        messagesScroll = UIFactory.createScrollPane(messagesPanel);

        add(buildContent(), BorderLayout.CENTER);
        loadConversations();
    }

    // ── Layout ───────────────────────────────────────────────────────

    private JComponent buildContent() {
        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                buildLeftPanel(), buildRightPanel());
        split.setOpaque(false);
        split.setBorder(BorderFactory.createEmptyBorder());
        split.setDividerSize(6);
        split.setDividerLocation(260);
        return split;
    }

    private JPanel buildLeftPanel() {
        JPanel panel = UIFactory.createSurfacePanel(new BorderLayout(0, 8));
        panel.setPreferredSize(new Dimension(260, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Header
        JLabel title = UIFactory.createSectionLabel("Conversations");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);

        // Search field
        JTextField searchField = UIFactory.createTextField("Search users…");
        searchField.setMaximumSize(new Dimension(Integer.MAX_VALUE, 44));

        // Results list (shown during search)
        DefaultListModel<ConversationEntry> searchModel = new DefaultListModel<>();
        JList<ConversationEntry> searchList = new JList<>(searchModel);
        searchList.setCellRenderer(new UserCellRenderer());
        searchList.setBackground(UITheme.SURFACE_MUTED);
        JScrollPane searchScroll = new JScrollPane(searchList);
        searchScroll.setVisible(false);
        searchScroll.setPreferredSize(new Dimension(0, 160));
        searchScroll.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));

        // Wire search — live filter as user types
        searchField.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e)  { doSearch(); }
            public void removeUpdate(DocumentEvent e)  { doSearch(); }
            public void changedUpdate(DocumentEvent e) { doSearch(); }

            private void doSearch() {
                String q = searchField.getText().trim();
                if (q.isEmpty()) {
                    searchModel.clear();
                    searchScroll.setVisible(false);
                    panel.revalidate();
                    return;
                }
                SwingUtilities.invokeLater(() -> {
                    try {
                        List<ConversationEntry> results =
                                ChatDAO.searchUsers(userId, role, q);
                        searchModel.clear();
                        for (ConversationEntry e : results) searchModel.addElement(e);
                        searchScroll.setVisible(!searchModel.isEmpty());
                        panel.revalidate();
                    } catch (Exception ex) {
                        searchModel.clear();
                        searchScroll.setVisible(false);
                    }
                });
            }
        });

        // Click search result → open / create conversation
        searchList.addListSelectionListener(e -> {
            if (e.getValueIsAdjusting()) return;
            ConversationEntry selected = searchList.getSelectedValue();
            if (selected == null || selected.otherUserId < 0) return;
            try {
                int convId = ChatDAO.getOrCreateConversation(userId, selected.otherUserId);
                searchField.setText("");
                searchModel.clear();
                searchScroll.setVisible(false);
                loadConversations();
                // Auto-select the conversation
                for (int i = 0; i < convListModel.size(); i++) {
                    if (convListModel.get(i).conversationId == convId) {
                        convList.setSelectedIndex(i);
                        break;
                    }
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this,
                        "Could not open conversation: " + ex.getMessage());
            }
        });

        // Conversation list
        convList.setCellRenderer(new UserCellRenderer());
        convList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        convList.setBackground(UITheme.SURFACE_MUTED);
        convList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && convList.getSelectedValue() != null) {
                activeConversation = convList.getSelectedValue();
                conversationTitle.setText(activeConversation.label);
                loadMessages();
            }
        });

        JPanel topSection = new JPanel();
        topSection.setOpaque(false);
        topSection.setLayout(new BoxLayout(topSection, BoxLayout.Y_AXIS));
        topSection.add(title);
        topSection.add(Box.createVerticalStrut(8));
        topSection.add(searchField);
        topSection.add(Box.createVerticalStrut(4));
        topSection.add(searchScroll);
        topSection.add(Box.createVerticalStrut(8));
        topSection.add(UIFactory.createBodyLabel("Recent"));

        panel.add(topSection, BorderLayout.NORTH);
        panel.add(new JScrollPane(convList), BorderLayout.CENTER);
        return panel;
    }

    private JPanel buildRightPanel() {
        JPanel panel = UIFactory.createSurfacePanel(new BorderLayout(0, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        // Header bar
        JPanel headerBar = new JPanel(new BorderLayout());
        headerBar.setOpaque(false);
        headerBar.add(conversationTitle, BorderLayout.WEST);
        JButton refreshBtn = UIFactory.createSecondaryButton("Refresh");
        refreshBtn.setPreferredSize(new Dimension(90, 34));
        refreshBtn.addActionListener(e -> loadMessages());
        headerBar.add(refreshBtn, BorderLayout.EAST);

        // Input
        inputField.setLineWrap(true);
        inputField.setWrapStyleWord(true);
        inputField.setFont(UITheme.BODY_FONT);
        inputField.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(UITheme.SURFACE_MUTED),
                BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        inputField.addKeyListener(new KeyAdapter() {
            @Override public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER && !e.isShiftDown()) {
                    e.consume();
                    sendMessage();
                }
            }
        });

        sendButton.setPreferredSize(new Dimension(90, 0));
        sendButton.addActionListener(e -> sendMessage());

        JPanel inputRow = new JPanel(new BorderLayout(8, 0));
        inputRow.setOpaque(false);
        inputRow.add(new JScrollPane(inputField), BorderLayout.CENTER);
        inputRow.add(sendButton, BorderLayout.EAST);

        panel.add(headerBar,    BorderLayout.NORTH);
        panel.add(messagesScroll, BorderLayout.CENTER);
        panel.add(inputRow,     BorderLayout.SOUTH);
        return panel;
    }

    // ── Data loading ─────────────────────────────────────────────────

    private void loadConversations() {
        convListModel.clear();
        try {
            List<ConversationEntry> entries = ChatDAO.getConversations(userId);
            for (ConversationEntry e : entries) convListModel.addElement(e);
        } catch (Exception ignored) {}
    }

    private void loadMessages() {
        if (activeConversation == null) return;
        messagesPanel.removeAll();
        try {
            ResultSet rs = ChatDAO.getMessages(activeConversation.conversationId);
            boolean any = false;
            while (rs != null && rs.next()) {
                any = true;
                messagesPanel.add(buildBubble(
                        rs.getString("message"),
                        rs.getString("sent_at"),
                        rs.getInt("sender_id") == userId
                ));
                messagesPanel.add(Box.createVerticalStrut(8));
            }
            if (!any) messagesPanel.add(
                    UIFactory.createWrappedBodyText("No messages yet. Say hello below."));
        } catch (Exception e) {
            messagesPanel.add(UIFactory.createWrappedBodyText("Could not load messages."));
        }
        messagesPanel.revalidate();
        messagesPanel.repaint();
        SwingUtilities.invokeLater(() -> {
            JScrollBar bar = messagesScroll.getVerticalScrollBar();
            bar.setValue(bar.getMaximum());
        });
    }

    private JPanel buildBubble(String text, String time, boolean mine) {
        JPanel bubble = new JPanel(new BorderLayout(0, 2));
        bubble.setOpaque(false);
        bubble.setAlignmentX(Component.LEFT_ALIGNMENT);
        bubble.setBorder(BorderFactory.createEmptyBorder(0, mine ? 60 : 0, 0, mine ? 0 : 60));

        JTextArea msg = new JTextArea(text);
        msg.setEditable(false);
        msg.setFocusable(false);
        msg.setLineWrap(true);
        msg.setWrapStyleWord(true);
        msg.setFont(UITheme.BODY_FONT);
        msg.setForeground(mine ? UITheme.TEXT_LIGHT : UITheme.TEXT_PRIMARY);
        msg.setBackground(mine ? UITheme.PRIMARY : UITheme.SURFACE_MUTED);
        msg.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(
                        mine ? UITheme.PRIMARY_DARK : UITheme.SURFACE_MUTED, 1, true),
                BorderFactory.createEmptyBorder(8, 12, 8, 12)
        ));

        JLabel timeLabel = new JLabel(time != null ? time : "");
        timeLabel.setFont(new Font("Segoe UI", Font.PLAIN, 10));
        timeLabel.setForeground(UITheme.TEXT_SECONDARY);
        timeLabel.setHorizontalAlignment(mine ? SwingConstants.RIGHT : SwingConstants.LEFT);

        bubble.add(msg, BorderLayout.CENTER);
        bubble.add(timeLabel, BorderLayout.SOUTH);
        return bubble;
    }

    private void sendMessage() {
        if (activeConversation == null) {
            JOptionPane.showMessageDialog(this,
                    "Search for a user above or select a conversation to start messaging.");
            return;
        }
        String text = inputField.getText().trim();
        if (text.isEmpty()) return;
        try {
            ChatDAO.sendMessage(activeConversation.conversationId, userId, text);
            inputField.setText("");
            loadMessages();
        } catch (Exception e) {
            JOptionPane.showMessageDialog(this, "Could not send: " + e.getMessage());
        }
    }

    // ── Cell renderer ─────────────────────────────────────────────────

    private static class UserCellRenderer extends DefaultListCellRenderer {
        @Override
        public Component getListCellRendererComponent(JList<?> list, Object value,
                                                      int index, boolean isSelected, boolean cellHasFocus) {
            JLabel lbl = (JLabel) super.getListCellRendererComponent(
                    list, value, index, isSelected, cellHasFocus);
            lbl.setBorder(BorderFactory.createEmptyBorder(8, 12, 8, 12));
            lbl.setFont(UITheme.BODY_FONT);
            if (value instanceof ConversationEntry) {
                ConversationEntry e = (ConversationEntry) value;
                lbl.setText(e.unread > 0 ? e.label + "  (" + e.unread + " new)" : e.label);
            }
            return lbl;
        }
    }
}
