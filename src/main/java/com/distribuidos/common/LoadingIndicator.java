package com.distribuidos.common;

import java.awt.FlowLayout;
import java.awt.Font;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * Classe para gerenciar indicadores visuais de carregamento e estado
 * Fornece componentes reutilizáveis para feedback do usuário
 */
public class LoadingIndicator extends JPanel {
    
    private final JLabel iconLabel;
    private final JLabel textLabel;
    private Timer animationTimer;
    private int animationFrame = 0;
    private final String[] loadingFrames = {"⠋", "⠙", "⠹", "⠸", "⠼", "⠴", "⠦", "⠧", "⠇", "⠏"};
    
    public LoadingIndicator(String initialText) {
        setLayout(new FlowLayout(FlowLayout.CENTER, 10, 0));
        setOpaque(false);
        
        iconLabel = new JLabel(loadingFrames[0]);
        iconLabel.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 16));
        iconLabel.setForeground(UIColors.WARNING);
        add(iconLabel);
        
        textLabel = new JLabel(initialText);
        textLabel.setFont(new Font(Font.SANS_SERIF, Font.PLAIN, 12));
        textLabel.setForeground(UIColors.TEXT_PRIMARY);
        add(textLabel);
    }
    
    /**
     * Inicia animação de carregamento
     */
    public void start() {
        if (animationTimer != null && animationTimer.isRunning()) {
            return;
        }
        
        animationTimer = new Timer(100, e -> {
            animationFrame = (animationFrame + 1) % loadingFrames.length;
            iconLabel.setText(loadingFrames[animationFrame]);
        });
        animationTimer.start();
        setVisible(true);
    }
    
    /**
     * Para animação e mostra sucesso
     */
    public void stop(boolean success) {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        
        if (success) {
            iconLabel.setText("✓");
            iconLabel.setForeground(UIColors.SUCCESS);
            textLabel.setForeground(UIColors.SUCCESS);
        } else {
            iconLabel.setText("✗");
            iconLabel.setForeground(UIColors.ERROR);
            textLabel.setForeground(UIColors.ERROR);
        }
    }
    
    /**
     * Atualiza o texto do indicador
     */
    public void setText(String text) {
        textLabel.setText(text);
    }
    
    /**
     * Esconde o indicador
     */
    public void hide() {
        if (animationTimer != null) {
            animationTimer.stop();
        }
        setVisible(false);
    }
}
