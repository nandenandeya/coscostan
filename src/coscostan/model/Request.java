package model;

import java.util.Date;

public class Request {
    private int idRequest;
    private Integer idPenghuni; // Bisa null
    private int idKamar;
    private String namaCalonPenghuni;
    private Date tanggalRequest;
    private String kontak;
    private String email;
    private String alamatAsal;
    private String status;
    
    // Constructors
    public Request() {}
    
    public Request(int idKamar, String namaCalonPenghuni, String kontak, String email, String alamatAsal) {
        this.idKamar = idKamar;
        this.namaCalonPenghuni = namaCalonPenghuni;
        this.kontak = kontak;
        this.email = email;
        this.alamatAsal = alamatAsal;
        this.status = "pending";
    }
    
    // Getters and Setters
    public int getIdRequest() { return idRequest; }
    public void setIdRequest(int idRequest) { this.idRequest = idRequest; }
    
    public Integer getIdPenghuni() { return idPenghuni; }
    public void setIdPenghuni(Integer idPenghuni) { this.idPenghuni = idPenghuni; }
    
    public int getIdKamar() { return idKamar; }
    public void setIdKamar(int idKamar) { this.idKamar = idKamar; }
    
    public String getNamaCalonPenghuni() { return namaCalonPenghuni; }
    public void setNamaCalonPenghuni(String namaCalonPenghuni) { this.namaCalonPenghuni = namaCalonPenghuni; }
    
    public Date getTanggalRequest() { return tanggalRequest; }
    public void setTanggalRequest(Date tanggalRequest) { this.tanggalRequest = tanggalRequest; }
    
    public String getKontak() { return kontak; }
    public void setKontak(String kontak) { this.kontak = kontak; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public String getAlamatAsal() { return alamatAsal; }
    public void setAlamatAsal(String alamatAsal) { this.alamatAsal = alamatAsal; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    @Override
    public String toString() {
        return "Request{" + "idRequest=" + idRequest + ", namaCalonPenghuni=" + namaCalonPenghuni + 
               ", idKamar=" + idKamar + ", status=" + status + '}';
    }
}