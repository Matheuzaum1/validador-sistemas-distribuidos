package com.distribuidos.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

public class ServerMain {
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    
    public static void main(String[] args) {
        // Configurar encoding para UTF-8
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("java.awt.fonts", "true");
        
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
                // If a port is provided as first arg, auto-start server on that port (headless-friendly)
                if (args != null && args.length > 0) {
                    try {
                        int port = Integer.parseInt(args[0]);
                        serverGUI.setVisible(false);
                        // set port field and invoke startServer reflectively
                        try {
                            java.lang.reflect.Field portField = ServerGUI.class.getDeclaredField("portField");
                            portField.setAccessible(true);
                            javax.swing.JTextField pf = (javax.swing.JTextField) portField.get(serverGUI);
                            pf.setText(String.valueOf(port));
                        } catch (Exception ignored) {}

                        try {
                            java.lang.reflect.Method startMethod = ServerGUI.class.getDeclaredMethod("startServer");
                            startMethod.setAccessible(true);
                            startMethod.invoke(serverGUI);
                        } catch (Exception e) {
                            logger.error("Erro ao auto-iniciar servidor", e);
                            serverGUI.setVisible(true);
                        }
                    } catch (NumberFormatException ignored) {
                        serverGUI.setVisible(true);
                    }
                } else {
                    serverGUI.setVisible(true);
                }

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