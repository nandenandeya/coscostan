package coscostan.model;

import java.util.Date;

public class Penghuni {
    private int idPenghuni;
    private int idKamar;
    private String namaPenghuni;
    private Date tanggalMasuk;
    private Date tanggalKeluar;
    private String kontak;
    private String email;
    private String alamatAsal;
    
    // Constructors
    public Penghuni() {}
    
    public Penghuni(int idKamar, String namaPenghuni, Date tanggalMasuk, String kontak, String email, String alamatAsal) {
        this.idKamar = idKamar;
        this.namaPenghuni = namaPenghuni;
        this.tanggalMasuk = tanggalMasuk;
        this.kontak = kontak;
        this.email = email;
        this.alamatAsal = alamatAsal;
    }
    
    // Getters and Setters
    public int getIdPenghuni() { return idPenghuni; }
    public void setIdPenghuni(int idPenghuni) { this.idPenghuni = idPenghuni; }
    
    public int getIdKamar() { return idKamar; }
    public void setIdKamar(int idKamar) { this.idKamar = idKamar; }
    
    public String getNamaPenghuni() { return namaPenghuni; }
    public void setNamaPenghuni(String namaPenghuni) { this.namaPenghuni = namaPenghuni; }
    
    public Date getTanggalMasuk() { return tanggalMasuk; }
    public void setTanggalMasuk(Date tanggalMasuk) { this.tanggalMasuk = tanggalMasuk; }
    
    public Date getTanggalKeluar() { return tanggalKeluar; }
    public void setTanggalKeluar(Date tanggalKeluar) { this.tanggalKeluar = tanggalKeluar; }
    
    public String getKontak() { return kontak; }
    public void setKontak(String kontak) { this.kontak = kontak; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAlamatAsal() { return alamatAsal; }
    public void setAlamatAsal(String alamatAsal) { this.alamatAsal = alamatAsal; }
    
    @Override
    public String toString() {
        return "Penghuni{" + "idPenghuni=" + idPenghuni + ", idKamar=" + idKamar + 
               ", namaPenghuni=" + namaPenghuni + ", tanggalMasuk=" + tanggalMasuk + 
               ", kontak=" + kontak + ", email=" + email + '}';
    }
}