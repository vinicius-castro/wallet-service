package com.vicastro.walletservice.infra.repository;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.domain.WalletBalance;
import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.domain.enums.Origin;
import com.vicastro.walletservice.infra.repository.cache.redis.WalletBalanceRedisRepository;
import com.vicastro.walletservice.infra.repository.jpa.TransactionJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.WalletBalanceJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.entity.TransactionEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;

@Component
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository transactionJpaRepository;
    private final WalletBalanceRedisRepository walletBalanceRedisRepository;
    private final WalletBalanceJpaRepository walletBalanceJpaRepository;

    public TransactionRepositoryImpl(TransactionJpaRepository transactionJpaRepository,
                                     WalletBalanceRedisRepository walletBalanceRedisRepository,
                                     WalletBalanceJpaRepository walletBalanceJpaRepository) {
        this.transactionJpaRepository = transactionJpaRepository;
        this.walletBalanceRedisRepository = walletBalanceRedisRepository;
        this.walletBalanceJpaRepository = walletBalanceJpaRepository;
    }

    @Override
    @Transactional
    public void addFunds(String walletId, Long amount) {
        transactionJpaRepository.save(new TransactionEntity(walletId, amount, Operation.CREDIT.name(), Origin.DEPOSIT.name()));

        var walletBalance = walletBalanceRedisRepository.get(walletId);
        saveWalletBalanceInCache(walletId,
                walletBalance.map(balance -> balance.balance() + amount)
                        .orElse(amount)
        );
    }

    @Override
    public Long getBalance(String walletId) {
        var walletBalance = walletBalanceRedisRepository.get(walletId);
        if (walletBalance.isPresent()) return walletBalance.get().balance();

        var balance = calculateBalanceUsingWalletBalanceAndRecentTransactions(walletId);
        if (balance != null) return balance;

        return calculateBalanceFromAllTransactions(walletId);
    }

    @Override
    public Long getBalanceByDate(String walletId, OffsetDateTime date) {
        if (date.toLocalDate().isEqual(OffsetDateTime.now().toLocalDate())) {
            return getBalance(walletId);
        }
        return walletBalanceJpaRepository.findLastBalanceBeforeOrEqual(walletId, date).orElse(null);
    }

    private Long calculateBalanceUsingWalletBalanceAndRecentTransactions(String walletId) {
        var lastWalletBalance = walletBalanceJpaRepository.findTopByWalletIdOrderByReferenceDateDesc(walletId);
        if (lastWalletBalance.isEmpty()) return null;

        var recentTransactionsBalance = transactionJpaRepository.calculateBalanceByWalletAndDate(
                walletId,
                lastWalletBalance.get().getReferenceDate()
        );
        if (recentTransactionsBalance != null) {
            var balance = lastWalletBalance.get().getBalance() + recentTransactionsBalance;
            saveWalletBalanceInCache(walletId, balance);
            return balance;
        }
        saveWalletBalanceInCache(walletId, lastWalletBalance.get().getBalance());
        return lastWalletBalance.get().getBalance();
    }

    private Long calculateBalanceFromAllTransactions(String walletId) {
        var allTransactionsBalance = transactionJpaRepository.calculateBalanceByWallet(walletId)
                .orElse(0L);
        walletBalanceRedisRepository.save(walletId, new WalletBalance(walletId, allTransactionsBalance));
        return allTransactionsBalance;
    }

    private void saveWalletBalanceInCache(String walletId, long balance) {
        walletBalanceRedisRepository.save(walletId,
                WalletBalance
                        .builder()
                        .walletId(walletId)
                        .balance(balance)
                        .build());
    }
}
