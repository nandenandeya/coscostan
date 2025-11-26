package coscostan.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/coscostan_db";
    private static final String USERNAME = "root";
    private static final String PASSWORD = ""; // Sesuaikan dengan password MySQL Anda
    
    private static Connection connection;
    
    // Private constructor untuk mencegah instantiasi
    private DatabaseConnection() {}
    
    /**
     * Mendapatkan koneksi database
     * @return Connection object
     */
    public static Connection getConnection() {
        try {
            // Cek jika koneksi null, closed, atau tidak valid
            if (connection == null || connection.isClosed() || !connection.isValid(2)) {
                // Load MySQL JDBC Driver
                Class.forName("com.mysql.cj.jdbc.Driver");
                
                // Buat koneksi baru
                connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                System.out.println("Koneksi database berhasil!");
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "MySQL Driver tidak ditemukan", ex);
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Gagal terkoneksi ke database", ex);
        }
        return connection;
    }
    
    /**
     * Menutup koneksi database
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                connection = null;
                System.out.println("Koneksi database ditutup.");
            } catch (SQLException ex) {
                Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Gagal menutup koneksi database", ex);
            }
        }
    }
    
    /**
     * Test koneksi database
     */
    public static void testConnection() {
        try {
            Connection testConn = getConnection();
            if (testConn != null && !testConn.isClosed()) {
                System.out.println("Test koneksi: BERHASIL");
            } else {
                System.out.println("Test koneksi: GAGAL");
            }
        } catch (SQLException ex) {
            Logger.getLogger(DatabaseConnection.class.getName()).log(Level.SEVERE, "Test koneksi gagal", ex);
        }
    }
}