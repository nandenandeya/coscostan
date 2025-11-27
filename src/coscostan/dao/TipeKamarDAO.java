package coscostan.dao;

import coscostan.model.TipeKamar;
import coscostan.util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TipeKamarDAO implements DAO<TipeKamar> {
    
    @Override
    public boolean insert(TipeKamar tipeKamar) {
        String sql = "INSERT INTO tipe_kamar (ukuran, fasilitas_kamar, tipe_kamar, harga_sewa, lama_sewa) VALUES (?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipeKamar.getUkuran());
            stmt.setString(2, tipeKamar.getFasilitasKamar());
            stmt.setString(3, tipeKamar.getTipeKamar());
            stmt.setDouble(4, tipeKamar.getHargaSewa());
            stmt.setString(5, tipeKamar.getLamaSewa());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error inserting tipe kamar: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public TipeKamar getById(int id) {
        String sql = "SELECT * FROM tipe_kamar WHERE id_tipe_kamar = ?";
        TipeKamar tipeKamar = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                tipeKamar = new TipeKamar();
                tipeKamar.setIdTipeKamar(rs.getInt("id_tipe_kamar"));
                tipeKamar.setUkuran(rs.getString("ukuran"));
                tipeKamar.setFasilitasKamar(rs.getString("fasilitas_kamar"));
                tipeKamar.setTipeKamar(rs.getString("tipe_kamar"));
                tipeKamar.setHargaSewa(rs.getDouble("harga_sewa"));
                tipeKamar.setLamaSewa(rs.getString("lama_sewa"));
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting tipe kamar by id: " + ex.getMessage());
        }
        
        return tipeKamar;
    }
    
    public TipeKamar getByTipe(String tipe) {
        String sql = "SELECT * FROM tipe_kamar WHERE tipe_kamar = ?";
        TipeKamar tipeKamar = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipe);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                tipeKamar = new TipeKamar();
                tipeKamar.setIdTipeKamar(rs.getInt("id_tipe_kamar"));
                tipeKamar.setUkuran(rs.getString("ukuran"));
                tipeKamar.setFasilitasKamar(rs.getString("fasilitas_kamar"));
                tipeKamar.setTipeKamar(rs.getString("tipe_kamar"));
                tipeKamar.setHargaSewa(rs.getDouble("harga_sewa"));
                tipeKamar.setLamaSewa(rs.getString("lama_sewa"));
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting tipe kamar by tipe: " + ex.getMessage());
        }
        
        return tipeKamar;
    }
    
    @Override
    public List<TipeKamar> getAll() {
        String sql = "SELECT * FROM tipe_kamar ORDER BY tipe_kamar";
        List<TipeKamar> tipeKamars = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                TipeKamar tipeKamar = new TipeKamar();
                tipeKamar.setIdTipeKamar(rs.getInt("id_tipe_kamar"));
                tipeKamar.setUkuran(rs.getString("ukuran"));
                tipeKamar.setFasilitasKamar(rs.getString("fasilitas_kamar"));
                tipeKamar.setTipeKamar(rs.getString("tipe_kamar"));
                tipeKamar.setHargaSewa(rs.getDouble("harga_sewa"));
                tipeKamar.setLamaSewa(rs.getString("lama_sewa"));
                
                tipeKamars.add(tipeKamar);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting all tipe kamar: " + ex.getMessage());
        }
        
        return tipeKamars;
    }
    
    @Override
    public boolean update(TipeKamar tipeKamar) {
        String sql = "UPDATE tipe_kamar SET ukuran = ?, fasilitas_kamar = ?, tipe_kamar = ?, harga_sewa = ?, lama_sewa = ? WHERE id_tipe_kamar = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, tipeKamar.getUkuran());
            stmt.setString(2, tipeKamar.getFasilitasKamar());
            stmt.setString(3, tipeKamar.getTipeKamar());
            stmt.setDouble(4, tipeKamar.getHargaSewa());
            stmt.setString(5, tipeKamar.getLamaSewa());
            stmt.setInt(6, tipeKamar.getIdTipeKamar());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error updating tipe kamar: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM tipe_kamar WHERE id_tipe_kamar = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error deleting tipe kamar: " + ex.getMessage());
            return false;
        }
    }
    
    public List<String> getAllTipeKamarOnly() {
        List<String> tipeList = new ArrayList<>();
        String sql = "SELECT tipe_kamar FROM tipe_kamar ORDER BY tipe_kamar";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                tipeList.add(rs.getString("tipe_kamar"));
            }

        } catch (SQLException ex) {
            System.err.println("Error getting tipe kamar only: " + ex.getMessage());
        }

        return tipeList;
    }
}