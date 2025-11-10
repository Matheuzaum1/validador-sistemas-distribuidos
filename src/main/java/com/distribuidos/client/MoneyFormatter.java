package com.distribuidos.client;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * DocumentFilter para formatação automática de valores monetários.
 * Formata automaticamente no padrão R$ 0.000,00.
 */
public class MoneyFormatter extends PlainDocument {
    private static final DecimalFormat DECIMAL_FORMAT;
    
    static {
        DecimalFormatSymbols symbols = new DecimalFormatSymbols(new Locale("pt", "BR"));
        symbols.setGroupingSeparator('.');
        symbols.setDecimalSeparator(',');
        DECIMAL_FORMAT = new DecimalFormat("#,##0.00", symbols);
        DECIMAL_FORMAT.setMinimumFractionDigits(2);
        DECIMAL_FORMAT.setMaximumFractionDigits(2);
    }
    
    @Override
    public void insertString(int offset, String str, AttributeSet attr) throws BadLocationException {
        if (str == null) {
            return;
        }
        
        // Remove tudo que não for número
        String numbersOnly = str.replaceAll("[^0-9]", "");
        
        // Pega o texto atual
        String currentText = getText(0, getLength());
        String currentNumbers = currentText.replaceAll("[^0-9]", "");
        
        // Combina números antigos com novos
        String allNumbers = currentNumbers + numbersOnly;
        
        // Limita a 10 dígitos (99.999.999,99)
        if (allNumbers.length() > 10) {
            allNumbers = allNumbers.substring(0, 10);
        }
        
        if (!allNumbers.isEmpty()) {
            // Converte para valor em centavos
            long cents = Long.parseLong(allNumbers);
            double value = cents / 100.0;
            
            // Formata
            String formatted = DECIMAL_FORMAT.format(value);
            
            // Remove tudo e insere o formatado
            super.remove(0, getLength());
            super.insertString(0, formatted, attr);
        }
    }
    
    @Override
    public void remove(int offs, int len) throws BadLocationException {
        String currentText = getText(0, getLength());
        String numbersOnly = currentText.replaceAll("[^0-9]", "");
        
        // Remove o último dígito
        if (numbersOnly.length() > 0) {
            numbersOnly = numbersOnly.substring(0, numbersOnly.length() - 1);
        }
        
        super.remove(0, getLength());
        
        if (!numbersOnly.isEmpty()) {
            long cents = Long.parseLong(numbersOnly);
            double value = cents / 100.0;
            String formatted = DECIMAL_FORMAT.format(value);
            super.insertString(0, formatted, null);
        }
    }
    
    /**
     * Obtém o valor numérico do texto formatado
     */
    public static double parseValue(String formattedText) {
        if (formattedText == null || formattedText.trim().isEmpty()) {
            return 0.0;
        }
        
        // Remove tudo que não for número ou vírgula
        String cleaned = formattedText.replaceAll("[^0-9,]", "");
        
        // Substitui vírgula por ponto
        cleaned = cleaned.replace(',', '.');
        
        try {
            return Double.parseDouble(cleaned);
        } catch (NumberFormatException e) {
            return 0.0;
        }
    }
}
