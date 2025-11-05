package com.distribuidos.common;

import java.awt.*;

/**
 * Paleta de cores consistente para a aplicação.
 * Define cores para diferentes contextos: sucesso, erro, aviso, etc.
 */
public class UIColors {
    // Cores primárias
    public static final Color PRIMARY = new Color(25, 118, 210);           // Azul - Ações principais
    public static final Color PRIMARY_DARK = new Color(13, 71, 161);      // Azul escuro
    public static final Color PRIMARY_LIGHT = new Color(66, 165, 245);    // Azul claro
    
    // Cores de status
    public static final Color SUCCESS = new Color(56, 142, 60);           // Verde - Sucesso
    public static final Color SUCCESS_LIGHT = new Color(129, 199, 132);   // Verde claro
    public static final Color WARNING = new Color(245, 127, 23);          // Laranja - Aviso
    public static final Color WARNING_LIGHT = new Color(255, 167, 38);    // Laranja claro
    public static final Color ERROR = new Color(211, 47, 47);             // Vermelho - Erro
    public static final Color ERROR_LIGHT = new Color(239, 83, 80);       // Vermelho claro
    public static final Color INFO = new Color(33, 150, 243);             // Azul claro - Informação
    
    // Cores neutras
    public static final Color NEUTRAL = new Color(117, 117, 117);         // Cinza neutro
    public static final Color NEUTRAL_LIGHT = new Color(189, 189, 189);   // Cinza claro
    public static final Color NEUTRAL_VERY_LIGHT = new Color(238, 238, 238); // Cinza muito claro
    public static final Color TEXT_PRIMARY = new Color(33, 33, 33);       // Texto principal (cinza escuro)
    public static final Color TEXT_SECONDARY = new Color(117, 117, 117);  // Texto secundário
    
    // Cores de background
    public static final Color BG_LIGHT = new Color(250, 250, 250);        // Fundo claro
    public static final Color BG_DEFAULT = Color.WHITE;                   // Fundo branco
    public static final Color BG_PANEL = new Color(245, 245, 245);        // Fundo de painel
    
    // Cores de borda
    public static final Color BORDER = new Color(224, 224, 224);          // Borda padrão
    public static final Color BORDER_LIGHT = new Color(240, 240, 240);    // Borda clara
    
    // Estados de campo
    public static final Color FIELD_VALID = SUCCESS;                      // Campo válido (verde)
    public static final Color FIELD_INVALID = ERROR;                      // Campo inválido (vermelho)
    public static final Color FIELD_FOCUSED = PRIMARY;                    // Campo focado (azul)
    public static final Color FIELD_BACKGROUND = new Color(250, 250, 250); // Fundo de campo
    
    // Cores para transações
    public static final Color TRANSACTION_SENT = ERROR;                   // Transação enviada (vermelho - saída)
    public static final Color TRANSACTION_RECEIVED = SUCCESS;             // Transação recebida (verde - entrada)
    public static final Color TRANSACTION_DEPOSIT = PRIMARY;              // Depósito (azul)
    
    /**
     * Cria uma cor com transparência (alpha)
     */
    public static Color withAlpha(Color color, int alpha) {
        return new Color(color.getRed(), color.getGreen(), color.getBlue(), alpha);
    }
    
    /**
     * Retorna cor mais clara (para hover, etc)
     */
    public static Color lighter(Color color) {
        return new Color(
            Math.min(255, color.getRed() + 30),
            Math.min(255, color.getGreen() + 30),
            Math.min(255, color.getBlue() + 30)
        );
    }
    
    /**
     * Retorna cor mais clara por percentagem
     */
    public static Color lighter(Color color, int percent) {
        return new Color(
            Math.min(255, color.getRed() + (255 - color.getRed()) * percent / 100),
            Math.min(255, color.getGreen() + (255 - color.getGreen()) * percent / 100),
            Math.min(255, color.getBlue() + (255 - color.getBlue()) * percent / 100)
        );
    }
    
    /**
     * Retorna cor mais escura (para pressionado, etc)
     */
    public static Color darker(Color color) {
        return new Color(
            Math.max(0, color.getRed() - 30),
            Math.max(0, color.getGreen() - 30),
            Math.max(0, color.getBlue() - 30)
        );
    }
    
    /**
     * Retorna cor mais escura por percentagem
     */
    public static Color darker(Color color, int percent) {
        return new Color(
            Math.max(0, color.getRed() - color.getRed() * percent / 100),
            Math.max(0, color.getGreen() - color.getGreen() * percent / 100),
            Math.max(0, color.getBlue() - color.getBlue() * percent / 100)
        );
    }
}
