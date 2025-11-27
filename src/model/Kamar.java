package model;

public class Kamar {
    private int idKamar;
    private int idTipeKamar;
    private Integer idPenghuni; // Integer karena bisa null
    private int nomorKamar;
    private String status;
    
    // Constructors
    public Kamar() {}
    
    public Kamar(int idTipeKamar, int nomorKamar, String status) {
        this.idTipeKamar = idTipeKamar;
        this.nomorKamar = nomorKamar;
        this.status = status;
    }
    
    // Getters and Setters
    public int getIdKamar() { return idKamar; }
    public void setIdKamar(int idKamar) { this.idKamar = idKamar; }
    
    public int getIdTipeKamar() { return idTipeKamar; }
    public void setIdTipeKamar(int idTipeKamar) { this.idTipeKamar = idTipeKamar; }
    
    public Integer getIdPenghuni() { return idPenghuni; }
    public void setIdPenghuni(Integer idPenghuni) { this.idPenghuni = idPenghuni; }
    
    public int getNomorKamar() { return nomorKamar; }
    public void setNomorKamar(int nomorKamar) { this.nomorKamar = nomorKamar; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    
    
    @Override
    public String toString() {
        return "Kamar{" + "idKamar=" + idKamar + ", idTipeKamar=" + idTipeKamar + 
               ", nomorKamar=" + nomorKamar + ", status=" + status + 
               ", idPenghuni=" + idPenghuni + '}';
    }
}