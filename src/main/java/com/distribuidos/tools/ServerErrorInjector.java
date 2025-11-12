package com.distribuidos.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Injetor de Erros para o Servidor
 * Simula diversos tipos de falhas para testes de resiliência
 */
public class ServerErrorInjector {
    private static final Logger logger = LoggerFactory.getLogger(ServerErrorInjector.class);
    private static final Random random = new Random();
    
    // Tipos de erros que podem ser injetados
    public enum ErrorType {
        TIMEOUT,                   // Simula timeout na resposta
        CONNECTION_DROPPED,        // Simula queda de conexão
        DATABASE_ERROR,            // Simula erro no BD
        INVALID_TRANSACTION,       // Transação inválida
        INSUFFICIENT_BALANCE,      // Saldo insuficiente
        MALFORMED_REQUEST,         // Requisição malformada
        AUTHENTICATION_FAILED,     // Falha na autenticação
        RATE_LIMIT_EXCEEDED,       // Limite de requisições excedido
        INTERNAL_SERVER_ERROR,     // Erro interno do servidor
        NETWORK_LATENCY           // Simula latência de rede
    }
    
    private static boolean injectionEnabled = false;
    private static ErrorType currentErrorType = null;
    private static double injectionRate = 0.0; // 0.0 a 1.0 (0% a 100%)
    private static long latencyMs = 0;
    
    /**
     * Ativa injeção de erros
     */
    public static void enableErrorInjection(ErrorType errorType, double rate) {
        if (rate < 0.0 || rate > 1.0) {
            logger.warn("Taxa de injeção inválida: {}. Deve estar entre 0.0 e 1.0", rate);
            return;
        }
        injectionEnabled = true;
        currentErrorType = errorType;
        injectionRate = rate;
        logger.info("🔴 INJEÇÃO DE ERROS ATIVADA: {} (Taxa: {}%)", errorType, (int)(rate * 100));
    }
    
    /**
     * Desativa injeção de erros
     */
    public static void disableErrorInjection() {
        injectionEnabled = false;
        currentErrorType = null;
        injectionRate = 0.0;
        logger.info("🟢 INJEÇÃO DE ERROS DESATIVADA");
    }
    
    /**
     * Ativa latência artificial na rede
     */
    public static void setNetworkLatency(long delayMs) {
        latencyMs = delayMs;
        if (delayMs > 0) {
            logger.info("⏱️ LATÊNCIA DE REDE CONFIGURADA: {}ms", delayMs);
        } else {
            logger.info("⏱️ LATÊNCIA REMOVIDA");
        }
    }
    
    /**
     * Verifica se erro deve ser injetado baseado na probabilidade
     */
    public static boolean shouldInjectError() {
        if (!injectionEnabled) {
            return false;
        }
        return random.nextDouble() < injectionRate;
    }
    
    /**
     * Injeta latência artificial
     */
    public static void injectLatency() {
        if (latencyMs > 0) {
            try {
                Thread.sleep(latencyMs);
            } catch (InterruptedException e) {
                logger.warn("Latência artificial interrompida", e);
            }
        }
    }
    
    /**
     * Retorna o tipo de erro atual
     */
    public static ErrorType getCurrentErrorType() {
        return currentErrorType;
    }
    
    /**
     * Retorna true se injeção está ativa
     */
    public static boolean isInjectionEnabled() {
        return injectionEnabled;
    }
    
    /**
     * Retorna a taxa atual de injeção
     */
    public static double getInjectionRate() {
        return injectionRate;
    }
    
    /**
     * Gera uma mensagem de erro baseada no tipo
     */
    public static String getErrorMessage(ErrorType type) {
        return switch(type) {
            case TIMEOUT -> "⏱️ TIMEOUT: Servidor não respondeu no tempo limite";
            case CONNECTION_DROPPED -> "🔌 CONEXÃO PERDIDA: Conexão com servidor foi encerrada";
            case DATABASE_ERROR -> "🗄️ ERRO BD: Falha ao acessar banco de dados";
            case INVALID_TRANSACTION -> "❌ TRANSAÇÃO INVÁLIDA: Dados da transação não podem ser processados";
            case INSUFFICIENT_BALANCE -> "💰 SALDO INSUFICIENTE: Saldo do usuário é insuficiente";
            case MALFORMED_REQUEST -> "📋 REQUISIÇÃO MALFORMADA: Dados recebidos estão inválidos";
            case AUTHENTICATION_FAILED -> "🔐 FALHA NA AUTENTICAÇÃO: Credenciais não foram validadas";
            case RATE_LIMIT_EXCEEDED -> "⚠️ LIMITE EXCEDIDO: Muitas requisições em pouco tempo";
            case INTERNAL_SERVER_ERROR -> "🔥 ERRO INTERNO: Servidor encontrou um erro inesperado";
            case NETWORK_LATENCY -> "🌐 LATÊNCIA: Resposta atrasada pela rede";
        };
    }
    
    /**
     * Retorna status formatado para log
     */
    public static String getStatus() {
        if (!injectionEnabled) {
            return "🟢 Injeção de erros: DESATIVADA";
        }
        return String.format("🔴 Injeção ativa | Tipo: %s | Taxa: %.1f%% | Latência: %dms",
            currentErrorType, injectionRate * 100, latencyMs);
    }
}
