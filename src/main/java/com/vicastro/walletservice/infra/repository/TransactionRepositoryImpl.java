package com.vicastro.walletservice.infra.repository;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.domain.Transaction;
import com.vicastro.walletservice.domain.WalletBalance;
import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.infra.repository.cache.redis.WalletBalanceRedisRepository;
import com.vicastro.walletservice.infra.repository.jpa.TransactionJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.WalletBalanceJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.entity.TransactionEntity;
import com.vicastro.walletservice.infra.repository.jpa.entity.WalletBalanceEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;

@Component
@Transactional
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
    public void addTransaction(Transaction transaction) {
        transactionJpaRepository.save(new TransactionEntity(transaction));
        updateBalance(transaction);
    }

    @Override
    public void addTransferTransaction(Transaction from, Transaction to) {
        addTransaction(from);
        addTransaction(to);
    }

    @Override
    public Optional<Long> calculateBalanceByWalletLessThanEndDate(String walletId, OffsetDateTime endDate) {
        return transactionJpaRepository.calculateBalanceByWalletLessThanEndDate(walletId, endDate);
    }

    @Override
    public Optional<Long> calculateBalanceByWalletIdAndDateRange(String walletId, OffsetDateTime startDate, OffsetDateTime endDate) {
        return transactionJpaRepository.calculateBalanceByWalletIdAndDateRange(walletId, startDate, endDate);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getBalance(String walletId) {
        var walletBalance = walletBalanceRedisRepository.get(walletId);
        if (walletBalance.isPresent()) return walletBalance.get().balance();

        var balance = calculateBalanceUsingWalletBalanceAndRecentTransactions(walletId);
        if (balance != null) return balance;

        return calculateBalanceFromAllTransactions(walletId);
    }

    @Override
    @Transactional(readOnly = true)
    public Long getBalanceByDate(String walletId, OffsetDateTime date) {
        if (date.toLocalDate().isEqual(OffsetDateTime.now().toLocalDate())) {
            return getBalance(walletId);
        }
        var walletBalance = walletBalanceJpaRepository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, date);
        return walletBalance.map(WalletBalanceEntity::getBalance).orElse(null);
    }

    private Long calculateBalanceUsingWalletBalanceAndRecentTransactions(String walletId) {
        var lastWalletBalance = walletBalanceJpaRepository.findTopByWalletIdOrderByReferenceDateDesc(walletId);
        if (lastWalletBalance.isEmpty()) return null;

        var recentTransactionsBalance = transactionJpaRepository.calculateBalanceByWalletAndGreaterThanStartDate(
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
        saveWalletBalanceInCache(walletId, allTransactionsBalance);
        return allTransactionsBalance;
    }

    private void updateBalance(Transaction transaction) {
        var walletBalance = walletBalanceRedisRepository.get(transaction.walletId());
        if (walletBalance.isEmpty()) {
            getBalance(transaction.walletId());
            return;
        }

        var newBalance = 0L;
        if (transaction.operation() == Operation.CREDIT)
            newBalance = walletBalance.get().balance() + transaction.amount();
        else
            newBalance = walletBalance.get().balance() - transaction.amount();

        saveWalletBalanceInCache(transaction.walletId(), newBalance);
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
