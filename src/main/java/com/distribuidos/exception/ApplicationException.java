package com.distribuidos.exception;

/**
 * Exceção base para todas as exceções da aplicação.
 * Fornece uma hierarquia consistente de erros do domínio.
 */
public abstract class ApplicationException extends RuntimeException {
    private final ErrorCode errorCode;

    public ApplicationException(ErrorCode errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public ApplicationException(ErrorCode errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s: %s", 
            errorCode.getCode(), 
            errorCode.getMessage(), 
            getMessage());
    }
}
