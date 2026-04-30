package dao;

import database.DBConnection;
import model.Internship;

import java.sql.*;
import java.util.ArrayList;

public class InternshipDAO {

    public static boolean addInternship(Internship i) {
        try {
            Connection conn = DBConnection.getConnection();
            boolean hasDescription = hasDescriptionColumn(conn);
            String sql = hasDescription
                    ? "INSERT INTO internships(title, company, location, description) VALUES(?,?,?,?)"
                    : "INSERT INTO internships(title, company, location) VALUES(?,?,?)";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, i.getTitle());
            ps.setString(2, i.getCompany());
            ps.setString(3, i.getLocation());

            if (hasDescription) {
                ps.setString(4, i.getDescription());
            }

            return ps.executeUpdate() > 0;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    public static ArrayList<Internship> getAllInternships() {
        ArrayList<Internship> list = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            ResultSet rs = conn.createStatement().executeQuery("SELECT * FROM internships");
            boolean hasDescription = resultSetHasColumn(rs, "description");

            while (rs.next()) {
                list.add(new Internship(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("company"),
                        rs.getString("location"),
                        hasDescription ? rs.getString("description") : ""
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }
    /**
     * Deletes an internship by ID.
     */
    public static void deleteInternship(int id) {
        String sql = "DELETE FROM internships WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public static ArrayList<Internship> search(String keyword) {
        ArrayList<Internship> list = new ArrayList<>();

        try {
            Connection conn = DBConnection.getConnection();
            String sql = "SELECT * FROM internships WHERE title LIKE ?";

            PreparedStatement ps = conn.prepareStatement(sql);
            ps.setString(1, "%" + keyword + "%");

            ResultSet rs = ps.executeQuery();
            boolean hasDescription = resultSetHasColumn(rs, "description");

            while (rs.next()) {
                list.add(new Internship(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("company"),
                        rs.getString("location"),
                        hasDescription ? rs.getString("description") : ""
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return list;
    }

    public static boolean hasDescriptionColumn() {
        try {
            Connection conn = DBConnection.getConnection();
            return hasDescriptionColumn(conn);
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean hasDescriptionColumn(Connection conn) {
        if (conn == null) {
            return false;
        }

        try (ResultSet columns = conn.getMetaData().getColumns(null, null, "internships", "description")) {
            return columns.next();
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean resultSetHasColumn(ResultSet rs, String columnName) {
        try {
            ResultSetMetaData metaData = rs.getMetaData();
            for (int i = 1; i <= metaData.getColumnCount(); i++) {
                if (columnName.equalsIgnoreCase(metaData.getColumnName(i))) {
                    return true;
                }
            }
        } catch (Exception ignored) {
        }

        return false;
    }
    /**
     * Updates an internship's title, company, and location.
     */
    public static void updateInternship(int id, String title, String company, String location) {
        String sql = "UPDATE internships SET title = ?, company = ?, location = ? WHERE id = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, title);
            ps.setString(2, company);
            ps.setString(3, location);
            ps.setInt(4, id);
            ps.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}