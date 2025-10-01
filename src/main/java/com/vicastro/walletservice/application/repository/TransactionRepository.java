package com.vicastro.walletservice.application.repository;

public interface TransactionRepository {
    void addFunds(String walletId, Long amount);

    Long getBalance(String walletId);
}
