package dao;

import model.ConversationEntry;
import database.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Chat DAO — generic user-to-user messaging.
 * Rules enforced here:
 *  - Admin can start/receive conversations with anyone.
 *  - Students and businesses cannot start a conversation with admin.
 *  - Students can message businesses and vice-versa (unchanged).
 */
public class ChatDAO {

    // ── Conversations ────────────────────────────────────────────────

    /** All conversations the given user is part of, labelled with the other party's name. */
    public static List<ConversationEntry> getConversations(int userId) throws SQLException {
        List<ConversationEntry> list = new ArrayList<>();
        String sql =
                "SELECT c.id, " +
                        "  CASE WHEN c.user_one_id = ? THEN c.user_two_id ELSE c.user_one_id END AS other_id, " +
                        "  u.name, " +
                        "  (SELECT COUNT(*) FROM messages m " +
                        "   WHERE m.conversation_id = c.id AND m.sender_id != ? AND m.is_read = 0) AS unread " +
                        "FROM conversations c " +
                        "JOIN users u ON u.id = CASE WHEN c.user_one_id = ? THEN c.user_two_id ELSE c.user_one_id END " +
                        "WHERE c.user_one_id = ? OR c.user_two_id = ? " +
                        "ORDER BY c.created_at DESC";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, userId);
            ps.setInt(3, userId);
            ps.setInt(4, userId);
            ps.setInt(5, userId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ConversationEntry(
                        rs.getInt("id"),
                        rs.getInt("other_id"),
                        rs.getString("name"),
                        rs.getInt("unread")
                ));
            }
        }
        return list;
    }
    /**
     * Search for users the current user is allowed to message.
     * Rules:
     *  - Admin  → can search everyone (students + businesses), excluding themselves.
     *  - Student  → can search businesses only.
     *  - Business → can search students only.
     *  - Nobody   → can search admin (role = 'admin').
     * @param currentUserId  the logged-in user's id
     * @param currentRole    "admin", "student", "company", or "business"
     * @param query          search term matched against name or email
     */
    public static List<ConversationEntry> searchUsers(
            int currentUserId, String currentRole, String query) throws SQLException {

        List<ConversationEntry> list = new ArrayList<>();
        String likeQuery = "%" + query.trim() + "%";
        String roleFilter;

        if ("admin".equalsIgnoreCase(currentRole)) {
            // Admin can message students and businesses — not other admins
            roleFilter = "role IN ('student', 'company', 'business')";
        } else if ("student".equalsIgnoreCase(currentRole)) {
            // Students can message businesses only — not admin
            roleFilter = "role IN ('company', 'business')";
        } else {
            // Businesses can message students only — not admin
            roleFilter = "role = 'student'";
        }

        String sql = "SELECT id, name FROM users " +
                "WHERE " + roleFilter + " " +
                "AND id != ? " +
                "AND (name LIKE ? OR email LIKE ?) " +
                "ORDER BY name LIMIT 30";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setString(2, likeQuery);
            ps.setString(3, likeQuery);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(new ConversationEntry(-1, rs.getInt("id"), rs.getString("name"), 0));
            }
        }
        return list;
    }

    /** Gets an existing conversation or creates a new one between two users. */
    public static int getOrCreateConversation(int userOneId, int userTwoId) throws SQLException {
        // Normalise order so (A,B) and (B,A) map to the same row
        int lo = Math.min(userOneId, userTwoId);
        int hi = Math.max(userOneId, userTwoId);

        try (Connection conn = DBConnection.getConnection()) {
            String findSql = "SELECT id FROM conversations WHERE user_one_id = ? AND user_two_id = ?";
            try (PreparedStatement ps = conn.prepareStatement(findSql)) {
                ps.setInt(1, lo);
                ps.setInt(2, hi);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) return rs.getInt("id");
            }
            String insertSql = "INSERT INTO conversations (user_one_id, user_two_id) VALUES (?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, lo);
                ps.setInt(2, hi);
                ps.executeUpdate();
                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) return keys.getInt(1);
            }
        }
        throw new SQLException("Could not create conversation.");
    }

    // ── Messages ─────────────────────────────────────────────────────

    public static ResultSet getMessages(int conversationId) throws SQLException {
        Connection conn = DBConnection.getConnection();
        String sql = "SELECT sender_id, message, " +
                "DATE_FORMAT(sent_at, '%H:%i  %d %b') AS sent_at " +
                "FROM messages WHERE conversation_id = ? ORDER BY sent_at ASC";
        PreparedStatement ps = conn.prepareStatement(sql);
        ps.setInt(1, conversationId);
        return ps.executeQuery();
    }

    public static void sendMessage(int conversationId, int senderId, String text) throws SQLException {
        String sql = "INSERT INTO messages (conversation_id, sender_id, message) VALUES (?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            ps.setInt(2, senderId);
            ps.setString(3, text);
            ps.executeUpdate();
        }
        markRead(conversationId, senderId);
    }

    private static void markRead(int conversationId, int readerId) throws SQLException {
        String sql = "UPDATE messages SET is_read = 1 " +
                "WHERE conversation_id = ? AND sender_id != ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, conversationId);
            ps.setInt(2, readerId);
            ps.executeUpdate();
        }
    }
}
