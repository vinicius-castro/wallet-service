package com.vicastro.walletservice.infra.repository;

import com.vicastro.walletservice.domain.Transaction;
import com.vicastro.walletservice.domain.WalletBalance;
import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.domain.enums.Origin;
import com.vicastro.walletservice.infra.repository.cache.redis.WalletBalanceRedisRepository;
import com.vicastro.walletservice.infra.repository.jpa.TransactionJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.WalletBalanceJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.entity.TransactionEntity;
import com.vicastro.walletservice.infra.repository.jpa.entity.WalletBalanceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TransactionRepositoryImplTest {

    private TransactionJpaRepository transactionJpaRepository;
    private WalletBalanceRedisRepository walletBalanceRedisRepository;
    private WalletBalanceJpaRepository walletBalanceJpaRepository;
    private TransactionRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        transactionJpaRepository = mock(TransactionJpaRepository.class);
        walletBalanceRedisRepository = mock(WalletBalanceRedisRepository.class);
        walletBalanceJpaRepository = mock(WalletBalanceJpaRepository.class);
        repository = new TransactionRepositoryImpl(transactionJpaRepository, walletBalanceRedisRepository, walletBalanceJpaRepository);
    }

    @Test
    void shouldSaveCreditTransactionAndUpdateCacheWhenBalancePresent() {
        var walletId = "wallet-10";
        var amount = 100L;

        var transaction = Transaction.builder()
                .walletId(walletId)
                .amount(amount)
                .operation(Operation.CREDIT)
                .origin(Origin.DEPOSIT)
                .build();

        WalletBalance existingBalance = new WalletBalance(walletId, 200L, OffsetDateTime.now().minusDays(1));
        when(walletBalanceRedisRepository.get(walletId)).thenReturn(Optional.of(existingBalance));

        repository.addTransaction(transaction);

        ArgumentCaptor<TransactionEntity> txCaptor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(transactionJpaRepository).save(txCaptor.capture());
        TransactionEntity entity = txCaptor.getValue();
        assertEquals(walletId, entity.getWalletId());
        assertEquals(amount, entity.getAmount());
        assertEquals(Operation.CREDIT.name(), entity.getOperation());
        assertEquals(Origin.DEPOSIT.name(), entity.getOrigin());

        verify(walletBalanceRedisRepository).save(eq(walletId), argThat(b -> b.balance() == 300L));
    }

    @Test
    void shouldSaveCreditTransactionAndCallGetBalanceWhenBalanceNotPresent() {
        var walletId = "wallet-11";
        var amount = 150L;

        var transaction = Transaction.builder()
                .walletId(walletId)
                .amount(amount)
                .operation(Operation.CREDIT)
                .origin(Origin.DEPOSIT)
                .build();

        when(walletBalanceRedisRepository.get(walletId)).thenReturn(Optional.empty());
        TransactionRepositoryImpl spyRepo = spy(repository);
        doReturn(999L).when(spyRepo).getBalance(walletId);

        spyRepo.addTransaction(transaction);

        ArgumentCaptor<TransactionEntity> txCaptor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(transactionJpaRepository).save(txCaptor.capture());
        TransactionEntity entity = txCaptor.getValue();
        assertEquals(walletId, entity.getWalletId());
        assertEquals(amount, entity.getAmount());
        assertEquals(Operation.CREDIT.name(), entity.getOperation());
        assertEquals(Origin.DEPOSIT.name(), entity.getOrigin());

        verify(spyRepo).getBalance(walletId);
        verify(walletBalanceRedisRepository, never()).save(eq(walletId), any());
    }

    @Test
    void shouldReturnBalanceFromCacheIfPresent() {
        String walletId = "wallet-3";
        when(walletBalanceRedisRepository.get(walletId)).thenReturn(Optional.of(new WalletBalance(walletId, 300L, OffsetDateTime.now())));

        Long balance = repository.getBalance(walletId);

        assertEquals(300L, balance);
        verifyNoInteractions(walletBalanceJpaRepository);
        verifyNoMoreInteractions(transactionJpaRepository);
    }

    @Test
    void shouldCalculateBalanceFromWalletBalanceAndRecentTransactions() {
        String walletId = "wallet-4";
        var walletBalanceEntity = mock(com.vicastro.walletservice.infra.repository.jpa.entity.WalletBalanceEntity.class);
        when(walletBalanceEntity.getReferenceDate()).thenReturn(OffsetDateTime.now());
        when(walletBalanceEntity.getBalance()).thenReturn(400L);
        when(walletBalanceRedisRepository.get(walletId)).thenReturn(Optional.empty());
        when(walletBalanceJpaRepository.findTopByWalletIdOrderByReferenceDateDesc(walletId)).thenReturn(Optional.of(walletBalanceEntity));
        when(transactionJpaRepository.calculateBalanceByWalletAndGreaterThanStartDate(eq(walletId), any())).thenReturn(50L);

        Long balance = repository.getBalance(walletId);

        assertEquals(450L, balance);
        verify(walletBalanceRedisRepository).save(eq(walletId), argThat(b -> b.balance() == 450L));
    }

    @Test
    void shouldReturnWalletBalanceIfNoRecentTransactions() {
        String walletId = "wallet-5";
        var walletBalanceEntity = mock(com.vicastro.walletservice.infra.repository.jpa.entity.WalletBalanceEntity.class);
        when(walletBalanceEntity.getReferenceDate()).thenReturn(OffsetDateTime.now());
        when(walletBalanceEntity.getBalance()).thenReturn(500L);
        when(walletBalanceRedisRepository.get(walletId)).thenReturn(Optional.empty());
        when(walletBalanceJpaRepository.findTopByWalletIdOrderByReferenceDateDesc(walletId)).thenReturn(Optional.of(walletBalanceEntity));
        when(transactionJpaRepository.calculateBalanceByWalletAndGreaterThanStartDate(eq(walletId), any())).thenReturn(null);

        Long balance = repository.getBalance(walletId);

        assertEquals(500L, balance);
        verify(walletBalanceRedisRepository).save(eq(walletId), argThat(b -> b.balance() == 500L));
    }

    @Test
    void shouldCalculateBalanceFromAllTransactionsIfNoWalletBalance() {
        String walletId = "wallet-6";
        when(walletBalanceRedisRepository.get(walletId)).thenReturn(Optional.empty());
        when(walletBalanceJpaRepository.findTopByWalletIdOrderByReferenceDateDesc(walletId)).thenReturn(Optional.empty());
        when(transactionJpaRepository.calculateBalanceByWallet(walletId)).thenReturn(Optional.of(600L));

        Long balance = repository.getBalance(walletId);

        assertEquals(600L, balance);
        verify(walletBalanceRedisRepository).save(eq(walletId), argThat(b -> b.balance() == 600L));
    }

    @Test
    void shouldReturnZeroIfNoTransactions() {
        String walletId = "wallet-7";
        when(walletBalanceRedisRepository.get(walletId)).thenReturn(Optional.empty());
        when(walletBalanceJpaRepository.findTopByWalletIdOrderByReferenceDateDesc(walletId)).thenReturn(Optional.empty());
        when(transactionJpaRepository.calculateBalanceByWallet(walletId)).thenReturn(Optional.empty());

        Long balance = repository.getBalance(walletId);

        assertEquals(0L, balance);
        verify(walletBalanceRedisRepository).save(eq(walletId), argThat(b -> b.balance() == 0L));
    }

    @Test
    void shouldReturnCurrentBalanceWhenDateIsToday() {
        String walletId = "wallet-1";
        OffsetDateTime today = OffsetDateTime.now();
        TransactionRepositoryImpl spyRepo = spy(repository);
        doReturn(123L).when(spyRepo).getBalance(walletId);

        Long result = spyRepo.getBalanceByDate(walletId, today);

        assertEquals(123L, result);
        verify(spyRepo).getBalance(walletId);
    }

    @Test
    void shouldReturnBalanceForPastDateIfPresent() {
        String walletId = "wallet-2";
        OffsetDateTime pastDate = OffsetDateTime.now().minusDays(1);
        when(walletBalanceJpaRepository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, pastDate))
                .thenReturn(Optional.of(new WalletBalanceEntity(walletId, 456L, pastDate)));

        Long result = repository.getBalanceByDate(walletId, pastDate);

        assertEquals(456L, result);
        verify(walletBalanceJpaRepository).findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, pastDate);
    }

    @Test
    void shouldReturnNullIfNoBalanceForPastDate() {
        String walletId = "wallet-3";
        OffsetDateTime pastDate = OffsetDateTime.now().minusDays(2);
        when(walletBalanceJpaRepository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, pastDate)).thenReturn(Optional.empty());

        Long result = repository.getBalanceByDate(walletId, pastDate);

        assertNull(result);
        verify(walletBalanceJpaRepository).findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, pastDate);
    }

    @Test
    void shouldReturnBalanceByWalletLessThanEndDate() {
        String walletId = "wallet-abc";
        OffsetDateTime endDate = OffsetDateTime.now();
        Optional<Long> expected = Optional.of(123L);

        when(transactionJpaRepository.calculateBalanceByWalletLessThanEndDate(walletId, endDate)).thenReturn(expected);

        Optional<Long> result = repository.calculateBalanceByWalletLessThanEndDate(walletId, endDate);

        assertEquals(expected, result);
        verify(transactionJpaRepository).calculateBalanceByWalletLessThanEndDate(walletId, endDate);
    }

    @Test
    void shouldReturnBalanceByWalletIdAndDateRange() {
        String walletId = "wallet-def";
        OffsetDateTime startDate = OffsetDateTime.now().minusDays(2);
        OffsetDateTime endDate = OffsetDateTime.now();
        Optional<Long> expected = Optional.of(456L);

        when(transactionJpaRepository.calculateBalanceByWalletIdAndDateRange(walletId, startDate, endDate)).thenReturn(expected);

        Optional<Long> result = repository.calculateBalanceByWalletIdAndDateRange(walletId, startDate, endDate);

        assertEquals(expected, result);
        verify(transactionJpaRepository).calculateBalanceByWalletIdAndDateRange(walletId, startDate, endDate);
    }
}
