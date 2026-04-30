package dao;

import database.DBConnection;
import model.User;
import utils.Session;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UserDAO {

    public static boolean login(String email, String password) {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT * FROM users WHERE email=? AND password=?";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, email);
            ps.setString(2, password);

            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Session.userId = rs.getInt("id");
                Session.role = rs.getString("role");
                return true;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean register(User user) {
        if (emailExists(user.getEmail())) {
            return false;
        }

        try {
            Connection conn = DBConnection.getConnection();

            String sql = "INSERT INTO users(name, email, password, role) VALUES(?,?,?,?)";
            PreparedStatement ps = conn.prepareStatement(sql);

            ps.setString(1, user.getName());
            ps.setString(2, user.getEmail());
            ps.setString(3, user.getPassword());
            ps.setString(4, user.getRole());

            return ps.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean emailExists(String email) {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT id FROM users WHERE email=?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, email);

            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }
    /**
     * Returns the display name for a given user ID.
     * Called after login to populate Session.userName.
     */
    public static String getUserName(int userId) {
        String sql = "SELECT name FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("name");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * Returns all users as a ResultSet (id, name, email, role).
     * Used by AdminPanel.
     */
    public static ResultSet getAllUsers() throws SQLException {
        Connection conn = DBConnection.getConnection();
        Statement st = conn.createStatement();
        return st.executeQuery("SELECT id, name, email, role FROM users ORDER BY name");
    }

    /**
     * Deletes a user by ID. Used by AdminPanel.
     */
    public static void deleteUser(int id) {
        String sql = "DELETE FROM users WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Updates a user's name, email, and role. Used by AdminPanel.
     */
    public static void updateUser(int id, String name, String email, String role) {
        String sql = "UPDATE users SET name = ?, email = ?, role = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, email);
            ps.setString(3, role);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}