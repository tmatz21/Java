package database;

import java.sql.Connection;

public class DBTest {
    public static void main(String[] args) {
        Connection conn = DBConnection.getConnection();

        if (conn != null) {
            System.out.println("Connected!");
        } else {
            System.out.println("Failed!");
        }
    }
}