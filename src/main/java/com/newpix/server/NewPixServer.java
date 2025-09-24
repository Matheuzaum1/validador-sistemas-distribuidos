package com.newpix.server;

import javax.swing.SwingUtilities;

public class NewPixServer {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            com.newpix.server.gui.ServerGUI gui = new com.newpix.server.gui.ServerGUI();
            gui.setVisible(true);
        });
    }
}
