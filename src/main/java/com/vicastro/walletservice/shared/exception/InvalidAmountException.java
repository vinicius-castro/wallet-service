package com.vicastro.walletservice.shared.exception;

public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException() {
        super("Invalid amount");
    }

    public InvalidAmountException(String message) {
        super(message);
    }
}
