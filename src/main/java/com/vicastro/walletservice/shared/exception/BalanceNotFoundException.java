package com.vicastro.walletservice.shared.exception;

public class BalanceNotFoundException extends RuntimeException {
    public BalanceNotFoundException() {
        super("Invalid balance for the given date");
    }
}
