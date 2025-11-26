package coscostan.main;

import coscostan.gui.PickRole;
import javax.swing.SwingUtilities;

public class Main {
    public static void main(String[] args) {
        // Jalankan aplikasi di Event Dispatch Thread
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new PickRole().setVisible(true);
            }
        });
    }
}