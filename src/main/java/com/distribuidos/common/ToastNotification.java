package com.distribuidos.common;

import java.awt.Color;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

/**
 * Notificações tipo Toast - mensagens não-intrusivas que aparecem e desaparecem
 * Usa JOptionPane para compatibilidade máxima
 */
public class ToastNotification {
    
    public enum NotificationType {
        SUCCESS(JOptionPane.INFORMATION_MESSAGE, UIColors.SUCCESS),
        ERROR(JOptionPane.ERROR_MESSAGE, UIColors.ERROR),
        WARNING(JOptionPane.WARNING_MESSAGE, UIColors.WARNING),
        INFO(JOptionPane.INFORMATION_MESSAGE, UIColors.INFO);
        
        public final int messageType;
        public final Color color;
        
        NotificationType(int messageType, Color color) {
            this.messageType = messageType;
            this.color = color;
        }
    }
    
    private static final int TOAST_DURATION = 3000; // 3 segundos
    
    /**
     * Mostra uma notificação de Toast
     */
    private static void showToast(String title, String message, NotificationType type) {
        // Usar thread separada para não bloquear a UI
        SwingUtilities.invokeLater(() -> {
            // Criar um frame invisível como parent
            JFrame frame = new JFrame();
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setUndecorated(true);
            frame.setOpacity(0);
            frame.setSize(0, 0);
            
            // Mostrar diálogo
            JOptionPane.showMessageDialog(
                frame,
                message,
                title,
                type.messageType
            );
            
            frame.dispose();
        });
    }
    
    /**
     * Factory method para criar e mostrar notificação de sucesso
     */
    public static void showSuccess(String title, String message) {
        showToast(title, message, NotificationType.SUCCESS);
    }
    
    /**
     * Factory method para criar e mostrar notificação de erro
     */
    public static void showError(String title, String message) {
        showToast(title, message, NotificationType.ERROR);
    }
    
    /**
     * Factory method para criar e mostrar notificação de aviso
     */
    public static void showWarning(String title, String message) {
        showToast(title, message, NotificationType.WARNING);
    }
    
    /**
     * Factory method para criar e mostrar notificação de informação
     */
    public static void showInfo(String title, String message) {
        showToast(title, message, NotificationType.INFO);
    }
}
