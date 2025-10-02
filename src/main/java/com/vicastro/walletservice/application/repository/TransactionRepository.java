package com.vicastro.walletservice.application.repository;

import java.time.OffsetDateTime;

public interface TransactionRepository {
    void addFunds(String walletId, Long amount);

    Long getBalance(String walletId);

    Long getBalanceByDate(String walletId, OffsetDateTime date);
}
