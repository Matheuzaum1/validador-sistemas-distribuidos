package com.distribuidos.server;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.formdev.flatlaf.FlatDarkLaf;
import com.distribuidos.common.MDCManager;
import com.distribuidos.handler.GlobalExceptionHandler;

/**
 * Ponto de entrada da aplicação servidor.
 * Inicializa contexto de logging, Look & Feel e GUI do servidor.
 */
public class ServerMain {
    private static final Logger logger = LoggerFactory.getLogger(ServerMain.class);
    
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
            
            // Inicia a GUI do servidor
            SwingUtilities.invokeLater(() -> {
                try {
                    ServerGUI serverGUI = new ServerGUI();
                    // Se uma porta é fornecida como primeiro arg, auto-iniciar servidor nessa porta
                    if (args != null && args.length > 0) {
                        try {
                            int port = Integer.parseInt(args[0]);
                            serverGUI.setVisible(false);
                            logger.info("Iniciando servidor na porta {} (modo headless)", port);
                            
                            // Refletir portField e iniciar servidor
                            try {
                                java.lang.reflect.Field portField = ServerGUI.class.getDeclaredField("portField");
                                portField.setAccessible(true);
                                javax.swing.JTextField pf = (javax.swing.JTextField) portField.get(serverGUI);
                                pf.setText(String.valueOf(port));
                            } catch (Exception ignored) {
                                logger.debug("Não foi possível acessar portField via reflexão");
                            }

                            try {
                                java.lang.reflect.Method startMethod = ServerGUI.class.getDeclaredMethod("startServer");
                                startMethod.setAccessible(true);
                                startMethod.invoke(serverGUI);
                                logger.info("Servidor auto-iniciado com sucesso na porta {}", port);
                            } catch (Exception e) {
                                GlobalExceptionHandler.logDetailedError("auto-inicialização do servidor", e);
                                logger.error("Erro ao auto-iniciar servidor: {}", e.getMessage());
                                serverGUI.setVisible(true);
                            }
                        } catch (NumberFormatException e) {
                            logger.warn("Porta inválida fornecida: {}, mostrando GUI", args[0]);
                            serverGUI.setVisible(true);
                        }
                    } else {
                        serverGUI.setVisible(true);
                    }

                    logger.info("Servidor iniciado com sucesso");
                } catch (Exception e) {
                    GlobalExceptionHandler.logDetailedError("inicialização do servidor", e);
                    GlobalExceptionHandler.handleException(e, "inicialização do servidor");
                    JOptionPane.showMessageDialog(null, 
                        "Erro ao iniciar servidor: " + e.getMessage(),
                        "Erro Fatal", JOptionPane.ERROR_MESSAGE);
                    System.exit(1);
                }
            });
            
        } catch (Exception e) {
            GlobalExceptionHandler.logDetailedError("inicialização geral do servidor", e);
            logger.error("Erro crítico ao iniciar servidor: {}", e.getMessage(), e);
            System.exit(1);
            
        } finally {
            logger.debug("ServerMain.main() finalizado");
        }
    }
}