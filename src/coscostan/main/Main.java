package coscostan.main;

import coscostan.gui.LoginForm;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Jalankan aplikasi di Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new LoginForm().setVisible(true);
            }
        });
    }
}