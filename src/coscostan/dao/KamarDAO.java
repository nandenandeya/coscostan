package coscostan.dao;

import coscostan.model.Kamar;
import coscostan.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class KamarDAO implements DAO<Kamar> {
    
    @Override
    public boolean insert(Kamar kamar) {
        String sql = "INSERT INTO kamars (id_tipe_kamar, nomor_kamar, status) VALUES (?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, kamar.getIdTipeKamar());
            stmt.setInt(2, kamar.getNomorKamar());
            stmt.setString(3, kamar.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error inserting kamar: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public Kamar getById(int id) {
        String sql = "SELECT * FROM kamars WHERE id_kamar = ?";
        Kamar kamar = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                kamar = new Kamar();
                kamar.setIdKamar(rs.getInt("id_kamar"));
                kamar.setIdTipeKamar(rs.getInt("id_tipe_kamar"));
                kamar.setNomorKamar(rs.getInt("nomor_kamar"));
                kamar.setStatus(rs.getString("status"));
                
                // Handle nullable id_penghuni
                int idPenghuni = rs.getInt("id_penghuni");
                if (!rs.wasNull()) {
                    kamar.setIdPenghuni(idPenghuni);
                }
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting kamar by id: " + ex.getMessage());
        }
        
        return kamar;
    }
    
    public Kamar getByNomorKamar(int nomorKamar) {
        String sql = "SELECT * FROM kamars WHERE nomor_kamar = ?";
        Kamar kamar = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, nomorKamar);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                kamar = new Kamar();
                kamar.setIdKamar(rs.getInt("id_kamar"));
                kamar.setIdTipeKamar(rs.getInt("id_tipe_kamar"));
                kamar.setNomorKamar(rs.getInt("nomor_kamar"));
                kamar.setStatus(rs.getString("status"));
                
                // Handle nullable id_penghuni
                int idPenghuni = rs.getInt("id_penghuni");
                if (!rs.wasNull()) {
                    kamar.setIdPenghuni(idPenghuni);
                }
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting kamar by nomor: " + ex.getMessage());
        }
        
        return kamar;
    }
    
    @Override
    public List<Kamar> getAll() {
        String sql = "SELECT * FROM kamars ORDER BY nomor_kamar";
        List<Kamar> kamars = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Kamar kamar = new Kamar();
                kamar.setIdKamar(rs.getInt("id_kamar"));
                kamar.setIdTipeKamar(rs.getInt("id_tipe_kamar"));
                kamar.setNomorKamar(rs.getInt("nomor_kamar"));
                kamar.setStatus(rs.getString("status"));
                
                // Handle nullable id_penghuni
                int idPenghuni = rs.getInt("id_penghuni");
                if (!rs.wasNull()) {
                    kamar.setIdPenghuni(idPenghuni);
                }
                
                kamars.add(kamar);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting all kamars: " + ex.getMessage());
        }
        
        return kamars;
    }
    
    public List<Kamar> getByStatus(String status) {
        String sql = "SELECT * FROM kamars WHERE status = ? ORDER BY nomor_kamar";
        List<Kamar> kamars = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Kamar kamar = new Kamar();
                kamar.setIdKamar(rs.getInt("id_kamar"));
                kamar.setIdTipeKamar(rs.getInt("id_tipe_kamar"));
                kamar.setNomorKamar(rs.getInt("nomor_kamar"));
                kamar.setStatus(rs.getString("status"));
                
                // Handle nullable id_penghuni
                int idPenghuni = rs.getInt("id_penghuni");
                if (!rs.wasNull()) {
                    kamar.setIdPenghuni(idPenghuni);
                }
                
                kamars.add(kamar);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting kamars by status: " + ex.getMessage());
        }
        
        return kamars;
    }
    
    public List<Kamar> getByTipeKamar(int idTipeKamar) {
        String sql = "SELECT * FROM kamars WHERE id_tipe_kamar = ? ORDER BY nomor_kamar";
        List<Kamar> kamars = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idTipeKamar);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Kamar kamar = new Kamar();
                kamar.setIdKamar(rs.getInt("id_kamar"));
                kamar.setIdTipeKamar(rs.getInt("id_tipe_kamar"));
                kamar.setNomorKamar(rs.getInt("nomor_kamar"));
                kamar.setStatus(rs.getString("status"));
                
                // Handle nullable id_penghuni
                int idPenghuni = rs.getInt("id_penghuni");
                if (!rs.wasNull()) {
                    kamar.setIdPenghuni(idPenghuni);
                }
                
                kamars.add(kamar);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting kamars by tipe: " + ex.getMessage());
        }
        
        return kamars;
    }
    
    @Override
    public boolean update(Kamar kamar) {
        String sql = "UPDATE kamars SET id_tipe_kamar = ?, nomor_kamar = ?, status = ?, id_penghuni = ? WHERE id_kamar = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, kamar.getIdTipeKamar());
            stmt.setInt(2, kamar.getNomorKamar());
            stmt.setString(3, kamar.getStatus());
            
            // Handle nullable id_penghuni
            if (kamar.getIdPenghuni() != null) {
                stmt.setInt(4, kamar.getIdPenghuni());
            } else {
                stmt.setNull(4, Types.INTEGER);
            }
            
            stmt.setInt(5, kamar.getIdKamar());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error updating kamar: " + ex.getMessage());
            return false;
        }
    }
    
    public boolean updateStatus(int idKamar, String status) {
        String sql = "UPDATE kamars SET status = ? WHERE id_kamar = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, idKamar);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error updating kamar status: " + ex.getMessage());
            return false;
        }
    }
    
    public boolean assignPenghuni(int idKamar, Integer idPenghuni) {
        String sql = "UPDATE kamars SET id_penghuni = ?, status = 'terisi' WHERE id_kamar = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            if (idPenghuni != null) {
                stmt.setInt(1, idPenghuni);
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            
            stmt.setInt(2, idKamar);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error assigning penghuni to kamar: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM kamars WHERE id_kamar = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error deleting kamar: " + ex.getMessage());
            return false;
        }
    }
    
    public int countByStatus(String status) {
        String sql = "SELECT COUNT(*) FROM kamars WHERE status = ?";
        int count = 0;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                count = rs.getInt(1);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error counting kamars by status: " + ex.getMessage());
        }
        
        return count;
    }
}