package com.newpix.client.gui;

import javax.swing.*;

/**
 * Adaptador que delega para a implementação atual em com.distribuidos.client.ClientGUI
 */
public class ClientGUI extends JFrame {
    private final com.distribuidos.client.ClientGUI delegate;

    public ClientGUI() {
        delegate = new com.distribuidos.client.ClientGUI();
        // Expose the delegate frame
        this.setContentPane(delegate.getContentPane());
        this.setTitle(delegate.getTitle());
        this.setSize(delegate.getSize());
        this.setLocationRelativeTo(null);
    }
}
