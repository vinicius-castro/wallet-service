package com.vicastro.walletservice.infra.repository;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.domain.enums.Origin;
import com.vicastro.walletservice.infra.repository.jpa.TransactionJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.entity.TransactionEntity;
import org.springframework.stereotype.Component;

@Component
public class TransactionRepositoryImpl implements TransactionRepository {

    private final TransactionJpaRepository transactionJpaRepository;

    public TransactionRepositoryImpl(TransactionJpaRepository transactionJpaRepository) {
        this.transactionJpaRepository = transactionJpaRepository;
    }

    @Override
    public void addFunds(String walletId, Long amount) {
        transactionJpaRepository.save(new TransactionEntity(walletId, amount, Operation.CREDIT.name(), Origin.DEPOSIT.name()));
    }
}
