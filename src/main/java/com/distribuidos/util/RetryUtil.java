package com.distribuidos.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.distribuidos.exception.ConnectionException;

import java.util.function.Supplier;

/**
 * Utilitário para implementar retry com backoff exponencial.
 * Útil para operações que podem ser transitórias (conexão, timeouts, etc).
 */
public class RetryUtil {
    private static final Logger logger = LoggerFactory.getLogger(RetryUtil.class);

    private static final int DEFAULT_MAX_RETRIES = 3;
    private static final long DEFAULT_INITIAL_DELAY_MS = 100;
    private static final double BACKOFF_MULTIPLIER = 2.0;

    /**
     * Executa operação com retry automático.
     * 
     * @param operation A operação a executar
     * @param operationName Nome da operação para logging
     * @param maxRetries Número máximo de tentativas
     * @param initialDelayMs Delay inicial em ms
     * @param <T> Tipo de retorno
     * @return Resultado da operação
     * @throws Exception Se falhar após todas as tentativas
     */
    public static <T> T executeWithRetry(
            Supplier<T> operation,
            String operationName,
            int maxRetries,
            long initialDelayMs) throws Exception {

        String requestId = MDC.get("requestId");
        Exception lastException = null;

        for (int attempt = 1; attempt <= maxRetries; attempt++) {
            try {
                logger.debug("[{}] Tentativa {}/{} de {}", requestId, attempt, maxRetries, operationName);
                return operation.get();
            } catch (Exception e) {
                lastException = e;
                
                if (attempt < maxRetries) {
                    long delayMs = calculateDelay(initialDelayMs, attempt - 1);
                    logger.warn("[{}] Falha na tentativa {}/{} de {} - aguardando {}ms antes de retry",
                        requestId, attempt, maxRetries, operationName, delayMs, e);
                    
                    try {
                        Thread.sleep(delayMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new Exception("Retry interrompido", ie);
                    }
                } else {
                    logger.error("[{}] Falha em {} após {} tentativas", requestId, operationName, maxRetries, e);
                }
            }
        }

        throw lastException;
    }

    /**
     * Executa operação com retry usando valores padrão.
     */
    public static <T> T executeWithRetry(Supplier<T> operation, String operationName) throws Exception {
        return executeWithRetry(operation, operationName, DEFAULT_MAX_RETRIES, DEFAULT_INITIAL_DELAY_MS);
    }

    /**
     * Executa operação void com retry.
     */
    public static void executeVoidWithRetry(
            Runnable operation,
            String operationName,
            int maxRetries,
            long initialDelayMs) throws Exception {

        executeWithRetry(
            () -> {
                operation.run();
                return null;
            },
            operationName,
            maxRetries,
            initialDelayMs
        );
    }

    /**
     * Calcula delay com backoff exponencial.
     * delay = initialDelay * (2 ^ retryCount)
     */
    private static long calculateDelay(long initialDelayMs, int retryCount) {
        return (long) (initialDelayMs * Math.pow(BACKOFF_MULTIPLIER, retryCount));
    }

    /**
     * Valida se deve fazer retry baseado na exceção.
     */
    public static boolean shouldRetry(Exception e) {
        return e instanceof ConnectionException ||
               e instanceof java.net.SocketTimeoutException ||
               e instanceof java.net.ConnectException ||
               "Connection refused".equalsIgnoreCase(e.getMessage());
    }
}
