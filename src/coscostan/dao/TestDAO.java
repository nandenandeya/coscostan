package coscostan.dao;

import coscostan.model.Admin;
import coscostan.model.TipeKamar;
import coscostan.model.Kamar;
import coscostan.model.Penghuni;
import java.util.Date;
import java.util.List;

public class TestDAO {
    public static void main(String[] args) {
        System.out.println("=== TESTING DAO CLASSES ===");
        
        // Test AdminDAO
        System.out.println("\n--- Testing AdminDAO ---");
        AdminDAO adminDAO = new AdminDAO();
        
        List<Admin> admins = adminDAO.getAll();
        System.out.println("Total admins: " + admins.size());
        for (Admin admin : admins) {
            System.out.println("Admin: " + admin.getAdminName() + " - " + admin.getEmail());
        }
        
        Admin admin = adminDAO.getByUsername("admin1");
        if (admin != null) {
            System.out.println("Found admin: " + admin.getAdminName());
        }
        
        boolean isValid = adminDAO.validateLogin("admin1", "admin123");
        System.out.println("Login validation (admin1/admin123): " + isValid);
        
        // Test TipeKamarDAO
        System.out.println("\n--- Testing TipeKamarDAO ---");
        TipeKamarDAO tipeKamarDAO = new TipeKamarDAO();
        
        List<TipeKamar> tipeKamars = tipeKamarDAO.getAll();
        System.out.println("Total tipe kamar: " + tipeKamars.size());
        for (TipeKamar tk : tipeKamars) {
            System.out.println("Tipe: " + tk.getTipeKamar() + " - " + tk.getUkuran() + " - Rp " + tk.getHargaSewa());
        }
        
        TipeKamar tipeA = tipeKamarDAO.getByTipe("A");
        if (tipeA != null) {
            System.out.println("Tipe A: " + tipeA.getFasilitasKamar());
        }
        
        // Test KamarDAO
        System.out.println("\n--- Testing KamarDAO ---");
        KamarDAO kamarDAO = new KamarDAO();
        
        List<Kamar> kamars = kamarDAO.getAll();
        System.out.println("Total kamar: " + kamars.size());
        for (Kamar kamar : kamars) {
            System.out.println("Kamar: " + kamar.getNomorKamar() + " - Status: " + kamar.getStatus());
        }
        
        List<Kamar> kamarTersedia = kamarDAO.getByStatus("tersedia");
        System.out.println("Kamar tersedia: " + kamarTersedia.size());
        
        List<Kamar> kamarTerisi = kamarDAO.getByStatus("terisi");
        System.out.println("Kamar terisi: " + kamarTerisi.size());
        
        // Test PenghuniDAO
        System.out.println("\n--- Testing PenghuniDAO ---");
        PenghuniDAO penghuniDAO = new PenghuniDAO();
        
        List<Penghuni> penghunis = penghuniDAO.getAll();
        System.out.println("Total penghuni: " + penghunis.size());
        for (Penghuni penghuni : penghunis) {
            System.out.println("Penghuni: " + penghuni.getNamaPenghuni() + " - Kamar: " + penghuni.getIdKamar());
        }
        
        List<Penghuni> penghuniAktif = penghuniDAO.getPenghuniAktif();
        System.out.println("Penghuni aktif: " + penghuniAktif.size());
        
        int totalAktif = penghuniDAO.countPenghuniAktif();
        System.out.println("Total penghuni aktif: " + totalAktif);
        
        System.out.println("\n=== DAO TESTING COMPLETED ===");
    }
}