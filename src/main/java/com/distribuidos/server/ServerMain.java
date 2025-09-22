package com.distribuidos.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class ServerMain {
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    
    public static void main(String[] args) {
        // Configura Look and Feel
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            logger.warn("Não foi possível definir o Look and Feel", e);
        }
        
        // Inicia a GUI do servidor
        SwingUtilities.invokeLater(() -> {
            try {
                ServerGUI serverGUI = new ServerGUI();
                serverGUI.setVisible(true);
                logger.info("Servidor iniciado com sucesso");
            } catch (Exception e) {
                logger.error("Erro ao iniciar servidor", e);
                JOptionPane.showMessageDialog(null, 
                    "Erro ao iniciar servidor: " + e.getMessage(),
                    "Erro Fatal", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}