package gui;

import dao.KamarDAO;
import dao.PenghuniDAO;
import dao.TipeKamarDAO;
import model.Kamar;
import model.Penghuni;
import model.TipeKamar;
import javax.swing.table.DefaultTableModel;
import javax.swing.JOptionPane;
import javax.swing.JComboBox;
import javax.swing.DefaultCellEditor;
import java.util.List;

/**
 *
 * @author Nadya
 */
public class Admin extends javax.swing.JFrame {
    
    private AdminLogin parentForm;
    private String username;
    
    public Admin(AdminLogin parentForm, String username) {
        this.parentForm = parentForm;
        this.username = username;
        initComponents();
        setLocationRelativeTo(null);
        setTitle("Admin Dashboard - " + username);
        
        loadKamarDataToTable();
        updateDashboardStatistics();
        initTambahKamarForm();
        resetAllButtons();
        loadPenghuniDataToTable();     // Untuk tab statistik penghuni
        updatePenghuniStatistics();    // Untuk tab statistik penghuni
        
        initTambahPenghuniForm();
    setupRealTimePenghuniUpdate();
    
    // Inisialisasi form kelola penghuni
    setPenghuniButtonsState(false, false, false, false);
    setPenghuniFieldsEditable(false);
    
    initTabTipeKamar();
    setupTipeKamarTableListener();
    }
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(Admin.class.getName());

    /**
     * Creates new form Admin
     */
    public Admin() {
        initComponents();
        tipeKamarTable.addMouseListener(new java.awt.event.MouseAdapter() {
    @Override
    public void mouseClicked(java.awt.event.MouseEvent evt) {
        tipeKamarTableRowClicked();
    }
});
    }
    
    private void initTambahKamarForm() {
        // Kosongkan field input
    nomorKamarBaruLabel.setText("");
    statusKamarBaruComboBox.setSelectedIndex(0);
    
    // Load data tipe kamar ke combo box
    loadTipeKamarToComboBox();
    
    // Update label ID kamar
    updateIdKamarBaruLabel();
    }
   
    private void initTambahPenghuniForm() {
    // Kosongkan field input
    idKamarPenghuniField.setText("");
    namaPenghuniField.setText("");
    tanggalMasukField.setText("");
    kontakPenghuniField.setText("");
    emailPenghuniField.setText("");
    alamatAsalField.setText("");
    
    // Reset info labels
    tipeKamarInfoLabel.setText("-");
    nomorKamarInfoLabel.setText("-");
    hargaSewaInfoLabel.setText("-");
    lamaSewaInfoLabel.setText("-");
    tanggalKeluarInfoLabel.setText("-");
    
    // Update label ID penghuni
    updateIdPenghuniBaruLabel();
}
    
    private void updateIdPenghuniBaruLabel() {
    try {
        PenghuniDAO penghuniDAO = new PenghuniDAO();
        List<Penghuni> semuaPenghuni = penghuniDAO.getAll();
        
        // Cari ID tertinggi untuk predict next ID
        int maxId = 0;
        for (Penghuni penghuni : semuaPenghuni) {
            if (penghuni.getIdPenghuni() > maxId) {
                maxId = penghuni.getIdPenghuni();
            }
        }
        
        int nextId = maxId + 1;
        idPenghuniBaruLabel.setText("Auto generate: " + nextId);
        
    } catch (Exception ex) {
        System.err.println("Error in updateIdPenghuniBaruLabel: " + ex.getMessage());
        ex.printStackTrace();
        idPenghuniBaruLabel.setText("Auto generate: Error");
    }
}
    
    private boolean validateTambahPenghuniInput() {
    // Validasi ID Kamar
    String idKamarText = idKamarPenghuniField.getText().trim();
    if (idKamarText.isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "ID Kamar harus diisi!", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    int idKamar;
    try {
        idKamar = Integer.parseInt(idKamarText);
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, 
            "ID Kamar harus berupa angka!", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    // Cek apakah kamar ada
    KamarDAO kamarDAO = new KamarDAO();
    Kamar kamar = kamarDAO.getById(idKamar);
    if (kamar == null) {
        JOptionPane.showMessageDialog(this, 
            "Kamar dengan ID " + idKamar + " tidak ditemukan!", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    // Cek apakah kamar sudah terisi
    if (kamar.getStatus().equals("terisi")) {
        JOptionPane.showMessageDialog(this, 
            "Kamar ini sudah dihuni oleh penghuni lain!", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    // Cek apakah kamar dalam maintenance
    if (kamar.getStatus().equals("maintenance")) {
        JOptionPane.showMessageDialog(this, 
            "Kamar sedang dalam maintenance!", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    // Validasi Nama Penghuni
    if (namaPenghuniField.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Nama Penghuni harus diisi!", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    // Validasi Tanggal Masuk
    if (tanggalMasukField.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Tanggal Masuk harus diisi!", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    // Validasi Kontak
    if (kontakPenghuniField.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, 
            "Kontak harus diisi!", 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        return false;
    }
    
    return true;
}
    
    private void setupRealTimePenghuniUpdate() {
    // Auto-update info kamar ketika ID kamar diinput
    idKamarPenghuniField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { displayKamarInfo(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { displayKamarInfo(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { displayKamarInfo(); }
    });
    
    // Auto-update tanggal keluar ketika tanggal masuk diinput
    tanggalMasukField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
        public void insertUpdate(javax.swing.event.DocumentEvent e) { autoCalculateTanggalKeluar(); }
        public void removeUpdate(javax.swing.event.DocumentEvent e) { autoCalculateTanggalKeluar(); }
        public void changedUpdate(javax.swing.event.DocumentEvent e) { autoCalculateTanggalKeluar(); }
    });
}
    
    private void displayKamarInfo() {
    try {
        String idKamarText = idKamarPenghuniField.getText().trim();
        if (idKamarText.isEmpty()) {
            resetKamarInfo();
            return;
        }
        
        int idKamar = Integer.parseInt(idKamarText);
        KamarDAO kamarDAO = new KamarDAO();
        Kamar kamar = kamarDAO.getById(idKamar);
        
        if (kamar != null) {
            TipeKamarDAO tipeDAO = new TipeKamarDAO();
            TipeKamar tipe = tipeDAO.getById(kamar.getIdTipeKamar());
            
            if (tipe != null) {
                tipeKamarInfoLabel.setText("Tipe " + tipe.getTipeKamar());
                nomorKamarInfoLabel.setText(String.valueOf(kamar.getNomorKamar()));
                hargaSewaInfoLabel.setText("Rp " + String.format("%,.0f", tipe.getHargaSewa()));
                lamaSewaInfoLabel.setText(tipe.getLamaSewa());
                
                // Auto calculate tanggal keluar jika tanggal masuk sudah diisi
                autoCalculateTanggalKeluar();
            } else {
                resetKamarInfo();
            }
        } else {
            resetKamarInfo();
        }
        
    } catch (NumberFormatException ex) {
        resetKamarInfo();
    }
}

private void resetKamarInfo() {
    tipeKamarInfoLabel.setText("-");
    nomorKamarInfoLabel.setText("-");
    hargaSewaInfoLabel.setText("-");
    lamaSewaInfoLabel.setText("-");
    tanggalKeluarInfoLabel.setText("-");
}

private void autoCalculateTanggalKeluar() {
    try {
        String tanggalMasukStr = tanggalMasukField.getText().trim();
        String idKamarText = idKamarPenghuniField.getText().trim();
        
        if (tanggalMasukStr.isEmpty() || idKamarText.isEmpty()) {
            tanggalKeluarInfoLabel.setText("-");
            return;
        }
        
        // Parse tanggal masuk
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        java.util.Date tanggalMasuk = sdf.parse(tanggalMasukStr);
        
        // Ambil data kamar dan tipe kamar
        int idKamar = Integer.parseInt(idKamarText);
        KamarDAO kamarDAO = new KamarDAO();
        Kamar kamar = kamarDAO.getById(idKamar);
        
        if (kamar != null) {
            TipeKamarDAO tipeDAO = new TipeKamarDAO();
            TipeKamar tipe = tipeDAO.getById(kamar.getIdTipeKamar());
            
            if (tipe != null) {
                // Parse lama sewa (contoh: "6 bulan", "1 tahun")
                String lamaSewa = tipe.getLamaSewa().toLowerCase();
                java.util.Calendar cal = java.util.Calendar.getInstance();
                cal.setTime(tanggalMasuk);
                
                if (lamaSewa.contains("bulan")) {
                    // Extract angka dari string (misal: "6 bulan" -> 6)
                    int bulan = Integer.parseInt(lamaSewa.replaceAll("[^0-9]", ""));
                    cal.add(java.util.Calendar.MONTH, bulan);
                } else if (lamaSewa.contains("tahun")) {
                    int tahun = Integer.parseInt(lamaSewa.replaceAll("[^0-9]", ""));
                    cal.add(java.util.Calendar.YEAR, tahun);
                }
                
                java.util.Date tanggalKeluar = cal.getTime();
                tanggalKeluarInfoLabel.setText(sdf.format(tanggalKeluar));
            }
        }
        
    } catch (Exception ex) {
        tanggalKeluarInfoLabel.setText("-");
    }
}


    
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel2 = new javax.swing.JPanel();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel38 = new javax.swing.JPanel();
        jPanel45 = new javax.swing.JPanel();
        jLabel23 = new javax.swing.JLabel();
        jPanel46 = new javax.swing.JPanel();
        jLabel24 = new javax.swing.JLabel();
        jPanel47 = new javax.swing.JPanel();
        adminUsernameLabel = new javax.swing.JLabel();
        jPanel48 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        logoutButton = new javax.swing.JButton();
        btnRefreshDashboard = new javax.swing.JButton();
        jPanel49 = new javax.swing.JPanel();
        jLabel28 = new javax.swing.JLabel();
        totalPenghuniField = new javax.swing.JLabel();
        jPanel51 = new javax.swing.JPanel();
        totalKamarField = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jPanel52 = new javax.swing.JPanel();
        jLabel34 = new javax.swing.JLabel();
        totalTipeKamarField = new javax.swing.JLabel();
        jPanel53 = new javax.swing.JPanel();
        jLabel36 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jPanel56 = new javax.swing.JPanel();
        jPanel57 = new javax.swing.JPanel();
        jLabel25 = new javax.swing.JLabel();
        statistikKamar = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        kamarTerisiLabel = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        kamarKosongLabel = new javax.swing.JLabel();
        kamarMaintenanceLabel = new javax.swing.JLabel();
        jLabel94 = new javax.swing.JLabel();
        totalKamarLabel = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        kamarTable = new javax.swing.JTable();
        refresh = new javax.swing.JButton();
        jPanel4 = new javax.swing.JPanel();
        jPanel61 = new javax.swing.JPanel();
        jPanel62 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        idKamarBaruLabel = new javax.swing.JTextField();
        nomorKamarBaruLabel = new javax.swing.JTextField();
        batalTambahKamarButton = new javax.swing.JButton();
        tambahKamarButton = new javax.swing.JButton();
        statusKamarBaruComboBox = new javax.swing.JComboBox<>();
        IDTipeKamarBaruComboBox = new javax.swing.JComboBox<>();
        jPanel8 = new javax.swing.JPanel();
        cariIDKamar = new javax.swing.JTextField();
        jLabel16 = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        tabelHasilPencarian = new javax.swing.JTable();
        ubahKamarButton = new javax.swing.JButton();
        simpanPerubahanKamar = new javax.swing.JButton();
        batalPerubahan = new javax.swing.JButton();
        hapusKamarButton = new javax.swing.JButton();
        jTextField5 = new javax.swing.JTextField();
        cariKamarButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jPanel59 = new javax.swing.JPanel();
        statistikKamar1 = new javax.swing.JPanel();
        totalPenghuniLabel = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jScrollPane2 = new javax.swing.JScrollPane();
        penghuniTable = new javax.swing.JTable();
        jPanel58 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        refreshPenghuni = new javax.swing.JButton();
        jPanel10 = new javax.swing.JPanel();
        hasilCariIDPenghuniLabel1 = new javax.swing.JPanel();
        idPenghuniLabel1 = new javax.swing.JLabel();
        jScrollPane4 = new javax.swing.JScrollPane();
        tipeKamarTable = new javax.swing.JTable();
        jPanel64 = new javax.swing.JPanel();
        jLabel54 = new javax.swing.JLabel();
        tipeKamarInfoLabel1 = new javax.swing.JLabel();
        nomorKamarInfoLabel1 = new javax.swing.JLabel();
        hargaSewaInfoLabel1 = new javax.swing.JLabel();
        lamaSewaInfoLabel1 = new javax.swing.JLabel();
        tanggalKeluarInfoLabel1 = new javax.swing.JLabel();
        jPanel11 = new javax.swing.JPanel();
        jLabel55 = new javax.swing.JLabel();
        jLabel56 = new javax.swing.JLabel();
        jLabel57 = new javax.swing.JLabel();
        jLabel58 = new javax.swing.JLabel();
        jLabel59 = new javax.swing.JLabel();
        ukuranTipeKamarField = new javax.swing.JTextField();
        batalTambahTipeKamarButton = new javax.swing.JButton();
        tipeKamarField = new javax.swing.JTextField();
        hargaSewaField = new javax.swing.JTextField();
        jLabel61 = new javax.swing.JLabel();
        lamaSewaField = new javax.swing.JTextField();
        jLabel62 = new javax.swing.JLabel();
        simpanUbahTipeKamarButton = new javax.swing.JButton();
        ubahTipeKamarButton = new javax.swing.JButton();
        hapusTipeKamarButton = new javax.swing.JButton();
        jScrollPane3 = new javax.swing.JScrollPane();
        fasilitasTextArea = new javax.swing.JTextArea();
        idTipeKamarField = new javax.swing.JTextField();
        jPanel6 = new javax.swing.JPanel();
        jPanel63 = new javax.swing.JPanel();
        jLabel35 = new javax.swing.JLabel();
        tipeKamarInfoLabel = new javax.swing.JLabel();
        nomorKamarInfoLabel = new javax.swing.JLabel();
        hargaSewaInfoLabel = new javax.swing.JLabel();
        lamaSewaInfoLabel = new javax.swing.JLabel();
        tanggalKeluarInfoLabel = new javax.swing.JLabel();
        jPanel9 = new javax.swing.JPanel();
        jLabel10 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel17 = new javax.swing.JLabel();
        idPenghuniBaruLabel = new javax.swing.JTextField();
        namaPenghuniField = new javax.swing.JTextField();
        batalTambahPenghuniButton = new javax.swing.JButton();
        tambahPenghuniButton = new javax.swing.JButton();
        idKamarPenghuniField = new javax.swing.JTextField();
        tanggalMasukField = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        kontakPenghuniField = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        emailPenghuniField = new javax.swing.JTextField();
        jLabel20 = new javax.swing.JLabel();
        alamatAsalField = new javax.swing.JTextField();
        hasilCariIDPenghuniLabel = new javax.swing.JPanel();
        fieldCariPenghuni = new javax.swing.JTextField();
        jLabel18 = new javax.swing.JLabel();
        ubahPenghuniButton = new javax.swing.JButton();
        simpanPenghuniButton = new javax.swing.JButton();
        batalPenghuniButton = new javax.swing.JButton();
        hapusPenghuniButton = new javax.swing.JButton();
        jTextField6 = new javax.swing.JTextField();
        cariPenghuniButton = new javax.swing.JButton();
        jLabel21 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel37 = new javax.swing.JLabel();
        jLabel38 = new javax.swing.JLabel();
        jLabel39 = new javax.swing.JLabel();
        jLabel40 = new javax.swing.JLabel();
        jLabel41 = new javax.swing.JLabel();
        jLabel42 = new javax.swing.JLabel();
        idPenghuniLabel = new javax.swing.JLabel();
        idPenghuniField = new javax.swing.JTextField();
        namaField = new javax.swing.JTextField();
        idKamarPenghuniCariField = new javax.swing.JTextField();
        tipeKamarLabel = new javax.swing.JTextField();
        nomorKamarLabel = new javax.swing.JTextField();
        kontakField = new javax.swing.JTextField();
        emailField = new javax.swing.JTextField();
        alamatAsalField1 = new javax.swing.JTextField();
        tanggalMasukField1 = new javax.swing.JTextField();
        tanggalKeluarField = new javax.swing.JTextField();

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 997, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 611, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel38.setBackground(new java.awt.Color(9, 21, 25));

        jPanel45.setBackground(new java.awt.Color(22, 51, 61));
        jPanel45.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel45.setMaximumSize(new java.awt.Dimension(32767, 320));
        jPanel45.setPreferredSize(new java.awt.Dimension(133, 200));

        jLabel23.setBackground(new java.awt.Color(255, 255, 204));
        jLabel23.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel23.setForeground(new java.awt.Color(255, 255, 204));
        jLabel23.setText("COSCOSTAN");

        javax.swing.GroupLayout jPanel45Layout = new javax.swing.GroupLayout(jPanel45);
        jPanel45.setLayout(jPanel45Layout);
        jPanel45Layout.setHorizontalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel45Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel23)
                .addContainerGap(10, Short.MAX_VALUE))
        );
        jPanel45Layout.setVerticalGroup(
            jPanel45Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel45Layout.createSequentialGroup()
                .addGap(32, 32, 32)
                .addComponent(jLabel23, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(372, 372, 372))
        );

        jPanel46.setBackground(new java.awt.Color(22, 51, 61));

        jLabel24.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel24.setForeground(new java.awt.Color(255, 255, 204));
        jLabel24.setText("Welcome, Admin");

        javax.swing.GroupLayout jPanel46Layout = new javax.swing.GroupLayout(jPanel46);
        jPanel46.setLayout(jPanel46Layout);
        jPanel46Layout.setHorizontalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel46Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel24)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel46Layout.setVerticalGroup(
            jPanel46Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel46Layout.createSequentialGroup()
                .addGap(45, 45, 45)
                .addComponent(jLabel24)
                .addContainerGap(49, Short.MAX_VALUE))
        );

        jPanel47.setBackground(new java.awt.Color(5, 11, 14));
        jPanel47.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        adminUsernameLabel.setBackground(new java.awt.Color(255, 255, 204));
        adminUsernameLabel.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        adminUsernameLabel.setForeground(new java.awt.Color(255, 255, 204));
        adminUsernameLabel.setText("username admin");

        javax.swing.GroupLayout jPanel48Layout = new javax.swing.GroupLayout(jPanel48);
        jPanel48.setLayout(jPanel48Layout);
        jPanel48Layout.setHorizontalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 65, Short.MAX_VALUE)
        );
        jPanel48Layout.setVerticalGroup(
            jPanel48Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 63, Short.MAX_VALUE)
        );

        jLabel26.setBackground(new java.awt.Color(255, 255, 204));
        jLabel26.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(255, 255, 204));
        jLabel26.setText("Coscostan Admin");

        logoutButton.setText("Log Out");
        logoutButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                logoutButtonActionPerformed(evt);
            }
        });

        btnRefreshDashboard.setText("Refresh Data");
        btnRefreshDashboard.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnRefreshDashboardActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel47Layout = new javax.swing.GroupLayout(jPanel47);
        jPanel47.setLayout(jPanel47Layout);
        jPanel47Layout.setHorizontalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(logoutButton, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel47Layout.createSequentialGroup()
                        .addComponent(jPanel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jLabel26, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(adminUsernameLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 26, Short.MAX_VALUE))
                    .addComponent(btnRefreshDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel47Layout.setVerticalGroup(
            jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel47Layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel47Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel48, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jPanel47Layout.createSequentialGroup()
                        .addComponent(jLabel26)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(adminUsernameLabel)))
                .addGap(18, 18, 18)
                .addComponent(logoutButton, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(84, 84, 84)
                .addComponent(btnRefreshDashboard, javax.swing.GroupLayout.DEFAULT_SIZE, 113, Short.MAX_VALUE)
                .addGap(20, 20, 20))
        );

        jPanel49.setBackground(new java.awt.Color(5, 11, 14));
        jPanel49.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));
        jPanel49.setForeground(new java.awt.Color(204, 255, 204));

        jLabel28.setBackground(new java.awt.Color(255, 255, 204));
        jLabel28.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel28.setForeground(new java.awt.Color(255, 255, 204));
        jLabel28.setText("Total Penghuni");

        totalPenghuniField.setBackground(new java.awt.Color(255, 255, 204));
        totalPenghuniField.setFont(new java.awt.Font("SansSerif", 0, 48)); // NOI18N
        totalPenghuniField.setForeground(new java.awt.Color(255, 255, 204));
        totalPenghuniField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalPenghuniField.setText("0");

        javax.swing.GroupLayout jPanel49Layout = new javax.swing.GroupLayout(jPanel49);
        jPanel49.setLayout(jPanel49Layout);
        jPanel49Layout.setHorizontalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel49Layout.createSequentialGroup()
                .addGroup(jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addGap(46, 46, 46)
                        .addComponent(jLabel28))
                    .addGroup(jPanel49Layout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(totalPenghuniField)))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        jPanel49Layout.setVerticalGroup(
            jPanel49Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel49Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(totalPenghuniField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel28)
                .addGap(16, 16, 16))
        );

        jPanel51.setBackground(new java.awt.Color(5, 11, 14));
        jPanel51.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        totalKamarField.setBackground(new java.awt.Color(255, 255, 204));
        totalKamarField.setFont(new java.awt.Font("SansSerif", 0, 48)); // NOI18N
        totalKamarField.setForeground(new java.awt.Color(255, 255, 204));
        totalKamarField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalKamarField.setText("0");

        jLabel33.setBackground(new java.awt.Color(255, 255, 204));
        jLabel33.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel33.setForeground(new java.awt.Color(255, 255, 204));
        jLabel33.setText("Total Kamar");

        javax.swing.GroupLayout jPanel51Layout = new javax.swing.GroupLayout(jPanel51);
        jPanel51.setLayout(jPanel51Layout);
        jPanel51Layout.setHorizontalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel51Layout.createSequentialGroup()
                .addGroup(jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel51Layout.createSequentialGroup()
                        .addGap(58, 58, 58)
                        .addComponent(jLabel33))
                    .addGroup(jPanel51Layout.createSequentialGroup()
                        .addGap(94, 94, 94)
                        .addComponent(totalKamarField)))
                .addContainerGap(118, Short.MAX_VALUE))
        );
        jPanel51Layout.setVerticalGroup(
            jPanel51Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel51Layout.createSequentialGroup()
                .addContainerGap(22, Short.MAX_VALUE)
                .addComponent(totalKamarField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel33)
                .addGap(16, 16, 16))
        );

        jPanel52.setBackground(new java.awt.Color(5, 11, 14));
        jPanel52.setBorder(new javax.swing.border.SoftBevelBorder(javax.swing.border.BevelBorder.RAISED));

        jLabel34.setBackground(new java.awt.Color(255, 255, 204));
        jLabel34.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel34.setForeground(new java.awt.Color(255, 255, 204));
        jLabel34.setText("Tipe Kamar");

        totalTipeKamarField.setBackground(new java.awt.Color(255, 255, 204));
        totalTipeKamarField.setFont(new java.awt.Font("SansSerif", 0, 48)); // NOI18N
        totalTipeKamarField.setForeground(new java.awt.Color(255, 255, 204));
        totalTipeKamarField.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalTipeKamarField.setText("0");

        javax.swing.GroupLayout jPanel52Layout = new javax.swing.GroupLayout(jPanel52);
        jPanel52.setLayout(jPanel52Layout);
        jPanel52Layout.setHorizontalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel52Layout.createSequentialGroup()
                .addGap(173, 173, 173)
                .addGroup(jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel34)
                    .addGroup(jPanel52Layout.createSequentialGroup()
                        .addGap(34, 34, 34)
                        .addComponent(totalTipeKamarField)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel52Layout.setVerticalGroup(
            jPanel52Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel52Layout.createSequentialGroup()
                .addContainerGap(20, Short.MAX_VALUE)
                .addComponent(totalTipeKamarField)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel34)
                .addGap(18, 18, 18))
        );

        jPanel53.setBackground(new java.awt.Color(22, 51, 61));

        jLabel36.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel36.setForeground(new java.awt.Color(255, 255, 204));
        jLabel36.setText("Admin Profile");

        javax.swing.GroupLayout jPanel53Layout = new javax.swing.GroupLayout(jPanel53);
        jPanel53.setLayout(jPanel53Layout);
        jPanel53Layout.setHorizontalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel53Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addComponent(jLabel36)
                .addContainerGap(101, Short.MAX_VALUE))
        );
        jPanel53Layout.setVerticalGroup(
            jPanel53Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel53Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel36)
                .addContainerGap(19, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel38Layout = new javax.swing.GroupLayout(jPanel38);
        jPanel38.setLayout(jPanel38Layout);
        jPanel38Layout.setHorizontalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jPanel45, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addComponent(jPanel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jPanel51, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel46, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel52, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jPanel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(20, 20, 20))
            .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel38Layout.createSequentialGroup()
                    .addContainerGap(748, Short.MAX_VALUE)
                    .addComponent(jPanel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(13, 13, 13)))
        );
        jPanel38Layout.setVerticalGroup(
            jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel38Layout.createSequentialGroup()
                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel38Layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(jPanel47, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel38Layout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel38Layout.createSequentialGroup()
                                .addComponent(jPanel46, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jPanel49, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jPanel51, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(18, 18, 18)
                                .addComponent(jPanel52, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jPanel45, javax.swing.GroupLayout.DEFAULT_SIZE, 428, Short.MAX_VALUE))))
                .addContainerGap(355, Short.MAX_VALUE))
            .addGroup(jPanel38Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel38Layout.createSequentialGroup()
                    .addGap(16, 16, 16)
                    .addComponent(jPanel53, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(722, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel38, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel38, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("DashBoard", jPanel1);

        jPanel56.setBackground(new java.awt.Color(9, 21, 25));

        jPanel57.setBackground(new java.awt.Color(22, 51, 61));
        jPanel57.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel57.setMaximumSize(new java.awt.Dimension(32767, 320));
        jPanel57.setPreferredSize(new java.awt.Dimension(133, 200));

        jLabel25.setBackground(new java.awt.Color(255, 255, 204));
        jLabel25.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel25.setForeground(new java.awt.Color(255, 255, 204));
        jLabel25.setText("Coscostan");

        javax.swing.GroupLayout jPanel57Layout = new javax.swing.GroupLayout(jPanel57);
        jPanel57.setLayout(jPanel57Layout);
        jPanel57Layout.setHorizontalGroup(
            jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel57Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel25, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(41, 41, 41))
        );
        jPanel57Layout.setVerticalGroup(
            jPanel57Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel57Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(554, Short.MAX_VALUE))
        );

        statistikKamar.setBackground(new java.awt.Color(22, 51, 61));

        jLabel2.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 204));
        jLabel2.setText("Terisi");

        kamarTerisiLabel.setBackground(new java.awt.Color(255, 255, 204));
        kamarTerisiLabel.setFont(new java.awt.Font("SansSerif", 0, 48)); // NOI18N
        kamarTerisiLabel.setForeground(new java.awt.Color(255, 255, 204));
        kamarTerisiLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        kamarTerisiLabel.setText("0");

        jLabel7.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel7.setForeground(new java.awt.Color(255, 255, 204));
        jLabel7.setText("Kosong");

        kamarKosongLabel.setBackground(new java.awt.Color(255, 255, 204));
        kamarKosongLabel.setFont(new java.awt.Font("SansSerif", 0, 48)); // NOI18N
        kamarKosongLabel.setForeground(new java.awt.Color(255, 255, 204));
        kamarKosongLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        kamarKosongLabel.setText("0");

        kamarMaintenanceLabel.setBackground(new java.awt.Color(255, 255, 204));
        kamarMaintenanceLabel.setFont(new java.awt.Font("SansSerif", 0, 48)); // NOI18N
        kamarMaintenanceLabel.setForeground(new java.awt.Color(255, 255, 204));
        kamarMaintenanceLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        kamarMaintenanceLabel.setText("0");

        jLabel94.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel94.setForeground(new java.awt.Color(255, 255, 204));
        jLabel94.setText("Maintenance");

        totalKamarLabel.setBackground(new java.awt.Color(255, 255, 204));
        totalKamarLabel.setFont(new java.awt.Font("SansSerif", 0, 48)); // NOI18N
        totalKamarLabel.setForeground(new java.awt.Color(255, 255, 204));
        totalKamarLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalKamarLabel.setText("0");

        jLabel3.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel3.setForeground(new java.awt.Color(255, 255, 204));
        jLabel3.setText("Total Kamar");

        javax.swing.GroupLayout statistikKamarLayout = new javax.swing.GroupLayout(statistikKamar);
        statistikKamar.setLayout(statistikKamarLayout);
        statistikKamarLayout.setHorizontalGroup(
            statistikKamarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statistikKamarLayout.createSequentialGroup()
                .addGroup(statistikKamarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statistikKamarLayout.createSequentialGroup()
                        .addGap(92, 92, 92)
                        .addComponent(totalKamarLabel))
                    .addGroup(statistikKamarLayout.createSequentialGroup()
                        .addGap(52, 52, 52)
                        .addComponent(jLabel3)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 120, Short.MAX_VALUE)
                .addGroup(statistikKamarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addGroup(statistikKamarLayout.createSequentialGroup()
                        .addGap(16, 16, 16)
                        .addComponent(kamarTerisiLabel)))
                .addGap(120, 120, 120)
                .addGroup(statistikKamarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(statistikKamarLayout.createSequentialGroup()
                        .addComponent(kamarMaintenanceLabel)
                        .addGap(160, 160, 160)
                        .addComponent(kamarKosongLabel)
                        .addGap(113, 113, 113))
                    .addGroup(statistikKamarLayout.createSequentialGroup()
                        .addComponent(jLabel94)
                        .addGap(79, 79, 79)
                        .addComponent(jLabel7)
                        .addGap(84, 84, 84))))
        );
        statistikKamarLayout.setVerticalGroup(
            statistikKamarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statistikKamarLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(statistikKamarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(statistikKamarLayout.createSequentialGroup()
                        .addComponent(kamarKosongLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel7))
                    .addGroup(statistikKamarLayout.createSequentialGroup()
                        .addComponent(totalKamarLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel3))
                    .addGroup(statistikKamarLayout.createSequentialGroup()
                        .addGroup(statistikKamarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(kamarTerisiLabel, javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(kamarMaintenanceLabel, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(statistikKamarLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel94)
                            .addComponent(jLabel2))))
                .addContainerGap(26, Short.MAX_VALUE))
        );

        kamarTable.setForeground(new java.awt.Color(0, 102, 0));
        kamarTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null},
                {null, null, null, null, null}
            },
            new String [] {
                "ID Kamar", "Tipe Kamar", "Nomor Kamar", "ID Penghuni", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        kamarTable.setAlignmentX(3.0F);
        jScrollPane1.setViewportView(kamarTable);

        refresh.setText("Refresh Data");
        refresh.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel56Layout = new javax.swing.GroupLayout(jPanel56);
        jPanel56.setLayout(jPanel56Layout);
        jPanel56Layout.setHorizontalGroup(
            jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel56Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jPanel57, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(statistikKamar, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jScrollPane1))
                .addContainerGap(21, Short.MAX_VALUE))
        );
        jPanel56Layout.setVerticalGroup(
            jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel56Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel56Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel56Layout.createSequentialGroup()
                        .addComponent(statistikKamar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(refresh, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel57, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(139, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel56, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jPanel56, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Statistik Kamar", jPanel3);

        jPanel61.setBackground(new java.awt.Color(9, 21, 25));

        jPanel62.setBackground(new java.awt.Color(22, 51, 61));
        jPanel62.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel62.setMaximumSize(new java.awt.Dimension(32767, 320));
        jPanel62.setPreferredSize(new java.awt.Dimension(133, 200));

        jLabel32.setBackground(new java.awt.Color(255, 255, 204));
        jLabel32.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel32.setForeground(new java.awt.Color(255, 255, 204));
        jLabel32.setText("Coscostan");

        javax.swing.GroupLayout jPanel62Layout = new javax.swing.GroupLayout(jPanel62);
        jPanel62.setLayout(jPanel62Layout);
        jPanel62Layout.setHorizontalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel62Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel32, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(41, 41, 41))
        );
        jPanel62Layout.setVerticalGroup(
            jPanel62Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel62Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel32, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel7.setBackground(new java.awt.Color(22, 51, 61));

        jLabel4.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel4.setForeground(new java.awt.Color(242, 242, 242));
        jLabel4.setText("Tambahkan Kamar Baru");

        jLabel5.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel5.setForeground(new java.awt.Color(242, 242, 242));
        jLabel5.setText("ID Kamar          :");

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel6.setForeground(new java.awt.Color(242, 242, 242));
        jLabel6.setText("ID Tipe Kamar  :");

        jLabel8.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel8.setForeground(new java.awt.Color(242, 242, 242));
        jLabel8.setText("Nomor Kamar  :");

        jLabel9.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel9.setForeground(new java.awt.Color(242, 242, 242));
        jLabel9.setText("Status              : ");

        idKamarBaruLabel.setAlignmentX(2.0F);
        idKamarBaruLabel.setEnabled(false);
        idKamarBaruLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idKamarBaruLabelActionPerformed(evt);
            }
        });

        nomorKamarBaruLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nomorKamarBaruLabelActionPerformed(evt);
            }
        });

        batalTambahKamarButton.setText("Batal");
        batalTambahKamarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batalTambahKamarButtonActionPerformed(evt);
            }
        });

        tambahKamarButton.setText("Tambah");
        tambahKamarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahKamarButtonActionPerformed(evt);
            }
        });

        statusKamarBaruComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<status kamar>", "tersedia", "terisi", "maintenance" }));

        IDTipeKamarBaruComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "<tipe>", "Tipe A", "Tipe B", "Tipe C", "Tipe D" }));
        IDTipeKamarBaruComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                IDTipeKamarBaruComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel4)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel5)
                                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED))
                                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                                        .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 111, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addGap(3, 3, 3)))
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(idKamarBaruLabel)
                                    .addComponent(IDTipeKamarBaruComboBox, 0, 166, Short.MAX_VALUE))
                                .addGap(46, 46, 46)
                                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, 104, Short.MAX_VALUE)))
                            .addGroup(jPanel7Layout.createSequentialGroup()
                                .addComponent(tambahKamarButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(batalTambahKamarButton)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(nomorKamarBaruLabel, javax.swing.GroupLayout.DEFAULT_SIZE, 166, Short.MAX_VALUE)
                            .addComponent(statusKamarBaruComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel9)
                        .addComponent(statusKamarBaruComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jLabel4)
                        .addGap(26, 26, 26)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel5)
                            .addComponent(jLabel8)
                            .addComponent(idKamarBaruLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nomorKamarBaruLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(8, 8, 8)
                        .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel6)
                            .addComponent(IDTipeKamarBaruComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addGap(18, 18, 18)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tambahKamarButton)
                    .addComponent(batalTambahKamarButton))
                .addContainerGap(32, Short.MAX_VALUE))
        );

        jPanel8.setBackground(new java.awt.Color(22, 51, 61));

        cariIDKamar.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        cariIDKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariIDKamarActionPerformed(evt);
            }
        });

        jLabel16.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel16.setForeground(new java.awt.Color(225, 225, 225));
        jLabel16.setText("Hasil Pencarian Kamar");

        tabelHasilPencarian.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null}
            },
            new String [] {
                "ID Kamar", "Tipe Kamar", "Nomor Kamar", "ID Penghuni", "Status"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, true
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane8.setViewportView(tabelHasilPencarian);

        ubahKamarButton.setText("Ubah");
        ubahKamarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ubahKamarButtonActionPerformed(evt);
            }
        });

        simpanPerubahanKamar.setText("Simpan");
        simpanPerubahanKamar.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanPerubahanKamarActionPerformed(evt);
            }
        });

        batalPerubahan.setText("Batal");
        batalPerubahan.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batalPerubahanActionPerformed(evt);
            }
        });

        hapusKamarButton.setText("Hapus");
        hapusKamarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusKamarButtonActionPerformed(evt);
            }
        });

        jTextField5.setBackground(new java.awt.Color(5, 11, 14));
        jTextField5.setForeground(new java.awt.Color(102, 102, 102));
        jTextField5.setText("Cari kamar berdasarkan ID Kamar");
        jTextField5.setEnabled(false);
        jTextField5.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField5ActionPerformed(evt);
            }
        });

        cariKamarButton.setText("Cari");
        cariKamarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariKamarButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(29, 29, 29)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jLabel16)
                        .addGap(442, 442, 442))
                    .addGroup(jPanel8Layout.createSequentialGroup()
                        .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(ubahKamarButton)
                                .addGap(18, 18, 18)
                                .addComponent(simpanPerubahanKamar)
                                .addGap(18, 18, 18)
                                .addComponent(batalPerubahan)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(hapusKamarButton))
                            .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 788, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel8Layout.createSequentialGroup()
                                .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cariIDKamar, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(cariKamarButton)))
                        .addContainerGap(21, Short.MAX_VALUE))))
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel8Layout.createSequentialGroup()
                .addContainerGap(25, Short.MAX_VALUE)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField5, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cariIDKamar, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cariKamarButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel16)
                .addGap(10, 10, 10)
                .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, 62, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ubahKamarButton)
                    .addComponent(simpanPerubahanKamar)
                    .addComponent(batalPerubahan)
                    .addComponent(hapusKamarButton))
                .addGap(62, 62, 62))
        );

        javax.swing.GroupLayout jPanel61Layout = new javax.swing.GroupLayout(jPanel61);
        jPanel61.setLayout(jPanel61Layout);
        jPanel61Layout.setHorizontalGroup(
            jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel61Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jPanel62, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(49, Short.MAX_VALUE))
        );
        jPanel61Layout.setVerticalGroup(
            jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel61Layout.createSequentialGroup()
                .addContainerGap(16, Short.MAX_VALUE)
                .addGroup(jPanel61Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel62, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                    .addGroup(jPanel61Layout.createSequentialGroup()
                        .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(50, 50, 50))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel61, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel61, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Kelola Kamar", jPanel4);

        jPanel59.setBackground(new java.awt.Color(9, 21, 25));

        statistikKamar1.setBackground(new java.awt.Color(22, 51, 61));

        totalPenghuniLabel.setBackground(new java.awt.Color(255, 255, 204));
        totalPenghuniLabel.setFont(new java.awt.Font("SansSerif", 0, 48)); // NOI18N
        totalPenghuniLabel.setForeground(new java.awt.Color(255, 255, 204));
        totalPenghuniLabel.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        totalPenghuniLabel.setText("0");

        jLabel12.setFont(new java.awt.Font("SansSerif", 0, 24)); // NOI18N
        jLabel12.setForeground(new java.awt.Color(255, 255, 204));
        jLabel12.setText("Total Penghuni");

        jLabel13.setFont(new java.awt.Font("SansSerif", 1, 48)); // NOI18N
        jLabel13.setForeground(new java.awt.Color(255, 255, 204));
        jLabel13.setText("Statistik Penghuni");

        javax.swing.GroupLayout statistikKamar1Layout = new javax.swing.GroupLayout(statistikKamar1);
        statistikKamar1.setLayout(statistikKamar1Layout);
        statistikKamar1Layout.setHorizontalGroup(
            statistikKamar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statistikKamar1Layout.createSequentialGroup()
                .addGap(69, 69, 69)
                .addComponent(jLabel13)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statistikKamar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel12, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, statistikKamar1Layout.createSequentialGroup()
                        .addComponent(totalPenghuniLabel)
                        .addGap(67, 67, 67)))
                .addGap(51, 51, 51))
        );
        statistikKamar1Layout.setVerticalGroup(
            statistikKamar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statistikKamar1Layout.createSequentialGroup()
                .addGroup(statistikKamar1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(statistikKamar1Layout.createSequentialGroup()
                        .addGap(18, 18, 18)
                        .addComponent(jLabel12)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(totalPenghuniLabel))
                    .addGroup(statistikKamar1Layout.createSequentialGroup()
                        .addGap(35, 35, 35)
                        .addComponent(jLabel13)))
                .addContainerGap(23, Short.MAX_VALUE))
        );

        penghuniTable.setForeground(new java.awt.Color(0, 102, 0));
        penghuniTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null},
                {null, null, null, null, null, null, null, null}
            },
            new String [] {
                "ID Penghuni", "ID Kamar", "Nama Penghuni", "Telepon", "Email", "Alamat Asal", "Tanggal Masuk", "Tanggal Keluar"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        penghuniTable.setAlignmentX(3.0F);
        jScrollPane2.setViewportView(penghuniTable);

        jPanel58.setBackground(new java.awt.Color(22, 51, 61));
        jPanel58.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel58.setMaximumSize(new java.awt.Dimension(32767, 320));
        jPanel58.setPreferredSize(new java.awt.Dimension(133, 200));

        jLabel27.setBackground(new java.awt.Color(255, 255, 204));
        jLabel27.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel27.setForeground(new java.awt.Color(255, 255, 204));
        jLabel27.setText("Coscostan");

        javax.swing.GroupLayout jPanel58Layout = new javax.swing.GroupLayout(jPanel58);
        jPanel58.setLayout(jPanel58Layout);
        jPanel58Layout.setHorizontalGroup(
            jPanel58Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel58Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(41, 41, 41))
        );
        jPanel58Layout.setVerticalGroup(
            jPanel58Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel58Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel27, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(554, Short.MAX_VALUE))
        );

        refreshPenghuni.setText("Refresh Data");
        refreshPenghuni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                refreshPenghuniActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel59Layout = new javax.swing.GroupLayout(jPanel59);
        jPanel59.setLayout(jPanel59Layout);
        jPanel59Layout.setHorizontalGroup(
            jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel59Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jPanel58, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 875, Short.MAX_VALUE)
                        .addComponent(statistikKamar1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(refreshPenghuni, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel59Layout.setVerticalGroup(
            jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel59Layout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel59Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel59Layout.createSequentialGroup()
                        .addComponent(statistikKamar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(71, 71, 71)
                        .addComponent(refreshPenghuni, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel58, javax.swing.GroupLayout.PREFERRED_SIZE, 627, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(139, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel59, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jPanel59, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Statistik Penghuni", jPanel5);

        hasilCariIDPenghuniLabel1.setBackground(new java.awt.Color(22, 51, 61));

        idPenghuniLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        idPenghuniLabel1.setForeground(new java.awt.Color(242, 242, 242));
        idPenghuniLabel1.setText("-");

        tipeKamarTable.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null},
                {null, null, null, null, null, null}
            },
            new String [] {
                "ID Tipe Kamar", "Tipe ", "Ukuran", "Harga Sewa", "Lama Sewa", "Fasilitas"
            }
        ) {
            boolean[] canEdit = new boolean [] {
                false, false, false, false, false, false
            };

            public boolean isCellEditable(int rowIndex, int columnIndex) {
                return canEdit [columnIndex];
            }
        });
        jScrollPane4.setViewportView(tipeKamarTable);

        javax.swing.GroupLayout hasilCariIDPenghuniLabel1Layout = new javax.swing.GroupLayout(hasilCariIDPenghuniLabel1);
        hasilCariIDPenghuniLabel1.setLayout(hasilCariIDPenghuniLabel1Layout);
        hasilCariIDPenghuniLabel1Layout.setHorizontalGroup(
            hasilCariIDPenghuniLabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hasilCariIDPenghuniLabel1Layout.createSequentialGroup()
                .addGroup(hasilCariIDPenghuniLabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(hasilCariIDPenghuniLabel1Layout.createSequentialGroup()
                        .addGap(161, 161, 161)
                        .addComponent(idPenghuniLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(hasilCariIDPenghuniLabel1Layout.createSequentialGroup()
                        .addGap(15, 15, 15)
                        .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 747, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(23, Short.MAX_VALUE))
        );
        hasilCariIDPenghuniLabel1Layout.setVerticalGroup(
            hasilCariIDPenghuniLabel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hasilCariIDPenghuniLabel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(390, 390, 390)
                .addComponent(idPenghuniLabel1)
                .addContainerGap(185, Short.MAX_VALUE))
        );

        jPanel64.setBackground(new java.awt.Color(22, 51, 61));
        jPanel64.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel64.setMaximumSize(new java.awt.Dimension(32767, 320));
        jPanel64.setPreferredSize(new java.awt.Dimension(133, 200));

        jLabel54.setBackground(new java.awt.Color(255, 255, 204));
        jLabel54.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel54.setForeground(new java.awt.Color(255, 255, 204));
        jLabel54.setText("Coscostan");

        tipeKamarInfoLabel1.setBackground(new java.awt.Color(22, 51, 61));
        tipeKamarInfoLabel1.setForeground(new java.awt.Color(22, 51, 61));
        tipeKamarInfoLabel1.setText("jLabel48");

        nomorKamarInfoLabel1.setBackground(new java.awt.Color(22, 51, 61));
        nomorKamarInfoLabel1.setForeground(new java.awt.Color(22, 51, 61));
        nomorKamarInfoLabel1.setText("jLabel47");

        hargaSewaInfoLabel1.setBackground(new java.awt.Color(22, 51, 61));
        hargaSewaInfoLabel1.setForeground(new java.awt.Color(22, 51, 61));
        hargaSewaInfoLabel1.setText("jLabel47");

        lamaSewaInfoLabel1.setBackground(new java.awt.Color(22, 51, 61));
        lamaSewaInfoLabel1.setForeground(new java.awt.Color(22, 51, 61));
        lamaSewaInfoLabel1.setText("jLabel47");

        tanggalKeluarInfoLabel1.setBackground(new java.awt.Color(22, 51, 61));
        tanggalKeluarInfoLabel1.setForeground(new java.awt.Color(22, 51, 61));
        tanggalKeluarInfoLabel1.setText("jLabel47");

        javax.swing.GroupLayout jPanel64Layout = new javax.swing.GroupLayout(jPanel64);
        jPanel64.setLayout(jPanel64Layout);
        jPanel64Layout.setHorizontalGroup(
            jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel64Layout.createSequentialGroup()
                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel64Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tipeKamarInfoLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nomorKamarInfoLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel64Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(lamaSewaInfoLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel64Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel64Layout.createSequentialGroup()
                        .addComponent(tanggalKeluarInfoLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel64Layout.createSequentialGroup()
                        .addComponent(hargaSewaInfoLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel64Layout.createSequentialGroup()
                        .addComponent(jLabel54, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(41, 41, 41))))
        );
        jPanel64Layout.setVerticalGroup(
            jPanel64Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel64Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel54, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(388, 388, 388)
                .addComponent(tanggalKeluarInfoLabel1)
                .addGap(18, 18, 18)
                .addComponent(lamaSewaInfoLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hargaSewaInfoLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nomorKamarInfoLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tipeKamarInfoLabel1)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel11.setBackground(new java.awt.Color(22, 51, 61));

        jLabel55.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel55.setForeground(new java.awt.Color(242, 242, 242));
        jLabel55.setText("TIPE KAMAR");

        jLabel56.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel56.setForeground(new java.awt.Color(242, 242, 242));
        jLabel56.setText("ID Tipe Kamar          :");

        jLabel57.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel57.setForeground(new java.awt.Color(242, 242, 242));
        jLabel57.setText("Tipe Kamar               :");

        jLabel58.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel58.setForeground(new java.awt.Color(242, 242, 242));
        jLabel58.setText("Ukuran                     :");

        jLabel59.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel59.setForeground(new java.awt.Color(242, 242, 242));
        jLabel59.setText("Harga Sewa         : ");

        ukuranTipeKamarField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ukuranTipeKamarFieldActionPerformed(evt);
            }
        });

        batalTambahTipeKamarButton.setText("Batal");
        batalTambahTipeKamarButton.setEnabled(false);
        batalTambahTipeKamarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batalTambahTipeKamarButtonActionPerformed(evt);
            }
        });

        tipeKamarField.setEditable(false);

        hargaSewaField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hargaSewaFieldActionPerformed(evt);
            }
        });

        jLabel61.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel61.setForeground(new java.awt.Color(242, 242, 242));
        jLabel61.setText("Lama Sewa          :");

        lamaSewaField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                lamaSewaFieldActionPerformed(evt);
            }
        });

        jLabel62.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel62.setForeground(new java.awt.Color(242, 242, 242));
        jLabel62.setText("Fasilitas                :");

        simpanUbahTipeKamarButton.setText("Simpan");
        simpanUbahTipeKamarButton.setEnabled(false);
        simpanUbahTipeKamarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanUbahTipeKamarButtonActionPerformed(evt);
            }
        });

        ubahTipeKamarButton.setText("Ubah");
        ubahTipeKamarButton.setEnabled(false);
        ubahTipeKamarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ubahTipeKamarButtonActionPerformed(evt);
            }
        });

        hapusTipeKamarButton.setText("Hapus");
        hapusTipeKamarButton.setEnabled(false);
        hapusTipeKamarButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusTipeKamarButtonActionPerformed(evt);
            }
        });

        fasilitasTextArea.setColumns(20);
        fasilitasTextArea.setRows(5);
        jScrollPane3.setViewportView(fasilitasTextArea);

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel56)
                                    .addComponent(jLabel57))
                                .addGap(18, 18, 18)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(tipeKamarField, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                                    .addComponent(idTipeKamarField))
                                .addGap(39, 39, 39)
                                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                    .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(jLabel59)
                                        .addComponent(jLabel61, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE))
                                    .addGroup(jPanel11Layout.createSequentialGroup()
                                        .addComponent(jLabel62)
                                        .addGap(4, 4, 4))))
                            .addGroup(jPanel11Layout.createSequentialGroup()
                                .addComponent(batalTambahTipeKamarButton)
                                .addGap(18, 18, 18)
                                .addComponent(ubahTipeKamarButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(simpanUbahTipeKamarButton)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(hapusTipeKamarButton)))
                        .addGap(20, 20, 20)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(hargaSewaField, javax.swing.GroupLayout.DEFAULT_SIZE, 162, Short.MAX_VALUE)
                                .addComponent(lamaSewaField))
                            .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(jLabel55)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addComponent(jLabel58, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(ukuranTipeKamarField, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(97, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel55)
                .addGap(18, 18, 18)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel56)
                    .addComponent(hargaSewaField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel59)
                    .addComponent(idTipeKamarField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel57)
                    .addComponent(tipeKamarField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel61)
                    .addComponent(lamaSewaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel11Layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                .addComponent(ukuranTipeKamarField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(jLabel62))
                            .addComponent(jLabel58))
                        .addGap(41, 41, 41)
                        .addGroup(jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(batalTambahTipeKamarButton)
                            .addComponent(simpanUbahTipeKamarButton)
                            .addComponent(ubahTipeKamarButton)
                            .addComponent(hapusTipeKamarButton))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel11Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 30, Short.MAX_VALUE))))
        );

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel64, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hasilCariIDPenghuniLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(120, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel64, javax.swing.GroupLayout.DEFAULT_SIZE, 1179, Short.MAX_VALUE)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(hasilCariIDPenghuniLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("Tipe Kamar", jPanel10);

        jPanel63.setBackground(new java.awt.Color(22, 51, 61));
        jPanel63.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jPanel63.setMaximumSize(new java.awt.Dimension(32767, 320));
        jPanel63.setPreferredSize(new java.awt.Dimension(133, 200));

        jLabel35.setBackground(new java.awt.Color(255, 255, 204));
        jLabel35.setFont(new java.awt.Font("SansSerif", 0, 18)); // NOI18N
        jLabel35.setForeground(new java.awt.Color(255, 255, 204));
        jLabel35.setText("Coscostan");

        tipeKamarInfoLabel.setBackground(new java.awt.Color(0, 51, 51));
        tipeKamarInfoLabel.setForeground(new java.awt.Color(0, 51, 51));
        tipeKamarInfoLabel.setText("jLabel48");

        nomorKamarInfoLabel.setBackground(new java.awt.Color(0, 51, 51));
        nomorKamarInfoLabel.setForeground(new java.awt.Color(0, 51, 51));
        nomorKamarInfoLabel.setText("jLabel47");

        hargaSewaInfoLabel.setBackground(new java.awt.Color(0, 51, 51));
        hargaSewaInfoLabel.setForeground(new java.awt.Color(0, 51, 51));
        hargaSewaInfoLabel.setText("jLabel47");

        lamaSewaInfoLabel.setBackground(new java.awt.Color(0, 51, 51));
        lamaSewaInfoLabel.setForeground(new java.awt.Color(0, 51, 51));
        lamaSewaInfoLabel.setText("jLabel47");

        tanggalKeluarInfoLabel.setBackground(new java.awt.Color(0, 51, 51));
        tanggalKeluarInfoLabel.setForeground(new java.awt.Color(0, 51, 51));
        tanggalKeluarInfoLabel.setText("jLabel47");

        javax.swing.GroupLayout jPanel63Layout = new javax.swing.GroupLayout(jPanel63);
        jPanel63.setLayout(jPanel63Layout);
        jPanel63Layout.setHorizontalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel63Layout.createSequentialGroup()
                .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tipeKamarInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nomorKamarInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addGap(26, 26, 26)
                        .addComponent(lamaSewaInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel63Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addComponent(tanggalKeluarInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addComponent(hargaSewaInfoLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel63Layout.createSequentialGroup()
                        .addComponent(jLabel35, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(41, 41, 41))))
        );
        jPanel63Layout.setVerticalGroup(
            jPanel63Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel63Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel35, javax.swing.GroupLayout.PREFERRED_SIZE, 67, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(388, 388, 388)
                .addComponent(tanggalKeluarInfoLabel)
                .addGap(18, 18, 18)
                .addComponent(lamaSewaInfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hargaSewaInfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(nomorKamarInfoLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tipeKamarInfoLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel9.setBackground(new java.awt.Color(22, 51, 61));

        jLabel10.setFont(new java.awt.Font("Segoe UI", 1, 24)); // NOI18N
        jLabel10.setForeground(new java.awt.Color(242, 242, 242));
        jLabel10.setText("Tambahkan Kamar Baru");

        jLabel11.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel11.setForeground(new java.awt.Color(242, 242, 242));
        jLabel11.setText("ID Penghuni             :");

        jLabel14.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel14.setForeground(new java.awt.Color(242, 242, 242));
        jLabel14.setText("ID Kamar Penghuni  :");

        jLabel15.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel15.setForeground(new java.awt.Color(242, 242, 242));
        jLabel15.setText("Nama Penghuni       :");

        jLabel17.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel17.setForeground(new java.awt.Color(242, 242, 242));
        jLabel17.setText("Tanggal Masuk   : ");

        idPenghuniBaruLabel.setEditable(false);
        idPenghuniBaruLabel.setAlignmentX(2.0F);
        idPenghuniBaruLabel.setEnabled(false);
        idPenghuniBaruLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                idPenghuniBaruLabelActionPerformed(evt);
            }
        });

        namaPenghuniField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                namaPenghuniFieldActionPerformed(evt);
            }
        });

        batalTambahPenghuniButton.setText("Batal");
        batalTambahPenghuniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batalTambahPenghuniButtonActionPerformed(evt);
            }
        });

        tambahPenghuniButton.setText("Tambah");
        tambahPenghuniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tambahPenghuniButtonActionPerformed(evt);
            }
        });

        tanggalMasukField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tanggalMasukFieldActionPerformed(evt);
            }
        });

        jLabel19.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel19.setForeground(new java.awt.Color(242, 242, 242));
        jLabel19.setText("Kontak                : ");

        kontakPenghuniField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                kontakPenghuniFieldActionPerformed(evt);
            }
        });

        jLabel1.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel1.setForeground(new java.awt.Color(242, 242, 242));
        jLabel1.setText("Email                   :");

        jLabel20.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel20.setForeground(new java.awt.Color(242, 242, 242));
        jLabel20.setText("Alamat Asal         :");

        alamatAsalField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                alamatAsalFieldActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(28, 28, 28)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel11)
                            .addComponent(jLabel14))
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(17, 17, 17)
                                .addComponent(idPenghuniBaruLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addGap(18, 18, 18)
                                .addComponent(idKamarPenghuniField, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(43, 43, 43)
                        .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel17)
                                .addGap(18, 18, 18)
                                .addComponent(tanggalMasukField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                .addGroup(jPanel9Layout.createSequentialGroup()
                                    .addComponent(jLabel20)
                                    .addGap(18, 18, 18)
                                    .addComponent(alamatAsalField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(jPanel9Layout.createSequentialGroup()
                                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 114, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addGap(18, 18, 18)
                                    .addComponent(emailPenghuniField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel9Layout.createSequentialGroup()
                                .addComponent(jLabel19)
                                .addGap(18, 18, 18)
                                .addComponent(kontakPenghuniField, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE))))
                    .addComponent(jLabel10)
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(tambahPenghuniButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(batalTambahPenghuniButton))
                    .addGroup(jPanel9Layout.createSequentialGroup()
                        .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(namaPenghuniField, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jLabel10)
                .addGap(18, 18, 18)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel11)
                    .addComponent(idPenghuniBaruLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(tanggalMasukField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel17))
                .addGap(8, 8, 8)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14)
                    .addComponent(idKamarPenghuniField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1)
                    .addComponent(emailPenghuniField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(6, 6, 6)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(namaPenghuniField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel20)
                        .addComponent(alamatAsalField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel15))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19)
                    .addComponent(kontakPenghuniField, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(12, 12, 12)
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(tambahPenghuniButton)
                    .addComponent(batalTambahPenghuniButton))
                .addGap(0, 26, Short.MAX_VALUE))
        );

        hasilCariIDPenghuniLabel.setBackground(new java.awt.Color(22, 51, 61));

        fieldCariPenghuni.setHorizontalAlignment(javax.swing.JTextField.CENTER);
        fieldCariPenghuni.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fieldCariPenghuniActionPerformed(evt);
            }
        });

        jLabel18.setFont(new java.awt.Font("Segoe UI", 1, 14)); // NOI18N
        jLabel18.setForeground(new java.awt.Color(225, 225, 225));
        jLabel18.setText("Hasil Pencarian Kamar");

        ubahPenghuniButton.setText("Ubah");
        ubahPenghuniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                ubahPenghuniButtonActionPerformed(evt);
            }
        });

        simpanPenghuniButton.setText("Simpan");
        simpanPenghuniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                simpanPenghuniButtonActionPerformed(evt);
            }
        });

        batalPenghuniButton.setText("Batal");
        batalPenghuniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                batalPenghuniButtonActionPerformed(evt);
            }
        });

        hapusPenghuniButton.setText("Hapus");
        hapusPenghuniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hapusPenghuniButtonActionPerformed(evt);
            }
        });

        jTextField6.setBackground(new java.awt.Color(5, 11, 14));
        jTextField6.setForeground(new java.awt.Color(102, 102, 102));
        jTextField6.setText("Cari kamar berdasarkan ID Penghuni");
        jTextField6.setEnabled(false);
        jTextField6.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField6ActionPerformed(evt);
            }
        });

        cariPenghuniButton.setText("Cari");
        cariPenghuniButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cariPenghuniButtonActionPerformed(evt);
            }
        });

        jLabel21.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel21.setForeground(new java.awt.Color(242, 242, 242));
        jLabel21.setText("ID Penghuni             :");

        jLabel22.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel22.setForeground(new java.awt.Color(242, 242, 242));
        jLabel22.setText("ID Kamar Penghuni  :");

        jLabel29.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel29.setForeground(new java.awt.Color(242, 242, 242));
        jLabel29.setText("Tipe Kamar               :");

        jLabel31.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel31.setForeground(new java.awt.Color(242, 242, 242));
        jLabel31.setText("Nomor Kamar          :");

        jLabel37.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel37.setForeground(new java.awt.Color(242, 242, 242));
        jLabel37.setText("Nama Penghuni       :");

        jLabel38.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel38.setForeground(new java.awt.Color(242, 242, 242));
        jLabel38.setText("Kontak                     :");

        jLabel39.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel39.setForeground(new java.awt.Color(242, 242, 242));
        jLabel39.setText("Alamat Asal              :");

        jLabel40.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel40.setForeground(new java.awt.Color(242, 242, 242));
        jLabel40.setText("Tanggal Masuk         :");

        jLabel41.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel41.setForeground(new java.awt.Color(242, 242, 242));
        jLabel41.setText("Tanggal Keluar          :");

        jLabel42.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        jLabel42.setForeground(new java.awt.Color(242, 242, 242));
        jLabel42.setText("Email                        :");

        idPenghuniLabel.setFont(new java.awt.Font("Segoe UI", 0, 14)); // NOI18N
        idPenghuniLabel.setForeground(new java.awt.Color(242, 242, 242));
        idPenghuniLabel.setText("-");

        idPenghuniField.setEditable(false);

        namaField.setEditable(false);

        idKamarPenghuniCariField.setEditable(false);

        tipeKamarLabel.setEditable(false);

        nomorKamarLabel.setEditable(false);
        nomorKamarLabel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nomorKamarLabelActionPerformed(evt);
            }
        });

        kontakField.setEditable(false);

        emailField.setEditable(false);

        alamatAsalField1.setEditable(false);

        tanggalMasukField1.setEditable(false);

        tanggalKeluarField.setEditable(false);

        javax.swing.GroupLayout hasilCariIDPenghuniLabelLayout = new javax.swing.GroupLayout(hasilCariIDPenghuniLabel);
        hasilCariIDPenghuniLabel.setLayout(hasilCariIDPenghuniLabelLayout);
        hasilCariIDPenghuniLabelLayout.setHorizontalGroup(
            hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                        .addGap(28, 28, 28)
                        .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                                .addGap(177, 177, 177)
                                .addComponent(jLabel18))
                            .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                                .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                                            .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 212, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(fieldCariPenghuni, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                            .addComponent(cariPenghuniButton))
                                        .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                                            .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                                                    .addComponent(jLabel21)
                                                    .addGap(22, 22, 22))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                                                    .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                        .addComponent(jLabel37)
                                                        .addComponent(jLabel22)
                                                        .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE))
                                                    .addGap(12, 12, 12))
                                                .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                                                    .addComponent(jLabel31)
                                                    .addGap(18, 18, 18)))
                                            .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                                .addComponent(namaField, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(idPenghuniField, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(idKamarPenghuniCariField, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(tipeKamarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                                .addComponent(nomorKamarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE))
                                            .addGap(172, 172, 172))
                                        .addGroup(javax.swing.GroupLayout.Alignment.LEADING, hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                                            .addGap(4, 4, 4)
                                            .addComponent(ubahPenghuniButton)
                                            .addGap(18, 18, 18)
                                            .addComponent(simpanPenghuniButton)
                                            .addGap(18, 18, 18)
                                            .addComponent(batalPenghuniButton)
                                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                            .addComponent(hapusPenghuniButton)))
                                    .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                                        .addGap(375, 375, 375)
                                        .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                            .addComponent(jLabel38)
                                            .addComponent(jLabel39)
                                            .addComponent(jLabel40, javax.swing.GroupLayout.PREFERRED_SIZE, 143, javax.swing.GroupLayout.PREFERRED_SIZE)
                                            .addComponent(jLabel41)
                                            .addComponent(jLabel42))))
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(kontakField, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(alamatAsalField1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tanggalMasukField1, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(tanggalKeluarField, javax.swing.GroupLayout.PREFERRED_SIZE, 175, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                    .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                        .addGap(161, 161, 161)
                        .addComponent(idPenghuniLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 166, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(36, Short.MAX_VALUE))
        );
        hasilCariIDPenghuniLabelLayout.setVerticalGroup(
            hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                .addGap(36, 36, 36)
                .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField6, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(fieldCariPenghuni, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cariPenghuniButton, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel18)
                .addGap(12, 12, 12)
                .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                        .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel21)
                            .addComponent(idPenghuniField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel37)
                            .addComponent(namaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel22)
                            .addComponent(idKamarPenghuniCariField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(4, 4, 4)
                        .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(jLabel29)
                            .addComponent(tipeKamarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(nomorKamarLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel31)))
                    .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                        .addComponent(jLabel38)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel42)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jLabel39)
                        .addGap(6, 6, 6)
                        .addComponent(jLabel40)
                        .addGap(4, 4, 4)
                        .addComponent(jLabel41))
                    .addGroup(hasilCariIDPenghuniLabelLayout.createSequentialGroup()
                        .addComponent(kontakField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(emailField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(alamatAsalField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(4, 4, 4)
                        .addComponent(tanggalMasukField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(tanggalKeluarField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(18, 18, 18)
                .addGroup(hasilCariIDPenghuniLabelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(ubahPenghuniButton)
                    .addComponent(simpanPenghuniButton)
                    .addComponent(batalPenghuniButton)
                    .addComponent(hapusPenghuniButton))
                .addGap(239, 239, 239)
                .addComponent(idPenghuniLabel)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel63, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(hasilCariIDPenghuniLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(136, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel63, javax.swing.GroupLayout.DEFAULT_SIZE, 1179, Short.MAX_VALUE)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(hasilCariIDPenghuniLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(378, 378, 378))
        );

        jTabbedPane1.addTab("Kelola Penghuni", jPanel6);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addComponent(jTabbedPane1)
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 621, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void logoutButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_logoutButtonActionPerformed
        parentForm.setVisible(true);
        this.dispose();
    }//GEN-LAST:event_logoutButtonActionPerformed

    private void hapusKamarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusKamarButtonActionPerformed
        if (currentKamarId <= 0) {
            JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int confirm = JOptionPane.showConfirmDialog(this, 
            "Yakin hapus kamar ID " + currentKamarId + "?\nData yang dihapus tidak dapat dikembalikan!", 
            "Konfirmasi Hapus", 
            JOptionPane.YES_NO_OPTION);

        if (confirm == JOptionPane.YES_OPTION) {
            try {
                KamarDAO kamarDAO = new KamarDAO();
                boolean success = kamarDAO.delete(currentKamarId);

                if (success) {
                    JOptionPane.showMessageDialog(this, "Data berhasil dihapus!", "Success", JOptionPane.INFORMATION_MESSAGE);

                    // Reset form
                    resetSearchForm();
                    resetAllButtons();

                    // Refresh data utama
                    loadKamarDataToTable();
                } else {
                    JOptionPane.showMessageDialog(this, "Gagal menghapus data!", "Error", JOptionPane.ERROR_MESSAGE);
                }

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        }
    }//GEN-LAST:event_hapusKamarButtonActionPerformed

    private void batalPerubahanActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batalPerubahanActionPerformed
        // Reload data asli dari database
        if (currentKamarId > 0) {
            // Panggil lagi method cari untuk reload data original
            cariIDKamar.setText(String.valueOf(currentKamarId));
            cariKamarButtonActionPerformed(evt);
        }

        // Kembalikan ke state normal
        resetTableToReadOnly();
        setEditDeleteButtonsEnabled(true);
        setSaveCancelButtonsEnabled(false);

        JOptionPane.showMessageDialog(this, "Perubahan dibatalkan", "Info", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_batalPerubahanActionPerformed

    private void simpanPerubahanKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpanPerubahanKamarActionPerformed
        try {
            DefaultTableModel model = (DefaultTableModel) tabelHasilPencarian.getModel();

            if (model.getRowCount() == 0) {
                JOptionPane.showMessageDialog(this, "Tidak ada data untuk disimpan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Ambil data dari tabel
            int idKamar = (int) model.getValueAt(0, 0);
            String newStatus = (String) model.getValueAt(0, 4);

            // Validasi status
            if (!newStatus.equals("tersedia") && !newStatus.equals("terisi") && !newStatus.equals("maintenance")) {
                JOptionPane.showMessageDialog(this, "Status tidak valid!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Update ke database
            KamarDAO kamarDAO = new KamarDAO();
            boolean success = kamarDAO.updateStatus(idKamar, newStatus);

            if (success) {
                JOptionPane.showMessageDialog(this, "Data berhasil diupdate!", "Success", JOptionPane.INFORMATION_MESSAGE);

                // Kembalikan ke state normal
                resetTableToReadOnly();
                setEditDeleteButtonsEnabled(true);
                setSaveCancelButtonsEnabled(false);

                // Refresh data utama
                loadKamarDataToTable();
            } else {
                JOptionPane.showMessageDialog(this, "Gagal update data!", "Error", JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_simpanPerubahanKamarActionPerformed

    private void ubahKamarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ubahKamarButtonActionPerformed
        // Aktifkan mode edit - buat kolom status menjadi editable
        DefaultTableModel model = (DefaultTableModel) tabelHasilPencarian.getModel();

        // Buat combo box untuk kolom status
        JComboBox<String> statusComboBox = new JComboBox<>(new String[]{"tersedia", "terisi", "maintenance"});

        // Set cell editor untuk kolom status (kolom index 4)
        tabelHasilPencarian.getColumnModel().getColumn(4).setCellEditor(new DefaultCellEditor(statusComboBox));

        // Aktifkan button simpan/batal, non-aktifkan edit/hapus
        setEditDeleteButtonsEnabled(false);
        setSaveCancelButtonsEnabled(true);

        JOptionPane.showMessageDialog(this, "Mode Edit: Klik pada kolom Status untuk mengubah", "Edit Mode", JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_ubahKamarButtonActionPerformed

    private void cariIDKamarActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariIDKamarActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_cariIDKamarActionPerformed

    private void jTextField5ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField5ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField5ActionPerformed

    private void idKamarBaruLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idKamarBaruLabelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idKamarBaruLabelActionPerformed

    private void nomorKamarBaruLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nomorKamarBaruLabelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nomorKamarBaruLabelActionPerformed

    private void tambahKamarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahKamarButtonActionPerformed

    if (!validateTambahKamarInput()) {
        return;
    }
    
    try {
        // Ambil data dari form
        int idTipeKamar = getSelectedTipeKamarId();
        int nomorKamar = Integer.parseInt(nomorKamarBaruLabel.getText().trim());
        String status = (String) statusKamarBaruComboBox.getSelectedItem();
        String selectedTipe = (String) IDTipeKamarBaruComboBox.getSelectedItem();
        
        // Debug: Check predicted ID
        String labelText = idKamarBaruLabel.getText();
        System.out.println("Label text: " + labelText); // Debug
        
        // Handle case jika label menunjukkan error
        if (labelText.contains("Error")) {
            JOptionPane.showMessageDialog(this, 
                "Tidak dapat memprediksi ID. Silakan refresh form.", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Extract predicted ID dengan lebih aman
        String predictedIdText = labelText.replace("Auto generate : ", "").trim();
        System.out.println("Predicted ID text: " + predictedIdText); // Debug
        
        int predictedId;
        try {
            predictedId = Integer.parseInt(predictedIdText);
        } catch (NumberFormatException ex) {
            // Jika gagal parse, gunakan prediksi sederhana
            predictedId = 0; // Akan diganti oleh auto-increment database
        }
        
        // Konfirmasi sebelum save (tanpa predicted ID jika 0)
        String confirmMessage = "Konfirmasi Tambah Kamar:\n" +
            "Tipe Kamar: " + selectedTipe + "\n" +
            "Nomor Kamar: " + nomorKamar + "\n" +
            "Status: " + status + "\n\n";
            
        if (predictedId > 0) {
            confirmMessage = "ID Kamar: " + predictedId + " (Auto generate)\n" + confirmMessage;
        }
        
        confirmMessage += "Apakah data sudah benar?";
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            confirmMessage,
            "Konfirmasi Tambah Kamar",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Buat object Kamar baru
        Kamar kamarBaru = new Kamar(idTipeKamar, nomorKamar, status);
        
        // Simpan ke database
        KamarDAO kamarDAO = new KamarDAO();
        boolean success = kamarDAO.insert(kamarBaru);
        
        if (success) {
            // Get actual ID yang di-generate oleh database
            Kamar kamarYangBaruDitambah = kamarDAO.getByNomorKamar(nomorKamar);
            int actualId = kamarYangBaruDitambah != null ? kamarYangBaruDitambah.getIdKamar() : 0;
            
            String successMessage = "Kamar berhasil ditambahkan!\n" +
                "Tipe Kamar: " + selectedTipe + "\n" +
                "Nomor Kamar: " + nomorKamar + "\n" +
                "Status: " + status;
                
            if (actualId > 0) {
                successMessage = "ID Kamar: " + actualId + "\n" + successMessage;
            }
            
            JOptionPane.showMessageDialog(this, 
                successMessage, 
                "Success", 
                JOptionPane.INFORMATION_MESSAGE);
            
            // Reset form
            initTambahKamarForm();
            
            // Refresh data di tab statistik kamar
            loadKamarDataToTable();
            
            // Refresh dashboard
            updateDashboardStatistics();
            
        } else {
            JOptionPane.showMessageDialog(this, 
                "Gagal menambah kamar!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "Error: " + ex.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    }//GEN-LAST:event_tambahKamarButtonActionPerformed

    private void batalTambahKamarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batalTambahKamarButtonActionPerformed
        int confirm = JOptionPane.showConfirmDialog(this, 
        "Yakin ingin membatalkan? Data yang sudah diinput akan hilang.", 
        "Konfirmasi Batal", 
        JOptionPane.YES_NO_OPTION);
    
        if (confirm == JOptionPane.YES_OPTION) {
            initTambahKamarForm();
            JOptionPane.showMessageDialog(this, 
                "Form telah direset", 
                "Info", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_batalTambahKamarButtonActionPerformed

    private void cariKamarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariKamarButtonActionPerformed
        try {
            String idText = cariIDKamar.getText().trim();

            if (idText.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Masukkan ID Kamar!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            int idKamar = Integer.parseInt(idText);
            KamarDAO kamarDAO = new KamarDAO();
            TipeKamarDAO tipeKamarDAO = new TipeKamarDAO();
            PenghuniDAO penghuniDAO = new PenghuniDAO();

            // Cari kamar by ID
            Kamar kamar = kamarDAO.getById(idKamar);

            if (kamar == null) {
                // Kosongkan tabel hasil
                DefaultTableModel model = (DefaultTableModel) tabelHasilPencarian.getModel();
                model.setRowCount(0);

                // Non-aktifkan button edit/hapus
                setEditDeleteButtonsEnabled(false);
                JOptionPane.showMessageDialog(this, "Kamar dengan ID " + idKamar + " tidak ditemukan!", "Not Found", JOptionPane.WARNING_MESSAGE);
                return;
            }

            // Kamar ditemukan, tampilkan di tabel hasil
            DefaultTableModel model = (DefaultTableModel) tabelHasilPencarian.getModel();
            model.setRowCount(0); // Clear previous results

            // Get additional data
            TipeKamar tipe = tipeKamarDAO.getById(kamar.getIdTipeKamar());
            String namaPenghuni = "Kosong";
            if (kamar.getIdPenghuni() != null) {
                Penghuni penghuni = penghuniDAO.getById(kamar.getIdPenghuni());
                if (penghuni != null) {
                    namaPenghuni = penghuni.getNamaPenghuni();
                }
            }

            // Add to results table
            model.addRow(new Object[]{
                kamar.getIdKamar(),
                "Tipe " + tipe.getTipeKamar(),
                namaPenghuni,
                kamar.getNomorKamar(),
                kamar.getStatus()
            });

            // Aktifkan button edit/hapus
            setEditDeleteButtonsEnabled(true);
            setSaveCancelButtonsEnabled(false);

            // Simpan ID kamar yang sedang dicari untuk operasi selanjutnya
            currentKamarId = idKamar;

        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "ID Kamar harus angka!", "Error", JOptionPane.ERROR_MESSAGE);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }//GEN-LAST:event_cariKamarButtonActionPerformed

    private void refreshActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshActionPerformed
        updateDashboardStatistics();
    }//GEN-LAST:event_refreshActionPerformed

    private void IDTipeKamarBaruComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_IDTipeKamarBaruComboBoxActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_IDTipeKamarBaruComboBoxActionPerformed

    private void refreshPenghuniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_refreshPenghuniActionPerformed
        loadPenghuniDataToTable();
        updatePenghuniStatistics();

        JOptionPane.showMessageDialog(this, 
            "Data penghuni berhasil di-refresh!", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_refreshPenghuniActionPerformed

    private void fieldCariPenghuniActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fieldCariPenghuniActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_fieldCariPenghuniActionPerformed

    private void ubahPenghuniButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ubahPenghuniButtonActionPerformed
if (currentPenghuniId <= 0) {
        JOptionPane.showMessageDialog(this, "Tidak ada data penghuni yang dipilih!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Aktifkan mode edit
    setPenghuniFieldsEditable(true);
    setPenghuniButtonsState(false, true, true, false);
    
    JOptionPane.showMessageDialog(this, 
        "Mode Edit: Anda dapat mengubah data penghuni.\nKlik Simpan untuk menyimpan perubahan atau Batal untuk membatalkan.", 
        "Edit Mode", 
        JOptionPane.INFORMATION_MESSAGE);  
        
    }//GEN-LAST:event_ubahPenghuniButtonActionPerformed

    private void simpanPenghuniButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpanPenghuniButtonActionPerformed
 if (currentPenghuniId <= 0) {
        JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Validasi input
    if (namaField.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Nama Penghuni harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
        namaField.requestFocus();
        return;
    }

    if (kontakField.getText().trim().isEmpty()) {
        JOptionPane.showMessageDialog(this, "Kontak harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
        kontakField.requestFocus();
        return;
    }

    try {
        PenghuniDAO penghuniDAO = new PenghuniDAO();
        Penghuni penghuni = penghuniDAO.getById(currentPenghuniId);

        if (penghuni == null) {
            JOptionPane.showMessageDialog(this, "Data penghuni tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Konfirmasi update
        int confirm = JOptionPane.showConfirmDialog(this, 
            "Simpan perubahan data penghuni?\n\n" +
            "Nama: " + penghuni.getNamaPenghuni() + " → " + namaField.getText().trim() + "\n" +
            "Kontak: " + penghuni.getKontak() + " → " + kontakField.getText().trim(),
            "Konfirmasi Update",
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Update data penghuni
        penghuni.setNamaPenghuni(namaField.getText().trim());
        penghuni.setKontak(kontakField.getText().trim());
        penghuni.setEmail(emailField.getText().trim().isEmpty() ? null : emailField.getText().trim());
        penghuni.setAlamatAsal(alamatAsalField1.getText().trim().isEmpty() ? null : alamatAsalField1.getText().trim());

        boolean success = penghuniDAO.update(penghuni);

        if (success) {
            JOptionPane.showMessageDialog(this, "Data penghuni berhasil diupdate!", "Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Kembalikan ke state normal
            setPenghuniFieldsEditable(false);
            setPenghuniButtonsState(true, false, false, true);
            
            // Refresh data tabel
            loadPenghuniDataToTable();
        } else {
            JOptionPane.showMessageDialog(this, "Gagal update data penghuni!", "Error", JOptionPane.ERROR_MESSAGE);
        }

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }    
    }//GEN-LAST:event_simpanPenghuniButtonActionPerformed

    private void batalPenghuniButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batalPenghuniButtonActionPerformed
  if (currentPenghuniId <= 0) {
        JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    // Reload data asli dari database
    try {
        PenghuniDAO penghuniDAO = new PenghuniDAO();
        Penghuni penghuni = penghuniDAO.getById(currentPenghuniId);

        if (penghuni != null) {
            // Isi field dengan data asli dari database
            namaField.setText(penghuni.getNamaPenghuni());
            kontakField.setText(penghuni.getKontak() == null ? "-" : penghuni.getKontak());
            emailField.setText(penghuni.getEmail() == null ? "-" : penghuni.getEmail());
            alamatAsalField1.setText(penghuni.getAlamatAsal() == null ? "-" : penghuni.getAlamatAsal());
        }

        // Kembalikan ke state normal
        setPenghuniFieldsEditable(false);
        setPenghuniButtonsState(true, false, false, true);

        JOptionPane.showMessageDialog(this, "Perubahan dibatalkan", "Info", JOptionPane.INFORMATION_MESSAGE);

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    }//GEN-LAST:event_batalPenghuniButtonActionPerformed

    private void hapusPenghuniButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusPenghuniButtonActionPerformed
 if (currentPenghuniId <= 0) {
        JOptionPane.showMessageDialog(this, "Tidak ada data yang dipilih!", "Error", JOptionPane.ERROR_MESSAGE);
        return;
    }

    int confirm = JOptionPane.showConfirmDialog(this, 
        "Yakin hapus penghuni ID " + currentPenghuniId + "?\n" +
        "Nama: " + namaField.getText() + "\n" +
        "Data yang dihapus tidak dapat dikembalikan!", 
        "Konfirmasi Hapus", 
        JOptionPane.YES_NO_OPTION,
        JOptionPane.WARNING_MESSAGE);

    if (confirm == JOptionPane.YES_OPTION) {
        try {
            PenghuniDAO penghuniDAO = new PenghuniDAO();
            Penghuni penghuni = penghuniDAO.getById(currentPenghuniId);
            
            if (penghuni == null) {
                JOptionPane.showMessageDialog(this, "Data penghuni tidak ditemukan!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            
            int idKamar = penghuni.getIdKamar();
            
            // === SOLUSI: UPDATE KAMAR DULU, BARU HAPUS PENGHUNI ===
            
            // 1. Update kamar: set status menjadi "tersedia" dan id_penghuni menjadi NULL
            KamarDAO kamarDAO = new KamarDAO();
            Kamar kamar = kamarDAO.getById(idKamar);
            
            if (kamar != null) {
                // Update status dan hapus reference ke penghuni
                kamar.setStatus("tersedia");
                kamar.setIdPenghuni(null); // SET KE NULL dulu
                boolean kamarUpdated = kamarDAO.update(kamar);
                
                System.out.println("DEBUG: Update kamar result: " + kamarUpdated);
                
                if (!kamarUpdated) {
                    JOptionPane.showMessageDialog(this, 
                        "Gagal update data kamar! Penghuni tidak bisa dihapus.", 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    return;
                }
            }
            
            // 2. Sekarang hapus penghuni (sudah tidak ada foreign key constraint)
            boolean success = penghuniDAO.delete(currentPenghuniId);
            
            if (success) {
                JOptionPane.showMessageDialog(this, 
                    "Penghuni berhasil dihapus!\nStatus kamar diubah menjadi 'tersedia'", 
                    "Success", 
                    JOptionPane.INFORMATION_MESSAGE);

                // Reset form
                kosongkanLabelPenghuni();
                setPenghuniButtonsState(false, false, false, false);
                fieldCariPenghuni.setText("");
                
                // Refresh data
                loadPenghuniDataToTable();
                loadKamarDataToTable();
                updateDashboardStatistics();
                updatePenghuniStatistics();
                
            } else {
                JOptionPane.showMessageDialog(this, 
                    "Gagal menghapus data penghuni!", 
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    }//GEN-LAST:event_hapusPenghuniButtonActionPerformed

    private void jTextField6ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField6ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField6ActionPerformed

    private void cariPenghuniButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cariPenghuniButtonActionPerformed
    String keyword = fieldCariPenghuni.getText().trim();
    
    if (keyword.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Masukkan ID Penghuni!");
        kosongkanLabelPenghuni();
        setPenghuniButtonsState(false, false, false, false);
        return;
    }

    try {
        int keywordInt = Integer.parseInt(keyword);
        PenghuniDAO dao = new PenghuniDAO();
        Penghuni p = dao.getById(keywordInt);

        if (p == null) {
            JOptionPane.showMessageDialog(this, "Penghuni tidak ditemukan.");
            kosongkanLabelPenghuni();
            setPenghuniButtonsState(false, false, false, false);
            return;
        }
        
        KamarDAO kamarDao = new KamarDAO();
    Kamar kamar = kamarDao.getById(p.getIdKamar());
    if (kamar != null) {
        nomorKamarLabel.setText(String.valueOf(kamar.getNomorKamar()));

        TipeKamarDAO tipeDao = new TipeKamarDAO();
        TipeKamar tipe = tipeDao.getById(kamar.getIdTipeKamar());
        tipeKamarLabel.setText(tipe != null ? tipe.getTipeKamar() : "-");
    } else {
        nomorKamarLabel.setText("-");
        tipeKamarLabel.setText("-");
    }
        
        // Isi data ke field
        idPenghuniField.setText(String.valueOf(p.getIdPenghuni()));
        namaField.setText(p.getNamaPenghuni());
        idKamarPenghuniCariField.setText(String.valueOf(kamar.getIdKamar()));
        kontakField.setText(p.getKontak() == null ? "-" : p.getKontak());
        emailField.setText(p.getEmail() == null ? "-" : p.getEmail());
        alamatAsalField1.setText(p.getAlamatAsal() == null ? "-" : p.getAlamatAsal());

        java.text.SimpleDateFormat fmt = new java.text.SimpleDateFormat("yyyy-MM-dd");
        tanggalMasukField1.setText(p.getTanggalMasuk() == null ? "-" : fmt.format(p.getTanggalMasuk()));
        tanggalKeluarField.setText(p.getTanggalKeluar() == null ? "-" : fmt.format(p.getTanggalKeluar()));

        // Simpan ID penghuni yang sedang dicari
        currentPenghuniId = p.getIdPenghuni(); // <- INI
        
        // Set field tidak bisa diedit dan tombol aktif
        setPenghuniFieldsEditable(false);
        setPenghuniButtonsState(true, false, false, true);
        
        JOptionPane.showMessageDialog(this, "Data penghuni ditemukan!", "Success", JOptionPane.INFORMATION_MESSAGE);

    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "ID Penghuni harus berupa angka!");
        kosongkanLabelPenghuni();
        setPenghuniButtonsState(false, false, false, false);
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        ex.printStackTrace();
    }
    }//GEN-LAST:event_cariPenghuniButtonActionPerformed

    private void tambahPenghuniButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tambahPenghuniButtonActionPerformed
        if (!validateTambahPenghuniInput()) {
        return;
    }
    
    try {
        // Ambil data dari form
        int idKamar = Integer.parseInt(idKamarPenghuniField.getText().trim());
        String namaPenghuni = namaPenghuniField.getText().trim();
        String tanggalMasukStr = tanggalMasukField.getText().trim();
        String kontak = kontakPenghuniField.getText().trim();
        String email = emailPenghuniField.getText().trim();
        String alamatAsal = alamatAsalField1.getText().trim();
        
        // Parse tanggal masuk (format: yyyy-MM-dd)
        java.util.Date tanggalMasuk;
        java.util.Date tanggalKeluar;
        try {
            java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
            tanggalMasuk = sdf.parse(tanggalMasukStr);
            
            // Calculate tanggal keluar
            KamarDAO kamarDAO = new KamarDAO();
            Kamar kamar = kamarDAO.getById(idKamar);
            TipeKamarDAO tipeDAO = new TipeKamarDAO();
            TipeKamar tipe = tipeDAO.getById(kamar.getIdTipeKamar());
            
            String lamaSewa = tipe.getLamaSewa().toLowerCase();
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(tanggalMasuk);
            
            if (lamaSewa.contains("bulan")) {
                int bulan = Integer.parseInt(lamaSewa.replaceAll("[^0-9]", ""));
                cal.add(java.util.Calendar.MONTH, bulan);
            } else if (lamaSewa.contains("tahun")) {
                int tahun = Integer.parseInt(lamaSewa.replaceAll("[^0-9]", ""));
                cal.add(java.util.Calendar.YEAR, tahun);
            }
            
            tanggalKeluar = cal.getTime();
            
        } catch (java.text.ParseException ex) {
            JOptionPane.showMessageDialog(this, 
                "Format tanggal salah! Gunakan format: yyyy-MM-dd (contoh: 2025-01-15)", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        // Konfirmasi
        KamarDAO kamarDAO = new KamarDAO();
        Kamar kamar = kamarDAO.getById(idKamar);
        TipeKamarDAO tipeDAO = new TipeKamarDAO();
        TipeKamar tipe = tipeDAO.getById(kamar.getIdTipeKamar());
        
        java.text.SimpleDateFormat sdf = new java.text.SimpleDateFormat("yyyy-MM-dd");
        
        String confirmMessage = "Konfirmasi Tambah Penghuni:\n\n" +
            "Nama: " + namaPenghuni + "\n" +
            "Kamar: " + kamar.getNomorKamar() + " (Tipe " + tipe.getTipeKamar() + ")\n" +
            "Tanggal Masuk: " + sdf.format(tanggalMasuk) + "\n" +
            "Tanggal Keluar: " + sdf.format(tanggalKeluar) + " (" + tipe.getLamaSewa() + ")\n" +
            "Kontak: " + kontak + "\n\n" +
            "Apakah data sudah benar?";
        
        int confirm = JOptionPane.showConfirmDialog(this, 
            confirmMessage,
            "Konfirmasi Tambah Penghuni",
            JOptionPane.YES_NO_OPTION);
        
        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }
        
        // Buat object Penghuni baru
        Penghuni penghuniBaru = new Penghuni();
        penghuniBaru.setIdKamar(idKamar);
        penghuniBaru.setNamaPenghuni(namaPenghuni);
        penghuniBaru.setTanggalMasuk(tanggalMasuk);
        penghuniBaru.setTanggalKeluar(tanggalKeluar); // Set tanggal keluar otomatis
        penghuniBaru.setKontak(kontak);
        penghuniBaru.setEmail(email.isEmpty() ? null : email);
        penghuniBaru.setAlamatAsal(alamatAsal);
        
        // Simpan ke database
        PenghuniDAO penghuniDAO = new PenghuniDAO();
        boolean success = penghuniDAO.insert(penghuniBaru);
        
        if (success) {
    System.out.println("DEBUG: Penghuni berhasil ditambah, ID: " + penghuniBaru.getIdPenghuni());
    System.out.println("DEBUG: Mengupdate status kamar ID: " + idKamar + " menjadi 'terisi'");
    
    // Update status kamar DAN id_penghuni sekaligus
    boolean updateStatus = kamarDAO.updateStatusAndPenghuni(
        idKamar, 
        "terisi", 
        penghuniBaru.getIdPenghuni()
    );
    
    System.out.println("DEBUG: Hasil update status: " + updateStatus);
    
    if (updateStatus) {
        // Verifikasi perubahan di database
        Kamar kamarUpdated = kamarDAO.getById(idKamar);
        System.out.println("DEBUG: Status kamar setelah update: " + kamarUpdated.getStatus());
        System.out.println("DEBUG: ID Penghuni di kamar setelah update: " + kamarUpdated.getIdPenghuni());
        
        JOptionPane.showMessageDialog(this, 
            "Penghuni berhasil ditambahkan!\n" +
            "ID Penghuni: " + penghuniBaru.getIdPenghuni() + "\n" +
            "Nama: " + namaPenghuni + "\n" +
            "Kamar " + kamar.getNomorKamar() + " berhasil diisi", 
            "Success", 
            JOptionPane.INFORMATION_MESSAGE);
        
        // Reset form
        initTambahPenghuniForm();
        
        // Refresh data
        loadPenghuniDataToTable();
        loadKamarDataToTable();
        updateDashboardStatistics();
        updatePenghuniStatistics();
    } else {
        JOptionPane.showMessageDialog(this, 
            "Penghuni ditambahkan tapi gagal update status kamar!", 
            "Warning", 
            JOptionPane.WARNING_MESSAGE);
    }
} else {
            JOptionPane.showMessageDialog(this, 
                "Gagal menambah penghuni!", 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
        }
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "Error: " + ex.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    }//GEN-LAST:event_tambahPenghuniButtonActionPerformed

    private void batalTambahPenghuniButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batalTambahPenghuniButtonActionPerformed
 int confirm = JOptionPane.showConfirmDialog(this, 
        "Yakin ingin membatalkan? Data yang sudah diinput akan hilang.", 
        "Konfirmasi Batal", 
        JOptionPane.YES_NO_OPTION);
    
    if (confirm == JOptionPane.YES_OPTION) {
        initTambahPenghuniForm();
        JOptionPane.showMessageDialog(this, 
            "Form telah direset", 
            "Info", 
            JOptionPane.INFORMATION_MESSAGE);
    }
    }//GEN-LAST:event_batalTambahPenghuniButtonActionPerformed

    private void namaPenghuniFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_namaPenghuniFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_namaPenghuniFieldActionPerformed

    private void idPenghuniBaruLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_idPenghuniBaruLabelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_idPenghuniBaruLabelActionPerformed

    private void tanggalMasukFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tanggalMasukFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_tanggalMasukFieldActionPerformed

    private void kontakPenghuniFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_kontakPenghuniFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_kontakPenghuniFieldActionPerformed

    private void alamatAsalFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_alamatAsalFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_alamatAsalFieldActionPerformed

    private void nomorKamarLabelActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nomorKamarLabelActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_nomorKamarLabelActionPerformed

    private void ukuranTipeKamarFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ukuranTipeKamarFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_ukuranTipeKamarFieldActionPerformed

    private void batalTambahTipeKamarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_batalTambahTipeKamarButtonActionPerformed
 clearFieldTipeKamar();
    setFieldTipeKamarEditable(false);
    setTipeKamarButtonState("awal");
    
    JOptionPane.showMessageDialog(this,
        "Operasi dibatalkan",
        "Info",
        JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_batalTambahTipeKamarButtonActionPerformed

    private void hargaSewaFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hargaSewaFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_hargaSewaFieldActionPerformed

    private void simpanUbahTipeKamarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_simpanUbahTipeKamarButtonActionPerformed
     if (selectedTipeKamarId <= 0) {
        JOptionPane.showMessageDialog(this,
            "Tidak ada data yang dipilih!",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    if (!validateTipeKamarInput(true)) {
        return;
    }

    try {
        // Konfirmasi
        int confirm = JOptionPane.showConfirmDialog(this,
            "Simpan perubahan tipe kamar?\n\n" +
            "ID: " + selectedTipeKamarId + "\n" +
            "Tipe: " + tipeKamarField.getText(),
            "Konfirmasi Ubah",
            JOptionPane.YES_NO_OPTION);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Update object
        TipeKamar tk = tipeKamarDAO.getById(selectedTipeKamarId);
        tk.setTipeKamar(tipeKamarField.getText().trim());
        tk.setUkuran(ukuranTipeKamarField.getText().trim());
        tk.setHargaSewa(Double.parseDouble(hargaSewaField.getText().trim()));
        tk.setLamaSewa(lamaSewaField.getText().trim());
        tk.setFasilitasKamar(fasilitasTextArea.getText().trim());

        // Update ke database
        boolean success = tipeKamarDAO.update(tk);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Tipe kamar berhasil diupdate!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            // Refresh dan reset
            loadTipeKamarTable();
            clearFieldTipeKamar();
            setFieldTipeKamarEditable(false);
            setTipeKamarButtonState("awal");
            
            // Update combo box di tab kelola kamar
            loadTipeKamarToComboBox();
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal mengupdate tipe kamar!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Error: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    }//GEN-LAST:event_simpanUbahTipeKamarButtonActionPerformed

    private void ubahTipeKamarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_ubahTipeKamarButtonActionPerformed
     if (selectedTipeKamarId <= 0) {
        JOptionPane.showMessageDialog(this,
            "Pilih tipe kamar dari tabel terlebih dahulu!",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    setFieldTipeKamarEditable(true);
    setTipeKamarButtonState("ubah");
    tipeKamarField.requestFocus();
    
    JOptionPane.showMessageDialog(this,
        "Mode Ubah: Edit data dan klik Simpan",
        "Info",
        JOptionPane.INFORMATION_MESSAGE);
    }//GEN-LAST:event_ubahTipeKamarButtonActionPerformed

    private void hapusTipeKamarButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hapusTipeKamarButtonActionPerformed
      if (selectedTipeKamarId <= 0) {
        JOptionPane.showMessageDialog(this,
            "Pilih tipe kamar dari tabel terlebih dahulu!",
            "Error",
            JOptionPane.ERROR_MESSAGE);
        return;
    }

    try {
        // Cek apakah ada kamar dengan tipe ini
        KamarDAO kamarDAO = new KamarDAO();
        int jumlahKamar = kamarDAO.countByTipeKamar(selectedTipeKamarId);

        if (jumlahKamar > 0) {
            JOptionPane.showMessageDialog(this,
                "Tidak dapat menghapus tipe kamar ini!\n" +
                "Masih ada " + jumlahKamar + " kamar dengan tipe ini.\n\n" +
                "Hapus atau ubah tipe kamar tersebut terlebih dahulu.",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Konfirmasi hapus
        TipeKamar tk = tipeKamarDAO.getById(selectedTipeKamarId);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Yakin hapus tipe kamar ini?\n\n" +
            "ID: " + tk.getIdTipeKamar() + "\n" +
            "Tipe: " + tk.getTipeKamar() + "\n\n" +
            "Data yang dihapus tidak dapat dikembalikan!",
            "Konfirmasi Hapus",
            JOptionPane.YES_NO_OPTION,
            JOptionPane.WARNING_MESSAGE);

        if (confirm != JOptionPane.YES_OPTION) {
            return;
        }

        // Hapus dari database
        boolean success = tipeKamarDAO.delete(selectedTipeKamarId);

        if (success) {
            JOptionPane.showMessageDialog(this,
                "Tipe kamar berhasil dihapus!",
                "Success",
                JOptionPane.INFORMATION_MESSAGE);

            // Refresh dan reset
            loadTipeKamarTable();
            clearFieldTipeKamar();
            setFieldTipeKamarEditable(false);
            setTipeKamarButtonState("awal");
            
            // Update combo box di tab kelola kamar
            loadTipeKamarToComboBox();
        } else {
            JOptionPane.showMessageDialog(this,
                "Gagal menghapus tipe kamar!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Error: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
    }//GEN-LAST:event_hapusTipeKamarButtonActionPerformed

    private void lamaSewaFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_lamaSewaFieldActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_lamaSewaFieldActionPerformed

    private void btnRefreshDashboardActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnRefreshDashboardActionPerformed
        updateDashboardStatistics();
    }//GEN-LAST:event_btnRefreshDashboardActionPerformed

    private void loadKamarDataToTable() {
    try {
        KamarDAO kamarDAO = new KamarDAO();
        TipeKamarDAO tipeKamarDAO = new TipeKamarDAO();
        PenghuniDAO penghuniDAO = new PenghuniDAO();
        
        // === HITUNG STATISTIK ===
        java.util.List<model.Kamar> semuaKamar = kamarDAO.getAll();
        java.util.List<model.Kamar> kamarTerisi = kamarDAO.getByStatus("terisi");
        java.util.List<model.Kamar> kamarTersedia = kamarDAO.getByStatus("tersedia");
        java.util.List<model.Kamar> kamarMaintenance = kamarDAO.getByStatus("maintenance");
        
        // === UPDATE LABEL STATISTIK ===
        totalKamarLabel.setText(String.valueOf(semuaKamar.size()));
        kamarTerisiLabel.setText(String.valueOf(kamarTerisi.size()));
        kamarKosongLabel.setText(String.valueOf(kamarTersedia.size()));
        kamarMaintenanceLabel.setText(String.valueOf(kamarMaintenance.size()));
        
        // === UPDATE TABEL ===
        javax.swing.table.DefaultTableModel model = (javax.swing.table.DefaultTableModel) kamarTable.getModel();
        model.setRowCount(0); // Clear existing data
        
        for (model.Kamar kamar : semuaKamar) {
            // Get tipe kamar (huruf)
            model.TipeKamar tipe = tipeKamarDAO.getById(kamar.getIdTipeKamar());
            String tipeKamar = tipe.getTipeKamar();
            
            // Get nama penghuni (jika ada)
            int IDPenghuni = 0;
            if (kamar.getIdPenghuni() != null) {
                model.Penghuni penghuni = penghuniDAO.getById(kamar.getIdPenghuni());
                if (penghuni != null) {
                    IDPenghuni = penghuni.getIdPenghuni();
                }
            }
            
            // Add row to table
            model.addRow(new Object[]{
                kamar.getIdKamar(),
                tipeKamar,
                kamar.getNomorKamar(),
                IDPenghuni,
                kamar.getStatus()
            });
        }
        
        // Auto-adjust column widths
        packTableColumns();
        
    } catch (Exception ex) {
        javax.swing.JOptionPane.showMessageDialog(this, 
            "Error loading data: " + ex.getMessage(), 
            "Error", 
            javax.swing.JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}
    
    private void updateDashboardStatistics() {
        try {
            KamarDAO kamarDAO = new KamarDAO();
            PenghuniDAO penghuniDAO = new PenghuniDAO();
            TipeKamarDAO tipeKamarDAO = new TipeKamarDAO();

            // Ambil data dari database
            int totalKamar = kamarDAO.getAll().size();
            int totalPenghuniAktif = penghuniDAO.countTotalPenghuniAktif();
            int totalTipeKamar = tipeKamarDAO.getAll().size();


            // Hitung statistik dari database
            

            // Update label dengan data real
            totalKamarField.setText(String.valueOf(totalKamar));
            totalPenghuniField.setText(String.valueOf(totalPenghuniAktif));
            totalTipeKamarField.setText(String.valueOf(totalTipeKamar));
            adminUsernameLabel.setText(username); // Username admin yang login

        } catch (Exception ex) {
            javax.swing.JOptionPane.showMessageDialog(this, 
                "Error loading dashboard data: " + ex.getMessage(), 
                "Error", 
                javax.swing.JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    private void packTableColumns() {
    for (int column = 0; column < kamarTable.getColumnCount(); column++) {
        javax.swing.table.TableColumn tableColumn = kamarTable.getColumnModel().getColumn(column);
        int preferredWidth = tableColumn.getMinWidth();
        int maxWidth = tableColumn.getMaxWidth();
        
        for (int row = 0; row < kamarTable.getRowCount(); row++) {
            javax.swing.table.TableCellRenderer cellRenderer = kamarTable.getCellRenderer(row, column);
            java.awt.Component c = kamarTable.prepareRenderer(cellRenderer, row, column);
            int width = c.getPreferredSize().width + kamarTable.getIntercellSpacing().width;
            preferredWidth = Math.max(preferredWidth, width);
            
            if (preferredWidth >= maxWidth) {
                preferredWidth = maxWidth;
                break;
            }
        }
        
        tableColumn.setPreferredWidth(preferredWidth);
    }
}
    
    private int currentKamarId = 0;
    
    private void resetTableToReadOnly() {
        // Kembalikan cell editor default (read-only)
        for (int i = 0; i < tabelHasilPencarian.getColumnCount(); i++) {
            tabelHasilPencarian.getColumnModel().getColumn(i).setCellEditor(null);
        }
    }

    private void resetSearchForm() {
        cariIDKamar.setText("");
        DefaultTableModel model = (DefaultTableModel) tabelHasilPencarian.getModel();
        model.setRowCount(0);
        currentKamarId = 0;
    }
    
    private void setEditDeleteButtonsEnabled(boolean enabled) {
        ubahKamarButton.setEnabled(enabled);
        hapusKamarButton.setEnabled(enabled);
    }

    private void setSaveCancelButtonsEnabled(boolean enabled) {
        simpanPerubahanKamar.setEnabled(enabled);
        batalPerubahan.setEnabled(enabled);
    }

    private boolean validateTambahKamarInput() {
        String nomorKamarText = nomorKamarBaruLabel.getText().trim();
    
        // Validasi Tipe Kamar (sekarang dari combo box)
        int idTipeKamar = getSelectedTipeKamarId();
        if (idTipeKamar == -1) {
            JOptionPane.showMessageDialog(this, "Pilih Tipe Kamar yang valid!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        // Validasi Nomor Kamar
        if (nomorKamarText.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Nomor Kamar harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        try {
            int nomorKamar = Integer.parseInt(nomorKamarText);
            if (nomorKamar < 100 || nomorKamar > 999) {
                JOptionPane.showMessageDialog(this, "Nomor Kamar harus antara 100-999!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }

            // Cek apakah nomor kamar sudah ada
            KamarDAO kamarDAO = new KamarDAO();
            Kamar existingKamar = kamarDAO.getByNomorKamar(nomorKamar);
            if (existingKamar != null) {
                JOptionPane.showMessageDialog(this, "Nomor Kamar " + nomorKamar + " sudah ada!", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Nomor Kamar harus angka!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }

        return true;
    }
    
    private void resetAllButtons() {
        setEditDeleteButtonsEnabled(false);
        setSaveCancelButtonsEnabled(false);
    }
    
//    private void onTambahKamarTabSelected() {
//        updateIdKamarBaruLabel();
//    }
            
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ReflectiveOperationException | javax.swing.UnsupportedLookAndFeelException ex) {
            logger.log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(() -> new Admin().setVisible(true));
    }
    
    private void loadTipeKamarToComboBox() {
    try {
        TipeKamarDAO tipeDAO = new TipeKamarDAO();
        List<TipeKamar> tipeKamars = tipeDAO.getAll();
        
        // Clear existing items
        IDTipeKamarBaruComboBox.removeAllItems();
        
        // Add items to combo box
        for (TipeKamar tipe : tipeKamars) {
            IDTipeKamarBaruComboBox.addItem("Tipe " + tipe.getTipeKamar());
        }
        
    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this, 
            "Error loading tipe kamar: " + ex.getMessage(), 
            "Error", 
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

    // Method untuk mendapatkan ID tipe kamar dari pilihan combo box
    private int getSelectedTipeKamarId() {
        String selectedTipe = (String) IDTipeKamarBaruComboBox.getSelectedItem();
        if (selectedTipe != null) {
            // Extract huruf tipe dari "Tipe A" -> "A"
            String tipeHuruf = selectedTipe.replace("Tipe ", "").trim();

            TipeKamarDAO tipeDAO = new TipeKamarDAO();
            TipeKamar tipe = tipeDAO.getByTipe(tipeHuruf);

            if (tipe != null) {
                return tipe.getIdTipeKamar();
            }
        }
        return -1; // Return -1 jika tidak ditemukan
    }

    // Method untuk update ID real-time ketika input berubah
//    private void setupRealTimeIdUpdate() {
//        javax.swing.event.DocumentListener docListener = new javax.swing.event.DocumentListener() {
//            public void insertUpdate(javax.swing.event.DocumentEvent e) { updateIdKamarBaruLabel(); }
//            public void removeUpdate(javax.swing.event.DocumentEvent e) { updateIdKamarBaruLabel(); }
//            public void changedUpdate(javax.swing.event.DocumentEvent e) { updateIdKamarBaruLabel(); }
//        };
//
//        nomorKamarBaruLabel.getDocument().addDocumentListener(docListener);
//
//        // Juga update ketika combo box tipe kamar berubah
//        IDTipeKamarBaruComboBox.addActionListener(new java.awt.event.ActionListener() {
//            public void actionPerformed(java.awt.event.ActionEvent evt) {
//                updateIdKamarBaruLabel();
//            }
//        });
//    }

    // Method untuk generate dan tampilkan ID kamar berikutnya
    private void updateIdKamarBaruLabel() {
        try {
            KamarDAO kamarDAO = new KamarDAO();
            List<Kamar> semuaKamar = kamarDAO.getAll();

            System.out.println("Jumlah kamar di database: " + semuaKamar.size()); // Debug

            // Cari ID tertinggi untuk predict next ID
            int maxId = 0;
            for (Kamar kamar : semuaKamar) {
                System.out.println("Kamar ID: " + kamar.getIdKamar()); // Debug
                if (kamar.getIdKamar() > maxId) {
                    maxId = kamar.getIdKamar();
                }
            }

            int nextId = maxId + 1;
            System.out.println("Next predicted ID: " + nextId); // Debug

            idKamarBaruLabel.setText("Auto generate : " + nextId);

        } catch (Exception ex) {
            System.err.println("Error in updateIdKamarBaruLabel: " + ex.getMessage()); // Debug
            ex.printStackTrace();
            idKamarBaruLabel.setText("Auto generate : Error");
        }
    }

    // Method untuk load data penghuni ke tabel
    private void loadPenghuniDataToTable() {
        try {
            PenghuniDAO penghuniDAO = new PenghuniDAO();
            DefaultTableModel model = (DefaultTableModel) penghuniTable.getModel();
            model.setRowCount(0); // Clear existing data

            // Get all penghuni from database
            java.util.List<model.Penghuni> semuaPenghuni = penghuniDAO.getAll();

            for (model.Penghuni penghuni : semuaPenghuni) {

                java.util.Date tanggalMasuk = penghuni.getTanggalMasuk();
                java.util.Date tanggalKeluar = penghuni.getTanggalKeluar();

                // Handle nullable fields
                String email = penghuni.getEmail() != null ? penghuni.getEmail() : "-";
                String alamatAsal = penghuni.getAlamatAsal() != null ? penghuni.getAlamatAsal() : "-";

                // Add row to table
                model.addRow(new Object[]{
                    penghuni.getIdPenghuni(),
                    penghuni.getIdKamar(),
                    penghuni.getNamaPenghuni(),
                    penghuni.getKontak(),
                    email,
                    alamatAsal,
                    tanggalMasuk,
                    tanggalKeluar
                });
            }

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading data penghuni: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    // Method untuk update statistik penghuni
    private void updatePenghuniStatistics() {
        try {
            PenghuniDAO penghuniDAO = new PenghuniDAO();
            KamarDAO kamarDAO = new KamarDAO();

            // Hitung statistik dari database
            int totalPenghuniAktif = penghuniDAO.countTotalPenghuniAktif();

            // Update label statistik
            totalPenghuniLabel.setText(String.valueOf(totalPenghuniAktif));

        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error loading penghuni statistics: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }
    
    // ===== TAMBAHKAN METHOD-METHOD INI KE DALAM CLASS Admin =====

// 1. Method untuk menampilkan info kamar berdasarkan ID


// ===== MODIFIKASI METHOD CONSTRUCTOR =====
// Tambahkan di dalam constructor Admin(AdminLogin parentForm, String username):
// initTambahPenghuniForm();
// setupRealTimePenghuniUpdate();

    
    
    
//====================================== CARI PENGHUNI========================================
    private int currentPenghuniId = 0;
private void kosongkanLabelPenghuni() {
    idPenghuniField.setText("-");
    namaField.setText("-");
    kontakField.setText("-");
    emailField.setText("-");
    alamatAsalField1.setText("-");
    idKamarPenghuniCariField.setText("-");
    tanggalMasukField1.setText("-");
    tanggalKeluarField.setText("-");
    nomorKamarLabel.setText("-");
    tipeKamarLabel.setText("-");
    currentPenghuniId = 0;
}
//
//private Penghuni penghuniFound;  // hasil pencarian terakhir
//
//// variabel penampung perubahan (dipakai saat mode Ubah)
//private String tempNama;
//private String tempKontak;
//private String tempEmail;
//private String tempAlamat;

private void setPenghuniButtonsState(boolean ubahEnabled, boolean simpanEnabled, boolean batalEnabled, boolean hapusEnabled) {
    ubahPenghuniButton.setEnabled(ubahEnabled);
    simpanPenghuniButton.setEnabled(simpanEnabled);
    batalPenghuniButton.setEnabled(batalEnabled);
    hapusPenghuniButton.setEnabled(hapusEnabled);
}

private void setPenghuniFieldsEditable(boolean editable) {
    namaField.setEditable(editable);
    kontakField.setEditable(editable);
    emailField.setEditable(editable);
    alamatAsalField1.setEditable(editable);
    // Field tanggal dan info kamar tetap tidak bisa diedit
}


//======================================= TIPE KAMAR ======================================


// ========================================
// BAGIAN TIPE KAMAR - TAMBAHKAN DI CLASS Admin
// ========================================

// Deklarasi variabel instance (letakkan di bagian atas class, setelah deklarasi DAO lainnya)
private TipeKamarDAO tipeKamarDAO = new TipeKamarDAO();
private int selectedTipeKamarId = -1;

// Method inisialisasi tab tipe kamar
private void initTabTipeKamar() {
    loadTipeKamarTable();
    clearFieldTipeKamar();
    setTipeKamarButtonState("awal");
    setFieldTipeKamarEditable(false);
}

// Method untuk setup listener klik tabel
private void setupTipeKamarTableListener() {
    tipeKamarTable.addMouseListener(new java.awt.event.MouseAdapter() {
        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            tipeKamarTableRowClicked();
        }
    });
}

// Method load data ke tabel dengan statistik
private void loadTipeKamarTable() {
    DefaultTableModel model = (DefaultTableModel) tipeKamarTable.getModel();
    model.setRowCount(0);

    try {
        List<TipeKamar> listTipeKamar = tipeKamarDAO.getAll();
        KamarDAO kamarDAO = new KamarDAO();

        for (TipeKamar tk : listTipeKamar) {
            // Hitung jumlah kamar per tipe
            int jumlahKamar = kamarDAO.countByTipeKamar(tk.getIdTipeKamar());

            Object[] row = new Object[]{
                tk.getIdTipeKamar(),
                tk.getTipeKamar(),
                tk.getUkuran(),
                String.format("Rp %,.0f", tk.getHargaSewa()),
                tk.getLamaSewa(),
                tk.getFasilitasKamar()
            };
            model.addRow(row);
        }

        // Update dashboard juga
        updateDashboardStatistics();

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Error loading tipe kamar: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

// Method ketika baris tabel diklik
private void tipeKamarTableRowClicked() {
    int row = tipeKamarTable.getSelectedRow();
    if (row < 0) return;

    try {
        // Ambil ID dari kolom pertama
        selectedTipeKamarId = Integer.parseInt(tipeKamarTable.getValueAt(row, 0).toString());
        
        // Load data lengkap dari database
        TipeKamar tk = tipeKamarDAO.getById(selectedTipeKamarId);
        
        if (tk != null) {
            // Isi field dengan data
            idTipeKamarField.setText(String.valueOf(tk.getIdTipeKamar()));
            tipeKamarField.setText(tk.getTipeKamar());
            ukuranTipeKamarField.setText(tk.getUkuran());
            hargaSewaField.setText(String.valueOf(tk.getHargaSewa()));
            lamaSewaField.setText(tk.getLamaSewa());
            fasilitasTextArea.setText(tk.getFasilitasKamar());

            // Ubah status tombol: Ubah dan Hapus aktif
            setTipeKamarButtonState("rowSelected");
            setFieldTipeKamarEditable(false);
        }

    } catch (Exception ex) {
        JOptionPane.showMessageDialog(this,
            "Error: " + ex.getMessage(),
            "Error",
            JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}

// Method clear semua field
private void clearFieldTipeKamar() {
    idTipeKamarField.setText("");
    tipeKamarField.setText("");
    ukuranTipeKamarField.setText("");
    hargaSewaField.setText("");
    lamaSewaField.setText("");
    fasilitasTextArea.setText("");
    selectedTipeKamarId = -1;
}

// Method set editable field
private void setFieldTipeKamarEditable(boolean editable, boolean isAddMode) {
    idTipeKamarField.setEditable(isAddMode); // ID bisa diedit saat tambah
//    tipeKamarField.setEditable(editable);
    ukuranTipeKamarField.setEditable(editable);
    hargaSewaField.setEditable(editable);
    lamaSewaField.setEditable(editable);
    fasilitasTextArea.setEditable(editable);
}

// Method set status tombol
private void setTipeKamarButtonState(String state) {
    switch(state) {
        case "awal":
            ubahTipeKamarButton.setEnabled(false);
            simpanUbahTipeKamarButton.setEnabled(false);
            batalTambahTipeKamarButton.setEnabled(false);
            hapusTipeKamarButton.setEnabled(false);
            break;

        case "rowSelected":
            ubahTipeKamarButton.setEnabled(true);
            simpanUbahTipeKamarButton.setEnabled(false);
            batalTambahTipeKamarButton.setEnabled(true);
            hapusTipeKamarButton.setEnabled(true);
            break;

//        case "tambah":
//            ubahTipeKamarButton.setEnabled(false);
//            simpanUbahTipeKamarButton.setEnabled(false);
//            batalTambahTipeKamarButton.setEnabled(true);
//            hapusTipeKamarButton.setEnabled(false);
//            break;

        case "ubah":
            ubahTipeKamarButton.setEnabled(false);
            simpanUbahTipeKamarButton.setEnabled(true);
            batalTambahTipeKamarButton.setEnabled(true);
            hapusTipeKamarButton.setEnabled(false);
            break;
    }
}

// Validasi input tipe kamar
private boolean validateTipeKamarInput(boolean isUpdate) {
    String tipe = tipeKamarField.getText().trim();
    String ukuran = ukuranTipeKamarField.getText().trim();
    String hargaStr = hargaSewaField.getText().trim();
    String lamaSewa = lamaSewaField.getText().trim();
    String fasilitas = fasilitasTextArea.getText().trim();

    // Validasi field kosong
    if (tipe.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Tipe Kamar harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
        tipeKamarField.requestFocus();
        return false;
    }

    if (ukuran.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Ukuran harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
        ukuranTipeKamarField.requestFocus();
        return false;
    }

    if (hargaStr.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Harga Sewa harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
        hargaSewaField.requestFocus();
        return false;
    }

    if (lamaSewa.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Lama Sewa harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
        lamaSewaField.requestFocus();
        return false;
    }

    if (fasilitas.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Fasilitas harus diisi!", "Error", JOptionPane.ERROR_MESSAGE);
        fasilitasTextArea.requestFocus();
        return false;
    }

    // Validasi format harga
    try {
        double harga = Double.parseDouble(hargaStr);
        if (harga <= 0) {
            JOptionPane.showMessageDialog(this, "Harga Sewa harus lebih dari 0!", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    } catch (NumberFormatException ex) {
        JOptionPane.showMessageDialog(this, "Harga Sewa harus berupa angka!", "Error", JOptionPane.ERROR_MESSAGE);
        hargaSewaField.requestFocus();
        return false;
    }

    // Cek duplikasi tipe (hanya saat tambah atau ubah ke tipe berbeda)
    if (!isUpdate || !tipe.equals(tipeKamarDAO.getById(selectedTipeKamarId).getTipeKamar())) {
        if (tipeKamarDAO.existsByTipe(tipe)) {
            JOptionPane.showMessageDialog(this,
                "Tipe Kamar '" + tipe + "' sudah ada!",
                "Error",
                JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    return true;
}

// Method set editable field - versi overload (tambahkan di class Admin)
private void setFieldTipeKamarEditable(boolean editable) {
    setFieldTipeKamarEditable(editable, false); // default: bukan mode tambah
}




    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<String> IDTipeKamarBaruComboBox;
    private javax.swing.JLabel adminUsernameLabel;
    private javax.swing.JTextField alamatAsalField;
    private javax.swing.JTextField alamatAsalField1;
    private javax.swing.JButton batalPenghuniButton;
    private javax.swing.JButton batalPerubahan;
    private javax.swing.JButton batalTambahKamarButton;
    private javax.swing.JButton batalTambahPenghuniButton;
    private javax.swing.JButton batalTambahTipeKamarButton;
    private javax.swing.JButton btnRefreshDashboard;
    private javax.swing.JTextField cariIDKamar;
    private javax.swing.JButton cariKamarButton;
    private javax.swing.JButton cariPenghuniButton;
    private javax.swing.JTextField emailField;
    private javax.swing.JTextField emailPenghuniField;
    private javax.swing.JTextArea fasilitasTextArea;
    private javax.swing.JTextField fieldCariPenghuni;
    private javax.swing.JButton hapusKamarButton;
    private javax.swing.JButton hapusPenghuniButton;
    private javax.swing.JButton hapusTipeKamarButton;
    private javax.swing.JTextField hargaSewaField;
    private javax.swing.JLabel hargaSewaInfoLabel;
    private javax.swing.JLabel hargaSewaInfoLabel1;
    private javax.swing.JPanel hasilCariIDPenghuniLabel;
    private javax.swing.JPanel hasilCariIDPenghuniLabel1;
    private javax.swing.JTextField idKamarBaruLabel;
    private javax.swing.JTextField idKamarPenghuniCariField;
    private javax.swing.JTextField idKamarPenghuniField;
    private javax.swing.JTextField idPenghuniBaruLabel;
    private javax.swing.JTextField idPenghuniField;
    private javax.swing.JLabel idPenghuniLabel;
    private javax.swing.JLabel idPenghuniLabel1;
    private javax.swing.JTextField idTipeKamarField;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel37;
    private javax.swing.JLabel jLabel38;
    private javax.swing.JLabel jLabel39;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel40;
    private javax.swing.JLabel jLabel41;
    private javax.swing.JLabel jLabel42;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel54;
    private javax.swing.JLabel jLabel55;
    private javax.swing.JLabel jLabel56;
    private javax.swing.JLabel jLabel57;
    private javax.swing.JLabel jLabel58;
    private javax.swing.JLabel jLabel59;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel61;
    private javax.swing.JLabel jLabel62;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel94;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel38;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel45;
    private javax.swing.JPanel jPanel46;
    private javax.swing.JPanel jPanel47;
    private javax.swing.JPanel jPanel48;
    private javax.swing.JPanel jPanel49;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel51;
    private javax.swing.JPanel jPanel52;
    private javax.swing.JPanel jPanel53;
    private javax.swing.JPanel jPanel56;
    private javax.swing.JPanel jPanel57;
    private javax.swing.JPanel jPanel58;
    private javax.swing.JPanel jPanel59;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel61;
    private javax.swing.JPanel jPanel62;
    private javax.swing.JPanel jPanel63;
    private javax.swing.JPanel jPanel64;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextField5;
    private javax.swing.JTextField jTextField6;
    private javax.swing.JLabel kamarKosongLabel;
    private javax.swing.JLabel kamarMaintenanceLabel;
    private javax.swing.JTable kamarTable;
    private javax.swing.JLabel kamarTerisiLabel;
    private javax.swing.JTextField kontakField;
    private javax.swing.JTextField kontakPenghuniField;
    private javax.swing.JTextField lamaSewaField;
    private javax.swing.JLabel lamaSewaInfoLabel;
    private javax.swing.JLabel lamaSewaInfoLabel1;
    private javax.swing.JButton logoutButton;
    private javax.swing.JTextField namaField;
    private javax.swing.JTextField namaPenghuniField;
    private javax.swing.JTextField nomorKamarBaruLabel;
    private javax.swing.JLabel nomorKamarInfoLabel;
    private javax.swing.JLabel nomorKamarInfoLabel1;
    private javax.swing.JTextField nomorKamarLabel;
    private javax.swing.JTable penghuniTable;
    private javax.swing.JButton refresh;
    private javax.swing.JButton refreshPenghuni;
    private javax.swing.JButton simpanPenghuniButton;
    private javax.swing.JButton simpanPerubahanKamar;
    private javax.swing.JButton simpanUbahTipeKamarButton;
    private javax.swing.JPanel statistikKamar;
    private javax.swing.JPanel statistikKamar1;
    private javax.swing.JComboBox<String> statusKamarBaruComboBox;
    private javax.swing.JTable tabelHasilPencarian;
    private javax.swing.JButton tambahKamarButton;
    private javax.swing.JButton tambahPenghuniButton;
    private javax.swing.JTextField tanggalKeluarField;
    private javax.swing.JLabel tanggalKeluarInfoLabel;
    private javax.swing.JLabel tanggalKeluarInfoLabel1;
    private javax.swing.JTextField tanggalMasukField;
    private javax.swing.JTextField tanggalMasukField1;
    private javax.swing.JTextField tipeKamarField;
    private javax.swing.JLabel tipeKamarInfoLabel;
    private javax.swing.JLabel tipeKamarInfoLabel1;
    private javax.swing.JTextField tipeKamarLabel;
    private javax.swing.JTable tipeKamarTable;
    private javax.swing.JLabel totalKamarField;
    private javax.swing.JLabel totalKamarLabel;
    private javax.swing.JLabel totalPenghuniField;
    private javax.swing.JLabel totalPenghuniLabel;
    private javax.swing.JLabel totalTipeKamarField;
    private javax.swing.JButton ubahKamarButton;
    private javax.swing.JButton ubahPenghuniButton;
    private javax.swing.JButton ubahTipeKamarButton;
    private javax.swing.JTextField ukuranTipeKamarField;
    // End of variables declaration//GEN-END:variables
}
