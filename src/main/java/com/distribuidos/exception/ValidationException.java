package com.distribuidos.exception;

/**
 * Exceção lançada quando uma validação falha.
 */
public class ValidationException extends ApplicationException {
    public ValidationException(String message) {
        super(ErrorCode.VALIDATION_FAILED, message);
    }

    public ValidationException(String message, Throwable cause) {
        super(ErrorCode.VALIDATION_FAILED, message, cause);
    }
}
