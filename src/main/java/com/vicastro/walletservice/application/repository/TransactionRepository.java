package com.vicastro.walletservice.application.repository;

import com.vicastro.walletservice.domain.Transaction;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface TransactionRepository {
    void addTransaction(Transaction transaction);

    Long getBalance(String walletId);

    Long getBalanceByDate(String walletId, OffsetDateTime date);

    void addTransferTransaction(Transaction from, Transaction to);

    Optional<Long> calculateBalanceByWalletLessThanEndDate(String walletId, OffsetDateTime endDate);

    Optional<Long> calculateBalanceByWalletIdAndDateRange(
            String walletId,
            OffsetDateTime startDate,
            OffsetDateTime endDate
    );
}
