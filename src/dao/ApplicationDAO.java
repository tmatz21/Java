package dao;

import database.DBConnection;
import model.Application;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class ApplicationDAO {

    public static void apply(Application app) {
        // Check for duplicate application first
        String checkSql = "SELECT id FROM applications WHERE user_id = ? AND internship_id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement check = conn.prepareStatement(checkSql)) {
            check.setInt(1, app.getUserId());
            check.setInt(2, app.getInternshipId());
            ResultSet rs = check.executeQuery();
            if (rs.next()) {
                return; // Already applied, silently skip
            }
        } catch (Exception e) {
            e.printStackTrace();
            return;
        }

        // Insert with cv_path
        String sql = "INSERT INTO applications (user_id, internship_id, status, cv_path) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, app.getUserId());
            ps.setInt(2, app.getInternshipId());
            ps.setString(3, "Pending");
            ps.setString(4, app.getCvPath() != null ? app.getCvPath() : "");
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getAllApplications() {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT applications.id, users.name, internships.title, " +
                    "applications.status, applications.cv_path " +
                    "FROM applications " +
                    "JOIN users ON applications.user_id = users.id " +
                    "JOIN internships ON applications.internship_id = internships.id";

            return conn.createStatement().executeQuery(sql);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void updateStatus(int id, String status) {
        try (Connection conn = DBConnection.getConnection()) {
            String sql = "UPDATE applications SET status = ? WHERE id = ?";
            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, status);
            ps.setInt(2, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static ResultSet getUserApplications(int userId) {
        try {
            Connection conn = DBConnection.getConnection();

            String sql = "SELECT internships.title, applications.status " +
                    "FROM applications " +
                    "JOIN internships ON applications.internship_id = internships.id " +
                    "WHERE user_id = ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setInt(1, userId);
            return ps.executeQuery();

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getCVPath(int applicationId) {
        String sql = "SELECT cv_path FROM applications WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, applicationId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) return rs.getString("cv_path");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void deleteApplication(int id) {
        String sql = "DELETE FROM applications WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}