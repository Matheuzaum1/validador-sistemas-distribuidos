package com.newpix.util;

/**
 * Utilitário para formatação e validação de CPF.
 * Permite conversão automática entre formatos com e sem máscaras.
 * 
 * @author NewPix Team
 * @version 1.0
 */
public class CpfUtil {
    
    /**
     * Remove formatação do CPF, mantendo apenas dígitos.
     * 
     * @param cpf CPF com ou sem formatação
     * @return CPF apenas com números (ex: "12345678901")
     */
    public static String limparCpf(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return "";
        }
        
        // Remove pontos, hífens, espaços e outros caracteres não numéricos
        return cpf.replaceAll("[^0-9]", "");
    }
    
    /**
     * Formata CPF adicionando pontos e hífen.
     * 
     * @param cpf CPF apenas com números
     * @return CPF formatado (ex: "123.456.789-01")
     */
    public static String formatarCpf(String cpf) {
        String cpfLimpo = limparCpf(cpf);
        
        if (cpfLimpo.length() != 11) {
            return cpf; // Retorna original se não tem 11 dígitos
        }
        
        return String.format("%s.%s.%s-%s", 
            cpfLimpo.substring(0, 3),
            cpfLimpo.substring(3, 6),
            cpfLimpo.substring(6, 9),
            cpfLimpo.substring(9, 11)
        );
    }
    
    /**
     * Valida se o CPF tem formato válido (com ou sem máscara).
     * 
     * @param cpf CPF a ser validado
     * @return true se o formato é válido
     */
    public static boolean validarFormatoCpf(String cpf) {
        if (cpf == null || cpf.isEmpty()) {
            return false;
        }
        
        String cpfLimpo = limparCpf(cpf);
        
        // Deve ter exatamente 11 dígitos
        if (cpfLimpo.length() != 11) {
            return false;
        }
        
        // Não pode ser sequência de números iguais
        if (cpfLimpo.matches("(\\d)\\1{10}")) {
            return false; // Ex: 11111111111, 00000000000
        }
        
        return true;
    }
    
    /**
     * Validação completa do CPF incluindo dígitos verificadores.
     * 
     * @param cpf CPF a ser validado
     * @return true se o CPF é válido
     */
    public static boolean validarCpf(String cpf) {
        if (!validarFormatoCpf(cpf)) {
            return false;
        }
        
        String cpfLimpo = limparCpf(cpf);
        
        try {
            // Calcula primeiro dígito verificador
            int soma = 0;
            for (int i = 0; i < 9; i++) {
                soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (10 - i);
            }
            int primeiroDigito = 11 - (soma % 11);
            if (primeiroDigito >= 10) primeiroDigito = 0;
            
            // Calcula segundo dígito verificador
            soma = 0;
            for (int i = 0; i < 10; i++) {
                soma += Character.getNumericValue(cpfLimpo.charAt(i)) * (11 - i);
            }
            int segundoDigito = 11 - (soma % 11);
            if (segundoDigito >= 10) segundoDigito = 0;
            
            // Verifica se os dígitos calculados conferem
            return Character.getNumericValue(cpfLimpo.charAt(9)) == primeiroDigito &&
                   Character.getNumericValue(cpfLimpo.charAt(10)) == segundoDigito;
                   
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * Normaliza o CPF para formato padrão do sistema (apenas números).
     * Usado para armazenamento no banco de dados.
     * 
     * @param cpf CPF em qualquer formato
     * @return CPF limpo e validado, ou null se inválido
     */
    public static String normalizarCpf(String cpf) {
        if (!validarFormatoCpf(cpf)) {
            return null;
        }
        return limparCpf(cpf);
    }
    
    /**
     * Converte CPF para formato de exibição (com máscara).
     * Usado para mostrar na interface do usuário.
     * 
     * @param cpf CPF em qualquer formato
     * @return CPF formatado com pontos e hífen, ou original se inválido
     */
    public static String paraExibicao(String cpf) {
        if (validarFormatoCpf(cpf)) {
            return formatarCpf(cpf);
        }
        return cpf; // Retorna original se inválido
    }
    
    /**
     * Verifica se o CPF está no formato com máscara.
     * 
     * @param cpf CPF a verificar
     * @return true se tem formato XXX.XXX.XXX-XX
     */
    public static boolean temMascara(String cpf) {
        if (cpf == null) return false;
        return cpf.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}");
    }
    
    /**
     * Verifica se o CPF está no formato apenas números.
     * 
     * @param cpf CPF a verificar
     * @return true se tem formato XXXXXXXXXXX (11 dígitos)
     */
    public static boolean apenasNumeros(String cpf) {
        if (cpf == null) return false;
        return cpf.matches("\\d{11}");
    }
}
