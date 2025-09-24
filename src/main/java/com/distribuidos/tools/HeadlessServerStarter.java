package com.distribuidos.tools;

import com.distribuidos.server.ServerGUI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.lang.reflect.Method;

public class HeadlessServerStarter {
    private static final Logger logger = LoggerFactory.getLogger(HeadlessServerStarter.class);

    public static void main(String[] args) {
        int tmpPort = 8080;
        if (args != null && args.length > 0) {
            try { tmpPort = Integer.parseInt(args[0]); } catch (Exception ignored) {}
        }
        final int port = tmpPort;

        try {
            // Start the ServerGUI but keep it hidden; invoke startServer to open socket
            SwingUtilities.invokeAndWait(() -> {
                ServerGUI gui = new ServerGUI();
                gui.setVisible(false);
                try {
                    Method startMethod = ServerGUI.class.getDeclaredMethod("startServer");
                    startMethod.setAccessible(true);
                    // set port field value via reflection
                    try {
                        java.lang.reflect.Field portField = ServerGUI.class.getDeclaredField("portField");
                        portField.setAccessible(true);
                        javax.swing.JTextField pf = (javax.swing.JTextField) portField.get(gui);
                        pf.setText(String.valueOf(port));
                    } catch (Exception e) {
                        logger.warn("Não foi possível ajustar a porta via reflection: {}", e.getMessage());
                    }
                    startMethod.invoke(gui);
                } catch (Exception e) {
                    logger.error("Erro ao iniciar servidor headless", e);
                }
            });

            logger.info("Servidor (headless) iniciado na porta {}", port);
            // block thread
            Thread.currentThread().join();
        } catch (Exception e) {
            logger.error("Erro no starter headless", e);
            System.exit(1);
        }
    }
}
