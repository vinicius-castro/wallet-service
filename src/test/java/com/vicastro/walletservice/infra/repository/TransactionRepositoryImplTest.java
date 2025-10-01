package com.vicastro.walletservice.infra.repository;

import com.vicastro.walletservice.domain.WalletBalance;
import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.domain.enums.Origin;
import com.vicastro.walletservice.infra.repository.cache.redis.WalletBalanceRedisRepository;
import com.vicastro.walletservice.infra.repository.jpa.TransactionJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.WalletBalanceJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.entity.TransactionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.mock;
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
    void shouldSaveTransactionAndUpdateCacheWhenAddingFunds() {
        String walletId = "wallet-1";
        Long amount = 100L;
        when(walletBalanceRedisRepository.get(walletId)).thenReturn(Optional.of(new WalletBalance(walletId, 50L)));

        repository.addFunds(walletId, amount);

        ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(transactionJpaRepository).save(captor.capture());
        TransactionEntity entity = captor.getValue();
        assertEquals(walletId, entity.getWalletId());
        assertEquals(amount, entity.getAmount());
        assertEquals(Operation.CREDIT.name(), entity.getOperation());
        assertEquals(Origin.DEPOSIT.name(), entity.getOrigin());

        verify(walletBalanceRedisRepository).save(eq(walletId), argThat(b -> b.balance() == 150L));
    }

    @Test
    void shouldSetInitialBalanceIfNotPresentWhenAddingFunds() {
        String walletId = "wallet-2";
        Long amount = 200L;
        when(walletBalanceRedisRepository.get(walletId)).thenReturn(Optional.empty());

        repository.addFunds(walletId, amount);

        verify(walletBalanceRedisRepository).save(eq(walletId), argThat(b -> b.balance() == 200L));
    }

    @Test
    void shouldReturnBalanceFromCacheIfPresent() {
        String walletId = "wallet-3";
        when(walletBalanceRedisRepository.get(walletId)).thenReturn(Optional.of(new WalletBalance(walletId, 300L)));

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
        when(transactionJpaRepository.calculateBalanceByWalletAndDate(eq(walletId), any())).thenReturn(50L);

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
        when(transactionJpaRepository.calculateBalanceByWalletAndDate(eq(walletId), any())).thenReturn(null);

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
}
