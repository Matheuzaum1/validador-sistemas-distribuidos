package com.distribuidos.common;

import org.slf4j.MDC;
import java.util.UUID;

/**
 * Gerencia o contexto de correlação MDC (Mapped Diagnostic Context).
 * Facilita rastreamento de requisições através dos logs.
 */
public class MDCManager {
    private static final String REQUEST_ID_KEY = "requestId";
    private static final String USER_ID_KEY = "userId";
    private static final String SESSION_KEY = "sessionId";

    /**
     * Inicializa um novo request ID ou usa o fornecido.
     */
    public static String initializeRequestId() {
        String requestId = UUID.randomUUID().toString();
        MDC.put(REQUEST_ID_KEY, requestId);
        return requestId;
    }

    /**
     * Define um user ID no contexto.
     */
    public static void setUserId(String userId) {
        if (userId != null) {
            MDC.put(USER_ID_KEY, userId);
        }
    }

    /**
     * Define um session ID no contexto.
     */
    public static void setSessionId(String sessionId) {
        if (sessionId != null) {
            MDC.put(SESSION_KEY, sessionId);
        }
    }

    /**
     * Obtém o request ID atual.
     */
    public static String getRequestId() {
        return MDC.get(REQUEST_ID_KEY);
    }

    /**
     * Obtém o user ID atual.
     */
    public static String getUserId() {
        return MDC.get(USER_ID_KEY);
    }

    /**
     * Limpa todo o contexto MDC.
     */
    public static void clear() {
        MDC.clear();
    }

    /**
     * Imprime o estado atual do MDC para debug.
     */
    public static String getContextSummary() {
        return String.format("RequestId=%s, UserId=%s, SessionId=%s",
            MDC.get(REQUEST_ID_KEY),
            MDC.get(USER_ID_KEY),
            MDC.get(SESSION_KEY));
    }
}
