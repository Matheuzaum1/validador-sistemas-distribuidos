package com.distribuidos.exception;

/**
 * Exceção lançada quando o saldo é insuficiente para operação.
 */
public class InsufficientBalanceException extends ApplicationException {
    private final double required;
    private final double available;

    public InsufficientBalanceException(double required, double available) {
        super(ErrorCode.INSUFFICIENT_BALANCE, 
            String.format("Saldo insuficiente. Requerido: %.2f, Disponível: %.2f", required, available));
        this.required = required;
        this.available = available;
    }

    public double getRequired() {
        return required;
    }

    public double getAvailable() {
        return available;
    }
}
