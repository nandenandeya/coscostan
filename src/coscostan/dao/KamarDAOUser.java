package coscostan.dao;

import coscostan.model.Kamar;
import coscostan.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;          
import java.util.HashMap;
import javax.swing.table.DefaultTableModel;

public class KamarDAOUser {
    
    /**
     * Method khusus untuk UserDashboard - ambil data kamar yang tersedia dengan JOIN yang benar
     */
    public List<Kamar> getAllKamarForDashboard() {
        List<Kamar> kamarList = new ArrayList<>();
        
        try {
            String sql = "SELECT k.id_kamar, k.nomor_kamar, k.status, " +
                        "tk.tipe_kamar, tk.harga_sewa, tk.lama_sewa, tk.ukuran, tk.fasilitas_kamar " +
                        "FROM kamars k " +
                        "INNER JOIN tipe_kamar tk ON k.id_tipe_kamar = tk.id_tipe_kamar " +
                        "WHERE k.status = 'tersedia' " +
                        "ORDER BY k.nomor_kamar";
            
            System.out.println("Executing query: " + sql);
            
            try (Connection conn = DatabaseConnection.getConnection();
                 PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                
                int count = 0;
                while (rs.next()) {
                    Kamar kamar = new Kamar();
                    // Data dari tabel kamars
                    kamar.setIdKamar(rs.getInt("id_kamar"));
                    kamar.setNomorKamar(rs.getInt("nomor_kamar"));
                    kamar.setStatus(rs.getString("status"));
                    
                    // Data dari tabel tipe_kamar
                    kamar.setTipeKamar(rs.getString("tipe_kamar"));
                    kamar.setHarga(rs.getDouble("harga_sewa"));
                    kamar.setLamaSewa(rs.getString("lama_sewa"));
                    
                    // Field tambahan jika diperlukan
                    kamar.setUkuran(rs.getString("ukuran"));
                    kamar.setFasilitas(rs.getString("fasilitas_kamar"));
                    
                    kamarList.add(kamar);
                    count++;
                    System.out.println("Data " + count + ": " + 
                        kamar.getTipeKamar() + " - Kamar " + kamar.getNomorKamar() + 
                        " - Rp " + kamar.getHarga() + " - " + kamar.getLamaSewa());
                }
                
                System.out.println("Total data found: " + count);
                
            }
            
        } catch (SQLException ex) {
            System.err.println("Error in JOIN query: " + ex.getMessage());
            ex.printStackTrace();
            // Fallback ke data dummy jika error
            kamarList = createFallbackData();
        }
        
        return kamarList;
    }
    
    /**
     * Method untuk langsung mengisi TableModel
     */
    public void fillTableModel(DefaultTableModel tableModel) {
        System.out.println("=== fillTableModel STARTED ===");
        tableModel.setRowCount(0); // Clear existing data
        
        List<Kamar> kamarList = getAllKamarForDashboard();
        System.out.println("Data to display: " + kamarList.size() + " items");
        
        for (Kamar kamar : kamarList) {
            String hargaFormatted = "Rp " + String.format("%,.0f", kamar.getHarga());
            
            Object[] rowData = {
                "KMR" + kamar.getIdKamar(),
                kamar.getTipeKamar(),
                "Kamar " + kamar.getNomorKamar(),
                hargaFormatted,
                kamar.getLamaSewa()
            };
            tableModel.addRow(rowData);
            System.out.println("Added to table: " + java.util.Arrays.toString(rowData));
        }
        
        System.out.println("Total rows in table after fill: " + tableModel.getRowCount());
        System.out.println("=== fillTableModel COMPLETED ===");
    }
    
    /**
     * Get kamar by tipe untuk filter
     */
    public List<Kamar> getKamarByTipeForDashboard(String tipeKamar) {
        System.out.println("Filtering by type: " + tipeKamar);
        
        List<Kamar> allKamars = getAllKamarForDashboard();
        List<Kamar> filteredKamars = new ArrayList<>();
        
        for (Kamar kamar : allKamars) {
            if (kamar.getTipeKamar() != null && 
                kamar.getTipeKamar().equalsIgnoreCase(tipeKamar)) {
                filteredKamars.add(kamar);
            }
        }
        
        System.out.println("Filtered result: " + filteredKamars.size() + " items for type " + tipeKamar);
        return filteredKamars;
    }
    
    /**
     * Get detail kamar lengkap by ID (untuk fitur detail)
     */
    public Kamar getKamarDetailById(int idKamar) {
        String sql = "SELECT k.id_kamar, k.nomor_kamar, k.status, " +
                    "tk.tipe_kamar, tk.harga_sewa, tk.lama_sewa, tk.ukuran, tk.fasilitas_kamar " +
                    "FROM kamars k " +
                    "INNER JOIN tipe_kamar tk ON k.id_tipe_kamar = tk.id_tipe_kamar " +
                    "WHERE k.id_kamar = ?";
        
        Kamar kamar = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idKamar);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                kamar = new Kamar();
                kamar.setIdKamar(rs.getInt("id_kamar"));
                kamar.setNomorKamar(rs.getInt("nomor_kamar"));
                kamar.setStatus(rs.getString("status"));
                kamar.setTipeKamar(rs.getString("tipe_kamar"));
                kamar.setHarga(rs.getDouble("harga_sewa"));
                kamar.setLamaSewa(rs.getString("lama_sewa"));
                kamar.setUkuran(rs.getString("ukuran"));
                kamar.setFasilitas(rs.getString("fasilitas_kamar"));
                
                System.out.println("Kamar detail loaded: " + kamar.getTipeKamar() + " - Kamar " + kamar.getNomorKamar());
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting kamar detail: " + ex.getMessage());
        }
        
        return kamar;
    }
    
    /**
     * Get semua tipe kamar yang tersedia (untuk statistik/filter)
     */
    public List<String> getAvailableRoomTypes() {
        String sql = "SELECT DISTINCT tk.tipe_kamar " +
                    "FROM kamars k " +
                    "INNER JOIN tipe_kamar tk ON k.id_tipe_kamar = tk.id_tipe_kamar " +
                    "WHERE k.status = 'tersedia' " +
                    "ORDER BY tk.tipe_kamar";
        
        List<String> types = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            while (rs.next()) {
                types.add(rs.getString("tipe_kamar"));
            }
            
            System.out.println("Available room types from DB: " + types);
            
        } catch (SQLException ex) {
            System.err.println("Error getting room types: " + ex.getMessage());
            // Fallback
            types.add("A");
            types.add("B");
            types.add("C");
            types.add("D");
        }
        
        return types;
    }
    
    /**
     * Count kamar tersedia by tipe (untuk statistik)
     */
    public int countAvailableKamarsByType(String tipeKamar) {
        String sql = "SELECT COUNT(*) as total " +
                    "FROM kamars k " +
                    "INNER JOIN tipe_kamar tk ON k.id_tipe_kamar = tk.id_tipe_kamar " +
                    "WHERE k.status = 'tersedia' AND tk.tipe_kamar = ?";
        
        int count = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipeKamar);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt("total");
            }
            
            System.out.println("Count for type '" + tipeKamar + "': " + count);
            
        } catch (SQLException ex) {
            System.err.println("Error counting kamars by type: " + ex.getMessage());
        }
        
        return count;
    }
    
    /**
     * Get total kamar tersedia (untuk dashboard statistik)
     */
    public int getTotalAvailableKamars() {
        String sql = "SELECT COUNT(*) as total FROM kamars WHERE status = 'tersedia'";
        
        int count = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            
            if (rs.next()) {
                count = rs.getInt("total");
            }
            
            System.out.println("Total available kamars: " + count);
            
        } catch (SQLException ex) {
            System.err.println("Error counting total available kamars: " + ex.getMessage());
        }
        
        return count;
    }
    
    /**
     * Create fallback data untuk testing (jika database error)
     */
    private List<Kamar> createFallbackData() {
        List<Kamar> fallbackData = new ArrayList<>();
        
        // Data berdasarkan struktur database Anda
        String[] types = {"A", "B", "C", "D"};
        double[] prices = {1500000.0, 2000000.0, 2500000.0, 3000000.0};
        String[] durations = {"3 bulan", "6 bulan", "12 bulan", "12 bulan"};
        
        for (int i = 0; i < 8; i++) {
            Kamar kamar = new Kamar();
            kamar.setIdKamar(i + 1);
            kamar.setTipeKamar(types[i % 4]);
            kamar.setNomorKamar(100 + (i * 10));
            kamar.setHarga(prices[i % 4]);
            kamar.setLamaSewa(durations[i % 4]);
            kamar.setStatus("tersedia");
            kamar.setUkuran((i % 4 + 3) + "x" + (i % 4 + 4) + " meter");
            
            fallbackData.add(kamar);
        }
        
        System.out.println("Fallback data created: " + fallbackData.size() + " items");
        return fallbackData;
    }
    
    /**
     * Test database connection
     */
    public boolean testConnection() {
        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("Database connection: SUCCESS");
                return true;
            }
        } catch (SQLException e) {
            System.err.println("Database connection: FAILED - " + e.getMessage());
        }
        return false;
    }
    
    public Map<String, Integer> getKostStatistics() {
    Map<String, Integer> stats = new HashMap<>();
    
    try (Connection conn = DatabaseConnection.getConnection()) {
        
        // 1. Total Kamar
        String sqlTotal = "SELECT COUNT(*) as total FROM kamars";
        try (PreparedStatement stmt = conn.prepareStatement(sqlTotal);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.put("totalKamar", rs.getInt("total"));
            }
        }
        
        // 2. Kamar Tersedia
        String sqlTersedia = "SELECT COUNT(*) as tersedia FROM kamars WHERE status = 'tersedia'";
        try (PreparedStatement stmt = conn.prepareStatement(sqlTersedia);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.put("kamarTersedia", rs.getInt("tersedia"));
            }
        }
        
        // 3. Total Penghuni - PAKAI DATA REAL 10
        String sqlPenghuni = "SELECT COUNT(*) as penghuni FROM penghuni";
        try (PreparedStatement stmt = conn.prepareStatement(sqlPenghuni);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                stats.put("totalPenghuni", rs.getInt("penghuni"));
            }
        }
        
        System.out.println("REAL DATA FROM DATABASE: " + stats);
        
    } catch (SQLException ex) {
        System.err.println("Error getting statistics: " + ex.getMessage());
        // Fallback data - PAKAI DATA REAL 10 PENGHUNI
        stats.put("totalKamar", 0);
        stats.put("kamarTersedia", 0);
        stats.put("totalPenghuni", 0); // DATA REAL
    }
    
    return stats;
}
}