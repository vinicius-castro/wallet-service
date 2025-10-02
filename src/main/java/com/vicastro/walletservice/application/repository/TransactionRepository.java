package com.vicastro.walletservice.application.repository;

import java.time.OffsetDateTime;

public interface TransactionRepository {
    void addFunds(String walletId, Long amountInCents);

    Long getBalance(String walletId);

    Long getBalanceByDate(String walletId, OffsetDateTime date);

    void withdrawFunds(String walletId, Long amountInCents);
}
