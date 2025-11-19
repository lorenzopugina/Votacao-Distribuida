package server;

import javax.swing.SwingUtilities;
import server.ui.ServerGUI;

public class ServerMain {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ServerGUI gui = new ServerGUI();
            gui.setVisible(true);
        });
    }
}
