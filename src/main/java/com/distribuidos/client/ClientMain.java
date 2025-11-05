package com.distribuidos.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.formdev.flatlaf.FlatDarkLaf;

import javax.swing.*;

public class ClientMain {
    private static final Logger logger = LoggerFactory.getLogger(ClientMain.class);
    
    public static void main(String[] args) {
        // Configurar encoding para UTF-8
        System.setProperty("file.encoding", "UTF-8");
        System.setProperty("java.awt.fonts", "true");
        
        // Configura Look and Feel - FlatLaf (tema moderno)
        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
            logger.info("Tema FlatDarkLaf aplicado com sucesso");
        } catch (Exception e) {
            logger.warn("Não foi possível aplicar tema FlatLaf, usando Look and Feel padrão", e);
            try {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
                }
            } catch (Exception e2) {
                logger.warn("Não foi possível definir nenhum Look and Feel customizado", e2);
            }
        }
        
        // Inicia a GUI do cliente
        SwingUtilities.invokeLater(() -> {
            try {
                ClientGUI clientGUI = new ClientGUI();
                clientGUI.setVisible(true);
                logger.info("Cliente iniciado com sucesso");
            } catch (Exception e) {
                logger.error("Erro ao iniciar cliente", e);
                JOptionPane.showMessageDialog(null, 
                    "Erro ao iniciar cliente: " + e.getMessage(),
                    "Erro Fatal", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        });
    }
}