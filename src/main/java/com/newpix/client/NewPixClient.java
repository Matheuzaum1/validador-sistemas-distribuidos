package com.newpix.client;

import javax.swing.SwingUtilities;

public class NewPixClient {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            com.newpix.client.gui.ClientGUI gui = new com.newpix.client.gui.ClientGUI();
            gui.setVisible(true);
        });
    }
}
