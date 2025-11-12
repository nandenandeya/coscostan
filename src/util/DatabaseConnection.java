package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/coscostan_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            try {
                Class.forName("com.mysql.cj.jdbc.Driver");
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("✅ Koneksi database berhasil!");
            } catch (ClassNotFoundException | SQLException e) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, e);
                System.out.println("❌ Koneksi database gagal: " + e.getMessage());
            }
        }
        return connection;
    }

    public static boolean testConnection() {
        System.out.println("🔍 Testing koneksi database...");
        System.out.println("URL: " + URL);
        System.out.println("Username: " + USERNAME);
        
        try (Connection testConn = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            if (testConn != null && !testConn.isClosed()) {
                System.out.println("✅ TEST BERHASIL: Koneksi database berhasil!");
                return true;
            }
        } catch (SQLException e) {
            System.out.println("❌ TEST GAGAL: " + e.getMessage());
            e.printStackTrace();
        }
        return false;
    }

    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("📌 Koneksi database ditutup!");
            } catch (SQLException e) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }
}