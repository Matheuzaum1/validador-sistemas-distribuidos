package com.newpix.util;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Sistema de logging em tempo real para operações do NewPix.
 * Mostra logs no console/terminal com timestamps e categorização.
 */
public class CLILogger {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private static boolean enableLogging = true;
    
    // Cores ANSI para terminal
    private static final String RESET = "\u001B[0m";
    private static final String RED = "\u001B[31m";
    private static final String GREEN = "\u001B[32m";
    private static final String YELLOW = "\u001B[33m";
    private static final String BLUE = "\u001B[34m";
    private static final String PURPLE = "\u001B[35m";
    private static final String CYAN = "\u001B[36m";
    
    public enum LogLevel {
        INFO(BLUE, "[INFO]"),
        SUCCESS(GREEN, "[SUCCESS]"),
        WARNING(YELLOW, "[WARNING]"),
        ERROR(RED, "[ERROR]"),
        DEBUG(PURPLE, "[DEBUG]"),
        TRANSACTION(CYAN, "[TRANSACTION]");
        
        private final String color;
        private final String prefix;
        
        LogLevel(String color, String prefix) {
            this.color = color;
            this.prefix = prefix;
        }
    }
    
    /**
     * Log genérico com nível especificado
     */
    public static void log(LogLevel level, String message) {
        if (!enableLogging) return;
        
        String timestamp = LocalDateTime.now().format(FORMATTER);
        String coloredMessage = String.format("%s%s %s - %s%s", 
            level.color, level.prefix, timestamp, message, RESET);
        
        System.out.println(coloredMessage);
    }
    
    /**
     * Log de informações gerais
     */
    public static void info(String message) {
        log(LogLevel.INFO, message);
    }
    
    /**
     * Log de operações bem-sucedidas
     */
    public static void success(String message) {
        log(LogLevel.SUCCESS, message);
    }
    
    /**
     * Log de avisos
     */
    public static void warning(String message) {
        log(LogLevel.WARNING, message);
    }
    
    /**
     * Log de erros
     */
    public static void error(String message) {
        log(LogLevel.ERROR, message);
    }
    
    /**
     * Log de debug
     */
    public static void debug(String message) {
        log(LogLevel.DEBUG, message);
    }
    
    /**
     * Log específico para transações
     */
    public static void transaction(String operation, String details) {
        log(LogLevel.TRANSACTION, String.format("%s - %s", operation, details));
    }
    
    /**
     * Log de login
     */
    public static void login(String cpf, boolean success) {
        if (success) {
            success(String.format("Login realizado: CPF %s", 
                maskCpf(cpf)));
        } else {
            error(String.format("Falha no login: CPF %s", 
                maskCpf(cpf)));
        }
    }
    
    /**
     * Log de PIX
     */
    public static void pix(String fromCpf, String toCpf, double valor, boolean success) {
        String operation = String.format("PIX R$ %.2f: %s -> %s", 
            valor, maskCpf(fromCpf), maskCpf(toCpf));
        
        if (success) {
            transaction("PIX_ENVIADO", operation);
        } else {
            error("PIX_FALHOU: " + operation);
        }
    }
    
    /**
     * Log de depósito
     */
    public static void deposit(String cpf, double valor, boolean success) {
        String operation = String.format("Depósito R$ %.2f para %s", 
            valor, maskCpf(cpf));
        
        if (success) {
            transaction("DEPOSITO_REALIZADO", operation);
        } else {
            error("DEPOSITO_FALHOU: " + operation);
        }
    }
    
    /**
     * Log de conexão
     */
    public static void connection(String host, int port, boolean connected) {
        if (connected) {
            success(String.format("Conectado ao servidor %s:%d", host, port));
        } else {
            error(String.format("Falha na conexão com %s:%d", host, port));
        }
    }
    
    /**
     * Mascara CPF para logs (mostra apenas primeiros e últimos dígitos)
     */
    private static String maskCpf(String cpf) {
        if (cpf == null || cpf.length() < 8) {
            return "***";
        }
        return cpf.substring(0, 3) + ".***.***-" + cpf.substring(cpf.length() - 2);
    }
    
    /**
     * Abilita ou desabilita o logging
     */
    public static void setLoggingEnabled(boolean enabled) {
        enableLogging = enabled;
        if (enabled) {
            info("Logging habilitado");
        }
    }
    
    /**
     * Mostra banner de inicialização
     */
    public static void showBanner() {
        System.out.println(CYAN + "╔════════════════════════════════════════╗" + RESET);
        System.out.println(CYAN + "║            NewPix Client               ║" + RESET);
        System.out.println(CYAN + "║        Sistema Bancário Digital        ║" + RESET);
        System.out.println(CYAN + "║         Logs em Tempo Real             ║" + RESET);
        System.out.println(CYAN + "╚════════════════════════════════════════╝" + RESET);
        System.out.println();
        info("Sistema iniciado - Logs ativos");
    }
}
