package com.newpix.client.gui.components;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Campo de senha com botão para mostrar/ocultar a senha.
 */
public class PasswordFieldWithToggle extends JPanel {
    
    private JPasswordField passwordField;
    private JButton toggleButton;
    private boolean passwordVisible = false;
    
    // Ícones usando caracteres Unicode
    private static final String SHOW_ICON = "👁";
    private static final String HIDE_ICON = "🙈";
    
    public PasswordFieldWithToggle() {
        this(20);
    }
    
    public PasswordFieldWithToggle(int columns) {
        setLayout(new BorderLayout());
        initializeComponents(columns);
        setupToggleAction();
    }
    
    private void initializeComponents(int columns) {
        passwordField = new JPasswordField(columns);
        passwordField.setEchoChar('•'); // Usar bullet point em vez de asterisco
        
        toggleButton = new JButton(SHOW_ICON);
        toggleButton.setPreferredSize(new Dimension(30, passwordField.getPreferredSize().height));
        toggleButton.setMargin(new Insets(0, 0, 0, 0));
        toggleButton.setFocusPainted(false);
        toggleButton.setBorderPainted(false);
        toggleButton.setContentAreaFilled(false);
        toggleButton.setToolTipText("Mostrar/Ocultar senha");
        
        add(passwordField, BorderLayout.CENTER);
        add(toggleButton, BorderLayout.EAST);
    }
    
    private void setupToggleAction() {
        toggleButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                togglePasswordVisibility();
            }
        });
    }
    
    private void togglePasswordVisibility() {
        passwordVisible = !passwordVisible;
        
        if (passwordVisible) {
            passwordField.setEchoChar((char) 0); // Mostrar texto
            toggleButton.setText(HIDE_ICON);
            toggleButton.setToolTipText("Ocultar senha");
        } else {
            passwordField.setEchoChar('•'); // Ocultar texto
            toggleButton.setText(SHOW_ICON);
            toggleButton.setToolTipText("Mostrar senha");
        }
        
        // Manter o foco no campo de senha
        passwordField.requestFocusInWindow();
    }
    
    /**
     * Obtém a senha como array de chars
     */
    public char[] getPassword() {
        return passwordField.getPassword();
    }
    
    /**
     * Obtém a senha como String
     */
    public String getPasswordText() {
        return new String(passwordField.getPassword());
    }
    
    /**
     * Define a senha
     */
    public void setPassword(String password) {
        passwordField.setText(password);
    }
    
    /**
     * Limpa o campo de senha
     */
    public void clear() {
        passwordField.setText("");
    }
    
    /**
     * Define se o campo está habilitado
     */
    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        passwordField.setEnabled(enabled);
        toggleButton.setEnabled(enabled);
    }
    
    /**
     * Adiciona listener para quando Enter é pressionado
     */
    public void addActionListener(ActionListener listener) {
        passwordField.addActionListener(listener);
    }
    
    /**
     * Obtém o campo de senha interno
     */
    public JPasswordField getPasswordField() {
        return passwordField;
    }
    
    /**
     * Define o texto de dica (placeholder)
     */
    public void setToolTipText(String text) {
        passwordField.setToolTipText(text);
    }
    
    /**
     * Força o foco no campo de senha
     */
    @Override
    public boolean requestFocusInWindow() {
        return passwordField.requestFocusInWindow();
    }
    
    /**
     * Valida se a senha atende aos critérios mínimos
     */
    public boolean isValidPassword() {
        String password = getPasswordText();
        return password.length() >= 6 && password.length() <= 120;
    }
    
    /**
     * Obtém feedback visual sobre a força da senha
     */
    public String getPasswordStrengthFeedback() {
        String password = getPasswordText();
        
        if (password.isEmpty()) {
            return "";
        }
        
        if (password.length() < 6) {
            return "Muito curta (mínimo 6 caracteres)";
        }
        
        if (password.length() > 120) {
            return "Muito longa (máximo 120 caracteres)";
        }
        
        // Verificar complexidade
        boolean hasLower = password.matches(".*[a-z].*");
        boolean hasUpper = password.matches(".*[A-Z].*");
        boolean hasDigit = password.matches(".*[0-9].*");
        boolean hasSpecial = password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>\\/?].*");
        
        int complexity = 0;
        if (hasLower) complexity++;
        if (hasUpper) complexity++;
        if (hasDigit) complexity++;
        if (hasSpecial) complexity++;
        
        switch (complexity) {
            case 1:
                return "Fraca";
            case 2:
                return "Regular";
            case 3:
                return "Boa";
            case 4:
                return "Forte";
            default:
                return "Válida";
        }
    }
}
