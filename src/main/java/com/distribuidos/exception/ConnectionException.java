package com.distribuidos.exception;

/**
 * Exceção lançada quando não há conexão com o servidor.
 */
public class ConnectionException extends ApplicationException {
    private final String host;
    private final int port;
    private final int retryCount;

    public ConnectionException(String host, int port, Throwable cause) {
        super(ErrorCode.CONNECTION_ERROR, 
            String.format("Falha ao conectar em %s:%d", host, port), cause);
        this.host = host;
        this.port = port;
        this.retryCount = 0;
    }

    public ConnectionException(String host, int port, int retryCount, Throwable cause) {
        super(ErrorCode.CONNECTION_ERROR, 
            String.format("Falha ao conectar em %s:%d após %d tentativas", host, port, retryCount), cause);
        this.host = host;
        this.port = port;
        this.retryCount = retryCount;
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    public int getRetryCount() {
        return retryCount;
    }
}
