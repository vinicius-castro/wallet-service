package com.vicastro.walletservice.adapter;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletBalanceRepository;
import com.vicastro.walletservice.application.usecases.DailySettlementUseCase;
import com.vicastro.walletservice.domain.WalletBalance;

import java.time.OffsetDateTime;

public class WalletBalanceController {

    private final WalletBalanceRepository walletBalanceRepository;
    private final TransactionRepository transactionRepository;

    public WalletBalanceController(WalletBalanceRepository walletBalanceRepository, TransactionRepository transactionRepository) {
        this.walletBalanceRepository = walletBalanceRepository;
        this.transactionRepository = transactionRepository;
    }

    public WalletBalance execute(String walletId, OffsetDateTime startDate, OffsetDateTime endDate) {
        var useCase = new DailySettlementUseCase(walletBalanceRepository, transactionRepository);
        return useCase.execute(walletId, startDate, endDate);
    }
}
