package com.distribuidos.common;

import java.util.regex.Pattern;

import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.text.JTextComponent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

/**
 * Classe utilitária para validação em tempo real de campos com feedback visual
 * Integra-se com UIColors para feedback por cores
 */
public class ValidationHelper {
    
    // Padrões de validação
    private static final Pattern CPF_PATTERN = Pattern.compile("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern VALOR_PATTERN = Pattern.compile("^\\d+(\\.\\d{1,2})?$");
    
    /**
     * Classe base abstrata para validadores com feedback visual
     */
    private abstract static class BaseValidator implements DocumentListener {
        protected final JTextComponent field;
        protected final Runnable onValidChange;
        
        protected BaseValidator(JTextComponent field, Runnable onValidChange) {
            this.field = field;
            this.onValidChange = onValidChange;
        }
        
        @Override
        public final void insertUpdate(DocumentEvent e) { validate(); }
        
        @Override
        public final void removeUpdate(DocumentEvent e) { validate(); }
        
        @Override
        public final void changedUpdate(DocumentEvent e) { validate(); }
        
        protected final void validate() {
            String text = getText();
            boolean isValid = text.isEmpty() || isFieldValid(text);
            applyFeedback(text, isValid);
        }
        
        protected abstract String getText();
        protected abstract boolean isFieldValid(String text);
        
        protected void applyFeedback(String text, boolean isValid) {
            if (text.isEmpty()) {
                field.setForeground(UIColors.TEXT_PRIMARY);
                field.setBackground(UIColors.FIELD_BACKGROUND);
            } else if (isValid) {
                field.setForeground(UIColors.SUCCESS);
                field.setBackground(UIColors.lighter(UIColors.SUCCESS, 30));
                if (onValidChange != null) {
                    onValidChange.run();
                }
            } else {
                field.setForeground(UIColors.ERROR);
                field.setBackground(UIColors.lighter(UIColors.ERROR, 30));
            }
        }
    }
    
    /**
     * Validador de CPF com feedback visual
     */
    public static class CPFValidator extends BaseValidator {
        public CPFValidator(JTextField field) {
            this(field, null);
        }
        
        public CPFValidator(JTextField field, Runnable onValidChange) {
            super(field, onValidChange);
        }
        
        @Override
        protected String getText() {
            return ((JTextField) field).getText().trim();
        }
        
        @Override
        protected boolean isFieldValid(String text) {
            return CPF_PATTERN.matcher(text).matches();
        }
    }
    
    /**
     * Validador de Nome/Texto com feedback visual
     */
    public static class TextValidator extends BaseValidator {
        private final int minLength;
        
        public TextValidator(JTextField field, int minLength) {
            this(field, minLength, null);
        }
        
        public TextValidator(JTextField field, int minLength, Runnable onValidChange) {
            super(field, onValidChange);
            this.minLength = minLength;
        }
        
        @Override
        protected String getText() {
            return ((JTextField) field).getText().trim();
        }
        
        @Override
        protected boolean isFieldValid(String text) {
            return text.length() >= minLength;
        }
    }
    
    /**
     * Validador de Valor monetário com feedback visual
     */
    public static class ValueValidator extends BaseValidator {
        public ValueValidator(JTextField field) {
            this(field, null);
        }
        
        public ValueValidator(JTextField field, Runnable onValidChange) {
            super(field, onValidChange);
        }
        
        @Override
        protected String getText() {
            return ((JTextField) field).getText().trim().replace(",", ".");
        }
        
        @Override
        protected boolean isFieldValid(String text) {
            try {
                return VALOR_PATTERN.matcher(text).matches() && Double.parseDouble(text) > 0;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    }
    
    /**
     * Validador de Senha com feedback visual
     */
    public static class PasswordValidator extends BaseValidator {
        private final int minLength;
        
        public PasswordValidator(JPasswordField field, int minLength) {
            this(field, minLength, null);
        }
        
        public PasswordValidator(JPasswordField field, int minLength, Runnable onValidChange) {
            super(field, onValidChange);
            this.minLength = minLength;
        }
        
        @Override
        protected String getText() {
            return new String(((JPasswordField) field).getPassword()).trim();
        }
        
        @Override
        protected boolean isFieldValid(String text) {
            return text.length() >= minLength;
        }
    }
    
    /**
     * Método utilitário para validar CPF (padrão)
     */
    public static boolean isValidCPF(String cpf) {
        return cpf != null && CPF_PATTERN.matcher(cpf).matches();
    }
    
    /**
     * Método utilitário para validar email
     */
    public static boolean isValidEmail(String email) {
        return email != null && EMAIL_PATTERN.matcher(email).matches();
    }
    
    /**
     * Método utilitário para validar nome (mínimo 6 caracteres)
     */
    public static boolean isValidNome(String nome) {
        return nome != null && nome.length() >= 6;
    }
    
    /**
     * Método utilitário para validar senha (mínimo 6 caracteres)
     */
    public static boolean isValidSenha(String senha) {
        return senha != null && senha.length() >= 6;
    }
    
    /**
     * Método utilitário para validar valor monetário
     */
    public static boolean isValidValor(String valor) {
        if (valor == null || valor.trim().isEmpty()) return false;
        try {
            double v = Double.parseDouble(valor.replace(",", "."));
            return v > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
