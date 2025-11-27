package com.distribuidos.errorinjector;

import com.distribuidos.errorinjector.ui.ErrorInjectorGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;

/**
 * Classe principal do Error Injector - Sistema de Injeção de Erros para Testes de Protocolo
 * 
 * Este sistema permite:
 * 1. Simular servidor com respostas malformadas para testar cliente
 * 2. Simular cliente com mensagens malformadas para testar servidor  
 * 3. Proxy entre cliente/servidor para interceptar e modificar mensagens
 * 4. Geração de casos de teste automatizados
 * 
 * @version 1.0.0
 * @author Sistema Distribuído - Error Injector
 */
public class ErrorInjectorMain {
    private static final Logger logger = LoggerFactory.getLogger(ErrorInjectorMain.class);
    
    public static void main(String[] args) {
        try {
            // Configurar Look and Feel
            // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeel());
        } catch (Exception e) {
            logger.warn("Não foi possível configurar Look and Feel do sistema: {}", e.getMessage());
        }
        
        logger.info("Iniciando Error Injector v1.0.0");
        logger.info("Sistema de Injeção de Erros para Protocolo de Sistemas Distribuídos");
        
        // Iniciar interface gráfica
        SwingUtilities.invokeLater(() -> {
            try {
                logger.info("Criando interface gráfica...");
                ErrorInjectorGUI gui = new ErrorInjectorGUI();
                logger.info("Interface gráfica criada, tornando visível...");
                
                // Garantir que a janela apareça
                gui.setVisible(true);
                gui.toFront();
                gui.requestFocus();
                
                logger.info("Interface gráfica iniciada com sucesso");
            } catch (Exception e) {
                logger.error("Erro ao iniciar interface gráfica", e);
                e.printStackTrace();
                
                // Mostrar erro em dialog se possível
                try {
                    JOptionPane.showMessageDialog(null, 
                        "Erro ao iniciar Error Injector:\n" + e.getMessage(),
                        "Erro de Inicialização", 
                        JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    System.err.println("Erro ao iniciar GUI: " + e.getMessage());
                }
                System.exit(1);
            }
        });
        
        // Hook de shutdown para limpeza
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Encerrando Error Injector...");
            // Cleanup será feito automaticamente pelo garbage collector
        }));
    }
}