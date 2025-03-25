package ru.kolpakovee.finance_service.exceptions;

public class DebtPaymentException extends RuntimeException {

    public DebtPaymentException(String message) {
        super(message);
    }

    public DebtPaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}
