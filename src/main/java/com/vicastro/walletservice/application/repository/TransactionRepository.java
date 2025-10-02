package com.vicastro.walletservice.application.repository;

import com.vicastro.walletservice.domain.Transaction;

import java.time.OffsetDateTime;

public interface TransactionRepository {
    void addTransaction(Transaction transaction);

    Long getBalance(String walletId);

    Long getBalanceByDate(String walletId, OffsetDateTime date);
}
