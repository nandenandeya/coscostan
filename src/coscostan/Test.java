/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package coscostan;

/**
 *
 * @author Nadya
 */
import java.sql.*;
public class Test{
    public static void main(String[] args) {
        String driver = "com.mysql.cj.jdbc.Driver";
        String db = "jdbc:mysql://localhost/daftar_kontak"; // Bila ada yang instance mysqlnya pindah port, tuliskan menjadi 'localhost:PORT/NAMA_DATABASE'
        String user = "root"; // Bila username berbeda, ganti baris ini
        String password = ""; // Bila instance MySQL memiliki password, isi baris ini
        Connection conn = null;
        Statement state = null;

        try{
            Class.forName(driver);
        } catch(Exception e){
            System.out.println("Driver Error"); 
        } 
        try{
            conn = (Connection)DriverManager.getConnection(db, user, password);
            state = (Statement) conn.createStatement();
        } catch(Exception e){
            System.out.println("Connection Error");
        }

        System.out.println("Database Connected");
        try{ 
            state.executeUpdate("MYSQL QUERY");
        } catch(Exception e){
            System.out.println("Error");
        }
    }
}
