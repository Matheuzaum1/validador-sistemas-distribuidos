package com.distribuidos.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import com.distribuidos.exception.*;

import java.sql.SQLException;

/**
 * Centralizador global de tratamento de exceções.
 * Proporciona tratamento consistente e logging estruturado.
 */
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Trata exceção genérica com contexto.
     * Retorna mensagem amigável ao usuário.
     */
    public static String handleException(Exception e, String context) {
        String requestId = MDC.get("requestId");
        
        if (e instanceof ApplicationException) {
            return handleApplicationException((ApplicationException) e, requestId, context);
        }
        
        if (e instanceof SQLException) {
            return handleDatabaseException((SQLException) e, requestId, context);
        }
        
        // Exceção inesperada
        logger.error("[{}] Erro inesperado em {}", requestId, context, e);
        return buildErrorResponse(ErrorCode.UNEXPECTED_ERROR, "Erro interno do sistema");
    }

    /**
     * Trata exceções da aplicação (ApplicationException).
     */
    private static String handleApplicationException(ApplicationException e, String requestId, String context) {
        ErrorCode code = e.getErrorCode();
        
        // Log apropriado por nível
        switch (code) {
            case VALIDATION_FAILED:
            case INVALID_CPF:
            case INVALID_PASSWORD:
            case INVALID_FORMAT:
                logger.debug("[{}] Validação falhou em {}: {}", requestId, context, e.getMessage());
                break;
                
            case INSUFFICIENT_BALANCE:
            case INVALID_TRANSACTION:
                logger.warn("[{}] Erro de negócio em {}: {}", requestId, context, e.getMessage());
                break;
                
            case USER_NOT_FOUND:
            case USER_ALREADY_EXISTS:
            case INVALID_CREDENTIALS:
                logger.warn("[{}] Erro de autenticação em {}: {}", requestId, context, e.getMessage());
                break;
                
            default:
                logger.error("[{}] Erro da aplicação [{}] em {}: {}", 
                    requestId, code.getCode(), context, e.getMessage());
        }
        
        return buildErrorResponse(code, e.getMessage());
    }

    /**
     * Trata exceções de banco de dados.
     */
    private static String handleDatabaseException(SQLException e, String requestId, String context) {
        logger.error("[{}] Erro de banco de dados em {}: {}", requestId, context, e.getMessage(), e);
        return buildErrorResponse(ErrorCode.DATABASE_ERROR, "Erro ao acessar banco de dados");
    }

    /**
     * Constrói resposta de erro estruturada.
     */
    public static String buildErrorResponse(ErrorCode code, String message) {
        return String.format(
            "{\"status\": false, \"codigo\": \"%s\", \"mensagem\": \"%s\", \"detalhes\": \"%s\"}",
            code.getCode(),
            code.getMessage(),
            sanitizeForJson(message)
        );
    }

    /**
     * Sanitiza mensagem para JSON, convertendo quebras de linha em escapes.
     */
    private static String sanitizeForJson(String message) {
        if (message == null) return "";
        return message
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r");
    }

    /**
     * Sanitiza mensagem para logs, mantendo quebras de linha mas removendo caracteres problemáticos.
     */
    private static String sanitizeForLog(String message) {
        if (message == null) return "";
        return message
            .replace("\r\n", " | ")  // Windows line endings
            .replace("\n", " | ")    // Unix line endings  
            .replace("\r", " | ")    // Mac line endings
            .replace("\t", " ");     // Tabs
    }

    /**
     * Log de erro com stack trace completo (apenas em DEBUG).
     */
    public static void logDetailedError(String context, Exception e) {
        String requestId = MDC.get("requestId");
        logger.debug("[{}] Erro detalhado em {}", requestId, context, e);
    }

    /**
     * Log de aviso sem exposição de stack trace.
     */
    public static void logWarning(String context, String message) {
        String requestId = MDC.get("requestId");
        logger.warn("[{}] Aviso em {}: {}", requestId, context, message);
    }

    /**
     * Log de informação.
     */
    public static void logInfo(String context, String message) {
        String requestId = MDC.get("requestId");
        logger.info("[{}] {} - {}", requestId, context, message);
    }
}
