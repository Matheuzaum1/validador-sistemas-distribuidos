package com.newpix.server.gui;

import javax.swing.*;

/**
 * Adaptador para a GUI do servidor que delega para a implementação em com.distribuidos.server.ServerGUI
 */
public class ServerGUI extends JFrame {
    private final com.distribuidos.server.ServerGUI delegate;

    public ServerGUI() {
        delegate = new com.distribuidos.server.ServerGUI();
        this.setContentPane(delegate.getContentPane());
        this.setTitle(delegate.getTitle());
        this.setSize(delegate.getSize());
        this.setLocationRelativeTo(null);
    }
}
