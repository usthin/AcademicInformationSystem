import ui.Forms;
import db.DB;

public class Main {
    public static void main(String[] args) {
        javax.swing.SwingUtilities.invokeLater(() -> {
            try {
                DB.init();
            } catch (Exception e) {
                javax.swing.JOptionPane.showMessageDialog(null, "DB init error: " + e.getMessage());
                e.printStackTrace();
                System.exit(1);
            }
            Forms.showLoginForm();
        });
    }
}
