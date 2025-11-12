package model;

import java.util.Date;

public class Penghuni {
    private int idPenghuni;
    private int idKamar;
    private Date tanggalMasuk;
    private Date tanggalKeluar;
    private String kontak;

    public Penghuni() {}

    public Penghuni(int idPenghuni, int idKamar, Date tanggalMasuk, Date tanggalKeluar, String kontak) {
        this.idPenghuni = idPenghuni;
        this.idKamar = idKamar;
        this.tanggalMasuk = tanggalMasuk;
        this.tanggalKeluar = tanggalKeluar;
        this.kontak = kontak;
    }

    // Getters and Setters
    public int getIdPenghuni() { return idPenghuni; }
    public void setIdPenghuni(int idPenghuni) { this.idPenghuni = idPenghuni; }

    public int getIdKamar() { return idKamar; }
    public void setIdKamar(int idKamar) { this.idKamar = idKamar; }

    public Date getTanggalMasuk() { return tanggalMasuk; }
    public void setTanggalMasuk(Date tanggalMasuk) { this.tanggalMasuk = tanggalMasuk; }

    public Date getTanggalKeluar() { return tanggalKeluar; }
    public void setTanggalKeluar(Date tanggalKeluar) { this.tanggalKeluar = tanggalKeluar; }

    public String getKontak() { return kontak; }
    public void setKontak(String kontak) { this.kontak = kontak; }
}