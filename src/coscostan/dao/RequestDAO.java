package dao;

import model.Request;
import util.DatabaseConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RequestDAO implements DAO<Request> {
    
    @Override
    public boolean insert(Request request) {
        String sql = "INSERT INTO request (id_kamar, nama_calon_penghuni, kontak, email, alamat_asal, status) VALUES (?, ?, ?, ?, ?, ?)";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            
            stmt.setInt(1, request.getIdKamar());
            stmt.setString(2, request.getNamaCalonPenghuni());
            stmt.setString(3, request.getKontak());
            stmt.setString(4, request.getEmail());
            stmt.setString(5, request.getAlamatAsal());
            stmt.setString(6, request.getStatus());
            
            int rowsAffected = stmt.executeUpdate();
            
            // Get generated ID
            if (rowsAffected > 0) {
                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        request.setIdRequest(generatedKeys.getInt(1));
                    }
                }
            }
            
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error inserting request: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public Request getById(int id) {
        String sql = "SELECT * FROM request WHERE id_request = ?";
        Request request = null;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            ResultSet rs = stmt.executeQuery();
            
            if (rs.next()) {
                request = extractRequestFromResultSet(rs);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting request by id: " + ex.getMessage());
        }
        
        return request;
    }
    
    @Override
    public List<Request> getAll() {
        String sql = "SELECT * FROM request ORDER BY tanggal_request DESC";
        List<Request> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Request request = extractRequestFromResultSet(rs);
                requests.add(request);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting all requests: " + ex.getMessage());
        }
        
        return requests;
    }
    
    public List<Request> getByStatus(String status) {
        String sql = "SELECT * FROM request WHERE status = ? ORDER BY tanggal_request DESC";
        List<Request> requests = new ArrayList<>();
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            ResultSet rs = stmt.executeQuery();
            
            while (rs.next()) {
                Request request = extractRequestFromResultSet(rs);
                requests.add(request);
            }
            
        } catch (SQLException ex) {
            System.err.println("Error getting requests by status: " + ex.getMessage());
        }
        
        return requests;
    }
    
    public List<Request> getPendingRequests() {
        return getByStatus("pending");
    }
    
    @Override
    public boolean update(Request request) {
        String sql = "UPDATE request SET id_penghuni = ?, id_kamar = ?, nama_calon_penghuni = ?, kontak = ?, email = ?, alamat_asal = ?, status = ? WHERE id_request = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            // Handle nullable id_penghuni
            if (request.getIdPenghuni() != null) {
                stmt.setInt(1, request.getIdPenghuni());
            } else {
                stmt.setNull(1, Types.INTEGER);
            }
            
            stmt.setInt(2, request.getIdKamar());
            stmt.setString(3, request.getNamaCalonPenghuni());
            stmt.setString(4, request.getKontak());
            stmt.setString(5, request.getEmail());
            stmt.setString(6, request.getAlamatAsal());
            stmt.setString(7, request.getStatus());
            stmt.setInt(8, request.getIdRequest());
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error updating request: " + ex.getMessage());
            return false;
        }
    }
    
    public boolean updateStatus(int idRequest, String status) {
        String sql = "UPDATE request SET status = ? WHERE id_request = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setString(1, status);
            stmt.setInt(2, idRequest);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error updating request status: " + ex.getMessage());
            return false;
        }
    }
    
    public boolean assignPenghuniToRequest(int idRequest, int idPenghuni) {
        String sql = "UPDATE request SET id_penghuni = ?, status = 'approved' WHERE id_request = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, idPenghuni);
            stmt.setInt(2, idRequest);
            
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error assigning penghuni to request: " + ex.getMessage());
            return false;
        }
    }
    
    @Override
    public boolean delete(int id) {
        String sql = "DELETE FROM request WHERE id_request = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            
            stmt.setInt(1, id);
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            
        } catch (SQLException ex) {
            System.err.println("Error deleting request: " + ex.getMessage());
            return false;
        }
    }
    
    private Request extractRequestFromResultSet(ResultSet rs) throws SQLException {
        Request request = new Request();
        request.setIdRequest(rs.getInt("id_request"));
        
        // Handle nullable id_penghuni
        int idPenghuni = rs.getInt("id_penghuni");
        if (!rs.wasNull()) {
            request.setIdPenghuni(idPenghuni);
        }
        
        request.setIdKamar(rs.getInt("id_kamar"));
        request.setNamaCalonPenghuni(rs.getString("nama_calon_penghuni"));
        request.setTanggalRequest(rs.getTimestamp("tanggal_request"));
        request.setKontak(rs.getString("kontak"));
        request.setEmail(rs.getString("email"));
        request.setAlamatAsal(rs.getString("alamat_asal"));
        request.setStatus(rs.getString("status"));
        
        return request;
    }
}