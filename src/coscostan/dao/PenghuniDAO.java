package dao;

import model.Penghuni;
import util.DatabaseConnection;
import util.DateUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PenghuniDAO implements DAO<Penghuni> {
    
    @Override
    public boolean insert(Penghuni penghuni) {
        String sql = "INSERT INTO penghuni (id_kamar, nama_penghuni, tanggal_masuk, tanggal_keluar, kontak, email, alamat_asal) VALUES (?, ?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, penghuni.getIdKamar());
            stmt.setString(2, penghuni.getNamaPenghuni());
            
            // Convert Date to SQL Date
            java.sql.Date sqlTanggalMasuk = new java.sql.Date(penghuni.getTanggalMasuk().getTime());
            stmt.setDate(3, sqlTanggalMasuk);
            
            // Handle nullable tanggal_keluar
            if (penghuni.getTanggalKeluar() != null) {
                java.sql.Date sqlTanggalKeluar = new java.sql.Date(penghuni.getTanggalKeluar().getTime());
                stmt.setDate(4, sqlTanggalKeluar);
            } else {
                stmt.setNull(4, Types.DATE);
            }
            
            stmt.setString(5, penghuni.getKontak());
            stmt.setString(6, penghuni.getEmail());
            stmt.setString(7, penghuni.getAlamatAsal());
            
            int rowsAffected = stmt.executeUpdate();
            
            // Get generated ID
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        penghuni.setIdPenghuni(generatedKeys.getInt(1));
                    }
                }
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error inserting penghuni: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public Penghuni getById(int id) {
        String sql = "SELECT * FROM penghuni WHERE id_penghuni = ?";
        Penghuni penghuni = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                penghuni = new Penghuni();
                penghuni.setIdPenghuni(rs.getInt("id_penghuni"));
                penghuni.setIdKamar(rs.getInt("id_kamar"));
                penghuni.setNamaPenghuni(rs.getString("nama_penghuni"));
                penghuni.setTanggalMasuk(rs.getDate("tanggal_masuk"));
                penghuni.setTanggalKeluar(rs.getDate("tanggal_keluar"));
                penghuni.setKontak(rs.getString("kontak"));
                penghuni.setEmail(rs.getString("email"));
                penghuni.setAlamatAsal(rs.getString("alamat_asal"));
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting penghuni by id: " + ex.getMessage());
        }
        
        return penghuni;
    }
    
    public Penghuni getByKamar(int idKamar) {
        String sql = "SELECT * FROM penghuni WHERE id_kamar = ?";
        Penghuni penghuni = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idKamar);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                penghuni = new Penghuni();
                penghuni.setIdPenghuni(rs.getInt("id_penghuni"));
                penghuni.setIdKamar(rs.getInt("id_kamar"));
                penghuni.setNamaPenghuni(rs.getString("nama_penghuni"));
                penghuni.setTanggalMasuk(rs.getDate("tanggal_masuk"));
                penghuni.setTanggalKeluar(rs.getDate("tanggal_keluar"));
                penghuni.setKontak(rs.getString("kontak"));
                penghuni.setEmail(rs.getString("email"));
                penghuni.setAlamatAsal(rs.getString("alamat_asal"));
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting penghuni by kamar: " + ex.getMessage());
        }
        
        return penghuni;
    }
    
    public Penghuni getByNama(String nama) {
        String sql = "SELECT * FROM penghuni WHERE nama_penghuni LIKE ?";
        Penghuni penghuni = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, "%" + nama + "%");
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                penghuni = new Penghuni();
                penghuni.setIdPenghuni(rs.getInt("id_penghuni"));
                penghuni.setIdKamar(rs.getInt("id_kamar"));
                penghuni.setNamaPenghuni(rs.getString("nama_penghuni"));
                penghuni.setTanggalMasuk(rs.getDate("tanggal_masuk"));
                penghuni.setTanggalKeluar(rs.getDate("tanggal_keluar"));
                penghuni.setKontak(rs.getString("kontak"));
                penghuni.setEmail(rs.getString("email"));
                penghuni.setAlamatAsal(rs.getString("alamat_asal"));
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting penghuni by nama: " + ex.getMessage());
        }
        
        return penghuni;
    }
    
    @Override
    public List<Penghuni> getAll() {
        String sql = "SELECT * FROM penghuni ORDER BY nama_penghuni";
        List<Penghuni> penghunis = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Penghuni penghuni = new Penghuni();
                penghuni.setIdPenghuni(rs.getInt("id_penghuni"));
                penghuni.setIdKamar(rs.getInt("id_kamar"));
                penghuni.setNamaPenghuni(rs.getString("nama_penghuni"));
                penghuni.setTanggalMasuk(rs.getDate("tanggal_masuk"));
                penghuni.setTanggalKeluar(rs.getDate("tanggal_keluar"));
                penghuni.setKontak(rs.getString("kontak"));
                penghuni.setEmail(rs.getString("email"));
                penghuni.setAlamatAsal(rs.getString("alamat_asal"));
                
                penghunis.add(penghuni);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting all penghuni: " + ex.getMessage());
        }
        
        return penghunis;
    }
    
    public List<Penghuni> getPenghuniAktif() {
        String sql = "SELECT * FROM penghuni WHERE tanggal_keluar IS NULL ORDER BY nama_penghuni";
        List<Penghuni> penghunis = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Penghuni penghuni = new Penghuni();
                penghuni.setIdPenghuni(rs.getInt("id_penghuni"));
                penghuni.setIdKamar(rs.getInt("id_kamar"));
                penghuni.setNamaPenghuni(rs.getString("nama_penghuni"));
                penghuni.setTanggalMasuk(rs.getDate("tanggal_masuk"));
                penghuni.setTanggalKeluar(rs.getDate("tanggal_keluar"));
                penghuni.setKontak(rs.getString("kontak"));
                penghuni.setEmail(rs.getString("email"));
                penghuni.setAlamatAsal(rs.getString("alamat_asal"));
                
                penghunis.add(penghuni);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting penghuni aktif: " + ex.getMessage());
        }
        
        return penghunis;
    }
    
    public List<Penghuni> getPenghuniByTipeKamar(String tipeKamar) {
        String sql = "SELECT p.* FROM penghuni p " +
                    "JOIN kamars k ON p.id_kamar = k.id_kamar " +
                    "JOIN tipe_kamar tk ON k.id_tipe_kamar = tk.id_tipe_kamar " +
                    "WHERE tk.tipe_kamar = ? AND p.tanggal_keluar IS NULL " +
                    "ORDER BY p.nama_penghuni";
        List<Penghuni> penghunis = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipeKamar);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Penghuni penghuni = new Penghuni();
                penghuni.setIdPenghuni(rs.getInt("id_penghuni"));
                penghuni.setIdKamar(rs.getInt("id_kamar"));
                penghuni.setNamaPenghuni(rs.getString("nama_penghuni"));
                penghuni.setTanggalMasuk(rs.getDate("tanggal_masuk"));
                penghuni.setTanggalKeluar(rs.getDate("tanggal_keluar"));
                penghuni.setKontak(rs.getString("kontak"));
                penghuni.setEmail(rs.getString("email"));
                penghuni.setAlamatAsal(rs.getString("alamat_asal"));
                
                penghunis.add(penghuni);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting penghuni by tipe kamar: " + ex.getMessage());
        }
        
        return penghunis;
    }
    
    @Override
    public boolean update(Penghuni penghuni) {
        String sql = "UPDATE penghuni SET id_kamar = ?, nama_penghuni = ?, tanggal_masuk = ?, tanggal_keluar = ?, kontak = ?, email = ?, alamat_asal = ? WHERE id_penghuni = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, penghuni.getIdKamar());
            stmt.setString(2, penghuni.getNamaPenghuni());
            
            // Convert Date to SQL Date
            java.sql.Date sqlTanggalMasuk = new java.sql.Date(penghuni.getTanggalMasuk().getTime());
            stmt.setDate(3, sqlTanggalMasuk);
            
            // Handle nullable tanggal_keluar
            if (penghuni.getTanggalKeluar() != null) {
                java.sql.Date sqlTanggalKeluar = new java.sql.Date(penghuni.getTanggalKeluar().getTime());
                stmt.setDate(4, sqlTanggalKeluar);
            } else {
                stmt.setNull(4, Types.DATE);
            }
            
            stmt.setString(5, penghuni.getKontak());
            stmt.setString(6, penghuni.getEmail());
            stmt.setString(7, penghuni.getAlamatAsal());
            stmt.setInt(8, penghuni.getIdPenghuni());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error updating penghuni: " + ex.getMessage());
            return false;
        }
    }
    
    public boolean updateTanggalKeluar(int idPenghuni, java.util.Date tanggalKeluar) {
        String sql = "UPDATE penghuni SET tanggal_keluar = ? WHERE id_penghuni = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (tanggalKeluar != null) {
                java.sql.Date sqlTanggalKeluar = new java.sql.Date(tanggalKeluar.getTime());
                stmt.setDate(1, sqlTanggalKeluar);
            } else {
                stmt.setNull(1, Types.DATE);
            }
            
            stmt.setInt(2, idPenghuni);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error updating tanggal keluar: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM penghuni WHERE id_penghuni = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error deleting penghuni: " + ex.getMessage());
            return false;
        }
    }
    
    public int countPenghuniAktif() {
        String sql = "SELECT COUNT(*) FROM penghuni WHERE tanggal_keluar IS NULL";
        int count = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error counting penghuni aktif: " + ex.getMessage());
        }
        
        return count;
    }
    
    public boolean isKamarTerisi(int idKamar) {
        String sql = "SELECT COUNT(*) FROM penghuni WHERE id_kamar = ? AND tanggal_keluar IS NULL";
        int count = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idKamar);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error checking kamar terisi: " + ex.getMessage());
        }
        
        return count > 0;
    }
    public int countTotalPenghuniAktif() {
        String sql = "SELECT * FROM penghuni";
        int count = 0;

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                java.sql.Date tglKeluar = rs.getDate("tanggal_keluar");
                if (rs.wasNull() || tglKeluar == null) {
                    count++;
                }
                count++;
            }

        } catch (SQLException ex) {
            System.err.println("Error counting penghuni aktif manual: " + ex.getMessage());
        }

        return count;
    }
}