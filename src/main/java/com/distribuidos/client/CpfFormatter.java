package com.distribuidos.client;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;

/**
 * DocumentFilter para formatação automática de CPF.
 * Permite apenas números e formata automaticamente no padrão 000.000.000-00.
 */
public class CpfFormatter extends PlainDocument {
    
    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) {
            return;
        }
        
        // Remove tudo que não for número
        String numbersOnly = str.replaceAll("[^0-9]", "");
        
        // Verifica se não excede 11 dígitos
        String currentText = getText(0, getLength());
        String currentNumbers = currentText.replaceAll("[^0-9]", "");
        
        if (currentNumbers.length() + numbersOnly.length() > 11) {
            // Trunca para não exceder 11 dígitos
            int maxLength = 11 - currentNumbers.length();
            if (maxLength > 0) {
                numbersOnly = numbersOnly.substring(0, Math.min(maxLength, numbersOnly.length()));
            } else {
                return; // Não adiciona nada se já tem 11 dígitos
            }
        }
        
        if (!numbersOnly.isEmpty()) {
            // Remove o texto atual e insere o formatado
            super.remove(0, getLength());
            String newText = currentNumbers + numbersOnly;
            String formatted = formatCpf(newText);
            super.insertString(0, formatted, attr);
        }
    }
    
    @Override
    public void remove(int offs, int len) throws BadLocationException {
        super.remove(offs, len);
        
        // Reformata após remoção
        String currentText = getText(0, getLength());
        String numbersOnly = currentText.replaceAll("[^0-9]", "");
        
        super.remove(0, getLength());
        String formatted = formatCpf(numbersOnly);
        super.insertString(0, formatted, null);
    }
    
    private String formatCpf(String numbers) {
        if (numbers.length() == 0) {
            return "";
        }
        
        StringBuilder formatted = new StringBuilder();
        
        for (int i = 0; i < numbers.length(); i++) {
            if (i == 3 || i == 6) {
                formatted.append(".");
            } else if (i == 9) {
                formatted.append("-");
            }
            formatted.append(numbers.charAt(i));
        }
        
        return formatted.toString();
    }
}