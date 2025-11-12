package model;

public class TipeKamar {
    private int idTipeKamar;
    private String ukuran;
    private String fasilitasKamar;
    private String tipeKamar;
    private double hargaSewa;
    private String lamaSewa;

    public TipeKamar() {}

    public TipeKamar(int idTipeKamar, String ukuran, String fasilitasKamar, String tipeKamar, double hargaSewa, String lamaSewa) {
        this.idTipeKamar = idTipeKamar;
        this.ukuran = ukuran;
        this.fasilitasKamar = fasilitasKamar;
        this.tipeKamar = tipeKamar;
        this.hargaSewa = hargaSewa;
        this.lamaSewa = lamaSewa;
    }

    // Getters and Setters
    public int getIdTipeKamar() { return idTipeKamar; }
    public void setIdTipeKamar(int idTipeKamar) { this.idTipeKamar = idTipeKamar; }

    public String getUkuran() { return ukuran; }
    public void setUkuran(String ukuran) { this.ukuran = ukuran; }

    public String getFasilitasKamar() { return fasilitasKamar; }
    public void setFasilitasKamar(String fasilitasKamar) { this.fasilitasKamar = fasilitasKamar; }

    public String getTipeKamar() { return tipeKamar; }
    public void setTipeKamar(String tipeKamar) { this.tipeKamar = tipeKamar; }

    public double getHargaSewa() { return hargaSewa; }
    public void setHargaSewa(double hargaSewa) { this.hargaSewa = hargaSewa; }

    public String getLamaSewa() { return lamaSewa; }
    public void setLamaSewa(String lamaSewa) { this.lamaSewa = lamaSewa; }
}