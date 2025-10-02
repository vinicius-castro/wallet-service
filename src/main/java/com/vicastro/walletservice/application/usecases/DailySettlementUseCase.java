package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletBalanceRepository;
import com.vicastro.walletservice.domain.WalletBalance;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;

public class DailySettlementUseCase {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final WalletBalanceRepository balanceRepository;
    private final TransactionRepository transactionRepository;

    public DailySettlementUseCase(WalletBalanceRepository balanceRepository, TransactionRepository transactionRepository) {
        this.balanceRepository = balanceRepository;
        this.transactionRepository = transactionRepository;
    }

    public WalletBalance execute(String walletId, OffsetDateTime startDate, OffsetDateTime endDate) {
        if (balanceRepository.existsByWalletIdAndReferenceDate(walletId, startDate).orElse(false)) {
            logger.info("Daily settlement for wallet {} on {} already exists. Skipping.",
                    walletId, startDate.toLocalDate());
            return null;
        }

        var previousWalletBalance = balanceRepository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, startDate);
        if (previousWalletBalance.isPresent() && !previousWalletBalance.get().referenceDate().toLocalDate()
                .isEqual(startDate.toLocalDate().minusDays(1))) {
            logger.warn("Previous balance for wallet {} is not from the day before {}. Skipping daily settlement.",
                    walletId, startDate.toLocalDate());
            return null;
        }

        var dailyBalance = 0L;
        if (previousWalletBalance.isEmpty()) {
            dailyBalance = transactionRepository.calculateBalanceByWalletLessThanEndDate(walletId, endDate)
                    .orElse(0L);
        } else {
            dailyBalance = transactionRepository.calculateBalanceByWalletIdAndDateRange(
                    walletId,
                    startDate,
                    endDate
            ).orElse(0L);
        }

        var previousBalanceValue = previousWalletBalance.map(WalletBalance::balance).orElse(0L);
        var finalBalance = previousBalanceValue + dailyBalance;
        var walletBalance = WalletBalance.builder()
                .walletId(walletId)
                .balance(finalBalance)
                .referenceDate(startDate)
                .build();
        logger.info("Processed daily settlement for wallet {}: previous balance = {}, daily balance = {}, " +
                        "final balance = {}",
                walletId,
                previousBalanceValue,
                dailyBalance,
                finalBalance);
        return walletBalance;
    }
}
