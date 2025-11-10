package com.distribuidos.exception;

/**
 * Exceção lançada quando há erro de banco de dados.
 */
public class DatabaseException extends ApplicationException {
    public DatabaseException(ErrorCode code, String message) {
        super(code, message);
    }

    public DatabaseException(ErrorCode code, String message, Throwable cause) {
        super(code, message, cause);
    }

    public DatabaseException(String message, Throwable cause) {
        super(ErrorCode.DATABASE_ERROR, message, cause);
    }

    public DatabaseException(String message) {
        super(ErrorCode.DATABASE_ERROR, message);
    }
}
