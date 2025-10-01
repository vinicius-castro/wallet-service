package com.vicastro.walletservice.infra.repository;

import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.domain.enums.Origin;
import com.vicastro.walletservice.infra.repository.jpa.TransactionJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.entity.TransactionEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

class TransactionRepositoryImplTest {

    private TransactionJpaRepository transactionJpaRepository;
    private TransactionRepositoryImpl transactionRepository;

    @BeforeEach
    void setUp() {
        transactionJpaRepository = mock(TransactionJpaRepository.class);
        transactionRepository = new TransactionRepositoryImpl(transactionJpaRepository);
    }

    @Test
    void shouldSaveTransactionEntityWithCorrectValuesWhenAddFunds() {
        var walletId = "wallet-1";
        var amount = 100L;

        transactionRepository.addFunds(walletId, amount);

        ArgumentCaptor<TransactionEntity> captor = ArgumentCaptor.forClass(TransactionEntity.class);
        verify(transactionJpaRepository).save(captor.capture());

        TransactionEntity savedEntity = captor.getValue();
        assertEquals(walletId, savedEntity.getWalletId());
        assertEquals(amount, savedEntity.getAmount());
        assertEquals(Operation.CREDIT.name(), savedEntity.getOperation());
        assertEquals(Origin.DEPOSIT.name(), savedEntity.getOrigin());
    }
}
