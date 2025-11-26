package dao;

import model.Admin;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdminDAO implements DAO<Admin> {
    
    @Override
    public boolean insert(Admin admin) {
        String sql = "INSERT INTO admin (admin_name, password, no_telepon, email) VALUES (?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, admin.getAdminName());
            stmt.setString(2, admin.getPassword());
            stmt.setString(3, admin.getNoTelepon());
            stmt.setString(4, admin.getEmail());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error inserting admin: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public Admin getById(int id) {
        String sql = "SELECT * FROM admin WHERE id_admin = ?";
        Admin admin = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                admin = new Admin();
                admin.setIdAdmin(rs.getInt("id_admin"));
                admin.setAdminName(rs.getString("admin_name"));
                admin.setPassword(rs.getString("password"));
                admin.setNoTelepon(rs.getString("no_telepon"));
                admin.setEmail(rs.getString("email"));
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting admin by id: " + ex.getMessage());
        }
        
        return admin;
    }
    
    public Admin getByUsername(String username) {
        String sql = "SELECT * FROM admin WHERE admin_name = ?";
        Admin admin = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                admin = new Admin();
                admin.setIdAdmin(rs.getInt("id_admin"));
                admin.setAdminName(rs.getString("admin_name"));
                admin.setPassword(rs.getString("password"));
                admin.setNoTelepon(rs.getString("no_telepon"));
                admin.setEmail(rs.getString("email"));
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting admin by username: " + ex.getMessage());
        }
        
        return admin;
    }
    
    @Override
    public List<Admin> getAll() {
        String sql = "SELECT * FROM admin";
        List<Admin> admins = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Admin admin = new Admin();
                admin.setIdAdmin(rs.getInt("id_admin"));
                admin.setAdminName(rs.getString("admin_name"));
                admin.setPassword(rs.getString("password"));
                admin.setNoTelepon(rs.getString("no_telepon"));
                admin.setEmail(rs.getString("email"));
                
                admins.add(admin);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting all admins: " + ex.getMessage());
        }
        
        return admins;
    }
    
    @Override
    public boolean update(Admin admin) {
        String sql = "UPDATE admin SET admin_name = ?, password = ?, no_telepon = ?, email = ? WHERE id_admin = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, admin.getAdminName());
            stmt.setString(2, admin.getPassword());
            stmt.setString(3, admin.getNoTelepon());
            stmt.setString(4, admin.getEmail());
            stmt.setInt(5, admin.getIdAdmin());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error updating admin: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM admin WHERE id_admin = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error deleting admin: " + ex.getMessage());
            return false;
        }
    }
    
    public boolean validateLogin(String username, String password) {
        String sql = "SELECT * FROM admin WHERE admin_name = ? AND password = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, username);
            stmt.setString(2, password);
            ResultSet rs = stmt.executeQuery();
            
            return rs.next(); // Return true jika ada data yang cocok
            
        } catch (SQLException ex) {
            System.err.println("Error validating login: " + ex.getMessage());
            return false;
        }
    }
}