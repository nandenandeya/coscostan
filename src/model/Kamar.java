package model;

public class Kamar {
    private int idKamar;
    private int idTipeKamar;
    private String nomorKamar;
    private String namaKamar;
    private int lantai;
    private String status;

    public Kamar() {}

    public Kamar(int idKamar, int idTipeKamar, String nomorKamar, String namaKamar, int lantai, String status) {
        this.idKamar = idKamar;
        this.idTipeKamar = idTipeKamar;
        this.nomorKamar = nomorKamar;
        this.namaKamar = namaKamar;
        this.lantai = lantai;
        this.status = status;
    }

    // Getters and Setters
    public int getIdKamar() { return idKamar; }
    public void setIdKamar(int idKamar) { this.idKamar = idKamar; }

    public int getIdTipeKamar() { return idTipeKamar; }
    public void setIdTipeKamar(int idTipeKamar) { this.idTipeKamar = idTipeKamar; }

    public String getNomorKamar() { return nomorKamar; }
    public void setNomorKamar(String nomorKamar) { this.nomorKamar = nomorKamar; }

    public String getNamaKamar() { return namaKamar; }
    public void setNamaKamar(String namaKamar) { this.namaKamar = namaKamar; }

    public int getLantai() { return lantai; }
    public void setLantai(int lantai) { this.lantai = lantai; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}