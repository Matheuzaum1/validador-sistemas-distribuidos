package com.newpix.client.gui.theme;

import javax.swing.*;
import java.awt.*;

/**
 * Temas e melhorias visuais para a interface do NewPix.
 */
public class NewPixTheme {
    
    // Paleta de cores do NewPix
    public static final Color PRIMARY_COLOR = new Color(33, 150, 243);      // Azul
    public static final Color PRIMARY_DARK = new Color(25, 118, 210);       // Azul escuro
    public static final Color ACCENT_COLOR = new Color(76, 175, 80);        // Verde
    public static final Color WARNING_COLOR = new Color(255, 152, 0);       // Laranja
    public static final Color ERROR_COLOR = new Color(244, 67, 54);         // Vermelho
    public static final Color SUCCESS_COLOR = new Color(76, 175, 80);       // Verde
    
    public static final Color BACKGROUND_LIGHT = new Color(250, 250, 250);  // Cinza claro
    public static final Color BACKGROUND_CARD = new Color(255, 255, 255);   // Branco
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);         // Cinza escuro
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);    // Cinza médio
    
    // Fontes
    public static final Font FONT_TITLE = new Font("Segoe UI", Font.BOLD, 16);
    public static final Font FONT_SUBTITLE = new Font("Segoe UI", Font.BOLD, 14);
    public static final Font FONT_BODY = new Font("Segoe UI", Font.PLAIN, 12);
    public static final Font FONT_SMALL = new Font("Segoe UI", Font.PLAIN, 10);
    
    /**
     * Aplica o tema NewPix à aplicação
     */
    public static void applyTheme() {
        try {
            // Usar Look and Feel nativo do sistema
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            
            // Customizar cores
            UIManager.put("Button.background", BACKGROUND_CARD);
            UIManager.put("Button.foreground", TEXT_PRIMARY);
            UIManager.put("Button.select", PRIMARY_COLOR);
            UIManager.put("Button.focus", PRIMARY_DARK);
            
            UIManager.put("Panel.background", BACKGROUND_LIGHT);
            UIManager.put("TextField.background", BACKGROUND_CARD);
            UIManager.put("TextField.foreground", TEXT_PRIMARY);
            UIManager.put("TextField.selectionBackground", PRIMARY_COLOR);
            
            UIManager.put("Table.background", BACKGROUND_CARD);
            UIManager.put("Table.selectionBackground", PRIMARY_COLOR);
            UIManager.put("Table.gridColor", new Color(224, 224, 224));
            
            UIManager.put("TabbedPane.selected", BACKGROUND_CARD);
            UIManager.put("TabbedPane.background", BACKGROUND_LIGHT);
            
        } catch (Exception e) {
            System.err.println("Erro ao aplicar tema: " + e.getMessage());
        }
    }
    
    /**
     * Cria um botão estilizado
     */
    public static JButton createStyledButton(String text, ButtonStyle style) {
        JButton button = new JButton(text);
        
        button.setFont(FONT_BODY);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setOpaque(true);
        
        switch (style) {
            case PRIMARY:
                button.setBackground(PRIMARY_COLOR);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(PRIMARY_DARK, 1));
                break;
                
            case SUCCESS:
                button.setBackground(SUCCESS_COLOR);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(SUCCESS_COLOR.darker(), 1));
                break;
                
            case WARNING:
                button.setBackground(WARNING_COLOR);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(WARNING_COLOR.darker(), 1));
                break;
                
            case ERROR:
                button.setBackground(ERROR_COLOR);
                button.setForeground(Color.WHITE);
                button.setBorder(BorderFactory.createLineBorder(ERROR_COLOR.darker(), 1));
                break;
                
            case SECONDARY:
            default:
                button.setBackground(BACKGROUND_CARD);
                button.setForeground(TEXT_PRIMARY);
                button.setBorder(BorderFactory.createLineBorder(TEXT_SECONDARY, 1));
                break;
        }
        
        // Efeito hover
        button.addMouseListener(new java.awt.event.MouseAdapter() {
            Color originalBg = button.getBackground();
            
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                button.setBackground(originalBg.brighter());
            }
            
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                button.setBackground(originalBg);
            }
        });
        
        return button;
    }
    
    /**
     * Cria um painel com cartão estilizado
     */
    public static JPanel createCard() {
        JPanel panel = new JPanel();
        panel.setBackground(BACKGROUND_CARD);
        panel.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(224, 224, 224), 1),
            BorderFactory.createEmptyBorder(20, 20, 20, 20)
        ));
        return panel;
    }
    
    /**
     * Cria um título estilizado
     */
    public static JLabel createTitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_TITLE);
        label.setForeground(TEXT_PRIMARY);
        return label;
    }
    
    /**
     * Cria um subtítulo estilizado
     */
    public static JLabel createSubtitle(String text) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_SUBTITLE);
        label.setForeground(TEXT_SECONDARY);
        return label;
    }
    
    /**
     * Cria um rótulo de status colorido
     */
    public static JLabel createStatusLabel(String text, StatusType status) {
        JLabel label = new JLabel(text);
        label.setFont(FONT_BODY);
        label.setOpaque(true);
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        
        switch (status) {
            case SUCCESS:
                label.setBackground(SUCCESS_COLOR);
                label.setForeground(Color.WHITE);
                break;
            case WARNING:
                label.setBackground(WARNING_COLOR);
                label.setForeground(Color.WHITE);
                break;
            case ERROR:
                label.setBackground(ERROR_COLOR);
                label.setForeground(Color.WHITE);
                break;
            case INFO:
            default:
                label.setBackground(PRIMARY_COLOR);
                label.setForeground(Color.WHITE);
                break;
        }
        
        return label;
    }
    
    /**
     * Estiliza uma tabela
     */
    public static void styleTable(JTable table) {
        table.setBackground(BACKGROUND_CARD);
        table.setForeground(TEXT_PRIMARY);
        table.setFont(FONT_BODY);
        table.setRowHeight(25);
        table.setShowVerticalLines(false);
        table.setShowHorizontalLines(true);
        table.setGridColor(new Color(240, 240, 240));
        table.setSelectionBackground(PRIMARY_COLOR);
        table.setSelectionForeground(Color.WHITE);
        
        // Cabeçalho
        table.getTableHeader().setBackground(BACKGROUND_LIGHT);
        table.getTableHeader().setForeground(TEXT_PRIMARY);
        table.getTableHeader().setFont(FONT_SUBTITLE);
        table.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, TEXT_SECONDARY));
    }
    
    /**
     * Adiciona ícones aos botões (usando Unicode)
     */
    public static class Icons {
        public static final String LOGIN = "🔐";
        public static final String LOGOUT = "🚪";
        public static final String MONEY = "💰";
        public static final String SEND = "📤";
        public static final String RECEIVE = "📥";
        public static final String REFRESH = "🔄";
        public static final String SETTINGS = "⚙️";
        public static final String SCAN = "🔍";
        public static final String CONNECT = "🔌";
        public static final String SUCCESS = "✅";
        public static final String ERROR = "❌";
        public static final String WARNING = "⚠️";
        public static final String INFO = "ℹ️";
        public static final String DEVICES = "📱";
        public static final String NETWORK = "🌐";
    }
    
    public enum ButtonStyle {
        PRIMARY, SECONDARY, SUCCESS, WARNING, ERROR
    }
    
    public enum StatusType {
        SUCCESS, WARNING, ERROR, INFO
    }
}
