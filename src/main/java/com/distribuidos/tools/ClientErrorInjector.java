package com.distribuidos.tools;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

/**
 * Injetor de Erros para o Cliente
 * Simula diversos tipos de falhas para testes de resiliência
 */
public class ClientErrorInjector {
    private static final Logger logger = LoggerFactory.getLogger(ClientErrorInjector.class);
    private static final Random random = new Random();
    
    // Tipos de erros que podem ser injetados no cliente
    public enum ErrorType {
        CONNECTION_REFUSED,        // Servidor recusa conexão
        CONNECTION_TIMEOUT,        // Timeout ao conectar
        READ_TIMEOUT,             // Timeout na leitura
        NETWORK_UNREACHABLE,      // Rede inacessível
        SOCKET_CLOSED,            // Socket fechado inesperadamente
        INVALID_RESPONSE,         // Resposta inválida do servidor
        SERIALIZATION_ERROR,      // Erro ao serializar dados
        AUTHENTICATION_ERROR,     // Erro na autenticação
        MEMORY_ERROR,             // Simula erro de memória
        INVALID_INPUT             // Entrada de usuário inválida
    }
    
    private static boolean injectionEnabled = false;
    private static ErrorType currentErrorType = null;
    private static double injectionRate = 0.0; // 0.0 a 1.0
    private static long latencyMs = 0;
    
    /**
     * Ativa injeção de erros no cliente
     */
    public static void enableErrorInjection(ErrorType errorType, double rate) {
        if (rate < 0.0 || rate > 1.0) {
            logger.warn("Taxa de injeção inválida: {}. Deve estar entre 0.0 e 1.0", rate);
            return;
        }
        injectionEnabled = true;
        currentErrorType = errorType;
        injectionRate = rate;
        logger.info("🔴 INJEÇÃO DE ERROS CLIENT ATIVADA: {} (Taxa: {}%)", errorType, (int)(rate * 100));
    }
    
    /**
     * Desativa injeção de erros
     */
    public static void disableErrorInjection() {
        injectionEnabled = false;
        currentErrorType = null;
        injectionRate = 0.0;
        logger.info("🟢 INJEÇÃO DE ERROS CLIENT DESATIVADA");
    }
    
    /**
     * Ativa latência artificial no cliente
     */
    public static void setNetworkLatency(long delayMs) {
        latencyMs = delayMs;
        if (delayMs > 0) {
            logger.info("⏱️ LATÊNCIA DO CLIENTE CONFIGURADA: {}ms", delayMs);
        } else {
            logger.info("⏱️ LATÊNCIA DO CLIENTE REMOVIDA");
        }
    }
    
    /**
     * Verifica se erro deve ser injetado
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
                logger.warn("Latência artificial do cliente interrompida", e);
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
            case CONNECTION_REFUSED -> "🔌 CONEXÃO RECUSADA: Servidor recusou a conexão";
            case CONNECTION_TIMEOUT -> "⏱️ TIMEOUT NA CONEXÃO: Não conseguiu conectar ao servidor";
            case READ_TIMEOUT -> "📖 TIMEOUT NA LEITURA: Servidor não respondeu a tempo";
            case NETWORK_UNREACHABLE -> "🌐 REDE INACESSÍVEL: Não consegue alcançar o servidor";
            case SOCKET_CLOSED -> "🔌 SOCKET FECHADO: Conexão foi encerrada inesperadamente";
            case INVALID_RESPONSE -> "📨 RESPOSTA INVÁLIDA: Dados recebidos são inválidos";
            case SERIALIZATION_ERROR -> "📦 ERRO DE SERIALIZAÇÃO: Não consegue processar dados";
            case AUTHENTICATION_ERROR -> "🔐 ERRO DE AUTENTICAÇÃO: Falha ao autenticar";
            case MEMORY_ERROR -> "💾 ERRO DE MEMÓRIA: Sem memória para continuar";
            case INVALID_INPUT -> "⌨️ ENTRADA INVÁLIDA: Dados digitados estão inválidos";
        };
    }
    
    /**
     * Retorna status formatado para log
     */
    public static String getStatus() {
        if (!injectionEnabled) {
            return "🟢 Injeção de erros (Cliente): DESATIVADA";
        }
        return String.format("🔴 Injeção cliente ativa | Tipo: %s | Taxa: %.1f%% | Latência: %dms",
            currentErrorType, injectionRate * 100, latencyMs);
    }
}
