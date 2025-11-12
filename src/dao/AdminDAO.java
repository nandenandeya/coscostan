package dao;

import model.Admin;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import util.DatabaseConnection;

public class AdminDAO {
    private Connection connection;

    public AdminDAO() {
        connection = DatabaseConnection.getConnection();
    }

    public Admin login(String adminName, String password) {
        String sql = "SELECT * FROM admin WHERE admin_name = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, adminName);
            stmt.setString(2, password);
            
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return new Admin(
                    rs.getInt("id_admin"),
                    rs.getString("admin_name"),
                    rs.getString("password"),
                    rs.getString("no_telepon"),
                    rs.getString("email")
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Admin> getAllAdmins() {
        List<Admin> admins = new ArrayList<>();
        String sql = "SELECT * FROM admin";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                admins.add(new Admin(
                    rs.getInt("id_admin"),
                    rs.getString("admin_name"),
                    rs.getString("password"),
                    rs.getString("no_telepon"),
                    rs.getString("email")
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return admins;
    }
}