package coscostan.gui;
import coscostan.gui.AdminLogin;
import coscostan.gui.UserDashboard1;

public class LoginForm extends javax.swing.JFrame {
    
    private static final java.util.logging.Logger logger = java.util.logging.Logger.getLogger(LoginForm.class.getName());

    public LoginForm() {
        initComponents();
    }

    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jLabel1 = new javax.swing.JLabel();
        jPanel1 = new javax.swing.JPanel();
        adminButton = new javax.swing.JButton();
        userButton = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jTextPane1 = new javax.swing.JTextPane();
        jLabel3 = new javax.swing.JLabel();

        jLabel1.setBackground(new java.awt.Color(102, 102, 0));
        jLabel1.setText("jLabel1");

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jPanel1.setBackground(new java.awt.Color(0, 0, 0));

        adminButton.setBackground(new java.awt.Color(75, 75, 48));
        adminButton.setFont(new java.awt.Font("Mona Sans ExtraLight", 0, 12)); // NOI18N
        adminButton.setForeground(new java.awt.Color(255, 255, 255));
        adminButton.setText("Admin");
        adminButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                adminButtonActionPerformed(evt);
            }
        });

        userButton.setBackground(new java.awt.Color(75, 75, 48));
        userButton.setFont(new java.awt.Font("Mona Sans ExtraLight", 0, 12)); // NOI18N
        userButton.setForeground(new java.awt.Color(255, 255, 255));
        userButton.setText("Visitor");
        userButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                userButtonActionPerformed(evt);
            }
        });

        jLabel2.setFont(new java.awt.Font("Montserrat ExtraBold", 1, 28)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(255, 255, 255));
        jLabel2.setText("Let's Get Started");

        jTextPane1.setBackground(new java.awt.Color(0, 0, 0));
        jTextPane1.setFont(new java.awt.Font("Mona Sans ExtraLight", 1, 14)); // NOI18N
        jTextPane1.setForeground(new java.awt.Color(255, 255, 255));
        jTextPane1.setText("Find Your Perfect Room - Browse Available Kost");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(53, 53, 53)
                        .addComponent(adminButton)
                        .addGap(32, 32, 32)
                        .addComponent(userButton, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jTextPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 256, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2))
                .addContainerGap(114, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(241, 241, 241)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 50, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(30, 30, 30)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(adminButton)
                    .addComponent(userButton))
                .addContainerGap(265, Short.MAX_VALUE))
        );

        jLabel3.setIcon(new javax.swing.ImageIcon("C:\\Users\\Raissa\\Downloads\\4444.jpg")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 474, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void adminButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_adminButtonActionPerformed
        this.setVisible(false);
        AdminLogin adminLogin = new AdminLogin(this);
        adminLogin.setVisible(true);
    }//GEN-LAST:event_adminButtonActionPerformed

    private void userButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_userButtonActionPerformed
        this.setVisible(false);
        UserDashboard1 userDashboard = new UserDashboard1();
        userDashboard.setVisible(true);
    }//GEN-LAST:event_userButtonActionPerformed


    public static void main(String args[]) {

        java.awt.EventQueue.invokeLater(() -> new LoginForm().setVisible(true));
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton adminButton;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JTextPane jTextPane1;
    private javax.swing.JButton userButton;
    // End of variables declaration//GEN-END:variables
}
