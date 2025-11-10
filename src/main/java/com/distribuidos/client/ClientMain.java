package com.distribuidos.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.formdev.flatlaf.FlatDarkLaf;
import com.distribuidos.common.MDCManager;
import com.distribuidos.handler.GlobalExceptionHandler;

import javax.swing.*;

/**
 * Ponto de entrada da aplicação cliente.
 * Inicializa contexto de logging, Look & Feel e GUI.
 */
public class ClientMain {
    private static final Logger logger = LoggerFactory.getLogger(ClientMain.class);
    
    public static void main(String[] args) {
        // Inicializar contexto MDC para rastreamento de logs
        MDCManager.initializeRequestId();
        logger.debug("Contexto MDC inicializado: {}", MDCManager.getContextSummary());
        
        try {
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
                            logger.info("Tema Nimbus aplicado como alternativa");
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
                    GlobalExceptionHandler.logDetailedError("inicialização da GUI", e);
                    GlobalExceptionHandler.handleException(e, "inicialização do cliente");
                    JOptionPane.showMessageDialog(null, 
                        "Erro ao iniciar cliente: " + e.getMessage(),
                        "Erro Fatal", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            });
            
        } catch (Exception e) {
            GlobalExceptionHandler.logDetailedError("inicialização geral do cliente", e);
            logger.error("Erro crítico ao iniciar cliente: {}", e.getMessage(), e);
            System.exit(1);
            
        } finally {
            // MDC não é limpo aqui pois a aplicação vai rodar indefinidamente
            logger.debug("ClientMain.main() finalizado");
        }
    }
}