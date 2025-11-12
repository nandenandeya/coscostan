package main;

import util.DatabaseConnection;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;

public class ConnectionTest {
    
    public static void main(String[] args) {
        System.out.println("🚀 MEMULAI TEST KONEKSI DATABASE");
        System.out.println("=================================");
        
        // Test 1: Basic connection
        System.out.println("\n📋 TEST 1: Basic Connection");
        boolean connectionTest = DatabaseConnection.testConnection();
        System.out.println("Hasil: " + (connectionTest ? "BERHASIL ✅" : "GAGAL ❌"));
        
        if (connectionTest) {
            testQueryExecution();

            testTableAccess();
        }
        
        System.out.println("=================================");
        System.out.println("TEST SELESAI");
    }
    
    private static void testQueryExecution() {
        System.out.println("\n TEST 2: Query Execution");
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT 1 as test")) {
            
            if (rs.next()) {
                System.out.println("✅ Query execution berhasil");
            }
        } catch (Exception e) {
            System.out.println("❌ Query execution gagal: " + e.getMessage());
        }
    }
    
    private static void testTableAccess() {
        System.out.println("\n TEST 3: Table Access");
        String[] tables = {"admin", "tipe_kamar", "kamar", "penghuni"};
        
        for (String table : tables) {
            try (Connection conn = DatabaseConnection.getConnection();
                 Statement stmt = conn.createStatement();
                 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) as count FROM " + table)) {
                
                if (rs.next()) {
                    int count = rs.getInt("count");
                    System.out.println("✅ Tabel '" + table + "' : " + count + " records");
                }
            } catch (Exception e) {
                System.out.println("❌ Akses tabel '" + table + "' gagal: " + e.getMessage());
            }
        }
    }
}