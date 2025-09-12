package com.newpix.client.gui.components;

import com.newpix.util.UserPreferences;
import com.newpix.util.CpfUtil;

import javax.swing.*;
import javax.swing.text.AbstractDocument;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.DocumentFilter;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

/**
 * Campo de CPF com auto-complete e formatação automática.
 */
public class CpfField extends JTextField {
    
    private static final int CPF_LENGTH = 14; // 000.000.000-00
    
    public CpfField() {
        super(CPF_LENGTH);
        setupCpfFormatting();
        setupAutoComplete();
        setupValidation();
    }
    
    /**
     * Configura formatação automática do CPF
     */
    private void setupCpfFormatting() {
        ((AbstractDocument) getDocument()).setDocumentFilter(new DocumentFilter() {
            @Override
            public void insertString(FilterBypass fb, int offset, String string, AttributeSet attr) 
                    throws BadLocationException {
                
                String newStr = formatCpf(fb.getDocument().getText(0, fb.getDocument().getLength()) + string);
                super.replace(fb, 0, fb.getDocument().getLength(), newStr, attr);
            }
            
            @Override
            public void replace(FilterBypass fb, int offset, int length, String text, AttributeSet attrs) 
                    throws BadLocationException {
                
                String currentText = fb.getDocument().getText(0, fb.getDocument().getLength());
                String beforeOffset = currentText.substring(0, offset);
                String afterOffset = currentText.substring(offset + length);
                String newStr = formatCpf(beforeOffset + text + afterOffset);
                
                super.replace(fb, 0, fb.getDocument().getLength(), newStr, attrs);
            }
        });
    }
    
    /**
     * Configura auto-complete baseado no histórico
     */
    private void setupAutoComplete() {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                String lastCpf = UserPreferences.getLastCpf();
                if (!lastCpf.isEmpty() && getText().trim().isEmpty()) {
                    setText(formatCpf(lastCpf));
                    selectAll();
                }
            }
        });
        
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_TAB && getText().trim().isEmpty()) {
                    String lastCpf = UserPreferences.getLastCpf();
                    if (!lastCpf.isEmpty()) {
                        setText(formatCpf(lastCpf));
                        selectAll();
                        e.consume();
                    }
                }
            }
        });
    }
    
    /**
     * Configura validação visual
     */
    private void setupValidation() {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(FocusEvent e) {
                validateCpf();
            }
        });
    }
    
    /**
     * Valida o CPF e muda a cor da borda
     */
    private void validateCpf() {
        String cpf = getCleanCpf();
        if (!cpf.isEmpty()) {
            if (CpfUtil.validarCpf(cpf)) {
                setBorder(BorderFactory.createLineBorder(java.awt.Color.GREEN, 2));
                setToolTipText("CPF válido");
            } else {
                setBorder(BorderFactory.createLineBorder(java.awt.Color.RED, 2));
                setToolTipText("CPF inválido");
            }
        } else {
            setBorder(UIManager.getBorder("TextField.border"));
            setToolTipText(null);
        }
    }
    
    /**
     * Formata o CPF com pontos e hífen
     */
    private String formatCpf(String cpf) {
        if (cpf == null) return "";
        
        // Remove tudo que não é dígito
        String numbers = cpf.replaceAll("[^0-9]", "");
        
        // Limita a 11 dígitos
        if (numbers.length() > 11) {
            numbers = numbers.substring(0, 11);
        }
        
        // Aplica formatação progressiva
        StringBuilder formatted = new StringBuilder();
        for (int i = 0; i < numbers.length(); i++) {
            if (i == 3 || i == 6) {
                formatted.append('.');
            } else if (i == 9) {
                formatted.append('-');
            }
            formatted.append(numbers.charAt(i));
        }
        
        return formatted.toString();
    }
    
    /**
     * Retorna o CPF sem formatação
     */
    public String getCleanCpf() {
        return getText().replaceAll("[^0-9]", "");
    }
    
    /**
     * Define o CPF (será formatado automaticamente)
     */
    public void setCpf(String cpf) {
        setText(formatCpf(cpf));
        validateCpf();
    }
    
    /**
     * Verifica se o CPF é válido
     */
    public boolean isValidCpf() {
        String cpf = getCleanCpf();
        return !cpf.isEmpty() && CpfUtil.validarCpf(cpf);
    }
    
    /**
     * Limpa o campo e reseta a validação
     */
    @Override
    public void setText(String text) {
        super.setText(text);
        setBorder(UIManager.getBorder("TextField.border"));
        setToolTipText(null);
    }
}
