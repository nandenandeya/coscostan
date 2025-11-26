package coscostan.model;

public class Kamar {
    private int idKamar;
    private int idTipeKamar;
    private Integer idPenghuni; // Integer karena bisa null
    private int nomorKamar;
    private String status;
    
    // Field tambahan untuk UserDashboard (bisa null)
    private String tipeKamar;
    private Double harga;
    private String lamaSewa;
    private String ukuran;
    private String fasilitas;
    
    // Constructors
    public Kamar() {}
    
    public Kamar(int idTipeKamar, int nomorKamar, String status) {
        this.idTipeKamar = idTipeKamar;
        this.nomorKamar = nomorKamar;
        this.status = status;
    }
    
    // Getters and Setters yang sudah ada
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
    
    // Getters and Setters tambahan untuk UserDashboard
    public String getTipeKamar() { return tipeKamar; }
    public void setTipeKamar(String tipeKamar) { this.tipeKamar = tipeKamar; }
    
    public Double getHarga() { return harga; }
    public void setHarga(Double harga) { this.harga = harga; }
    
    public String getLamaSewa() { return lamaSewa; }
    public void setLamaSewa(String lamaSewa) { this.lamaSewa = lamaSewa; }
    
    public String getUkuran() { return ukuran; }
    public void setUkuran(String ukuran) { this.ukuran = ukuran; }
    
    public String getFasilitas() { return fasilitas; }
    public void setFasilitas(String fasilitas) { this.fasilitas = fasilitas; }
    
    @Override
    public String toString() {
        return "Kamar{" + "idKamar=" + idKamar + ", idTipeKamar=" + idTipeKamar + 
               ", nomorKamar=" + nomorKamar + ", status=" + status + 
               ", idPenghuni=" + idPenghuni + 
               ", tipeKamar=" + tipeKamar + ", harga=" + harga + 
               ", lamaSewa=" + lamaSewa + '}';
    }
}