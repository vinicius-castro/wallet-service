package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.domain.Transaction;
import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.domain.enums.Origin;
import com.vicastro.walletservice.shared.exception.InvalidAmountException;
import com.vicastro.walletservice.shared.exception.WalletNotFoundException;

import java.util.UUID;

public class AddFundsUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public AddFundsUseCase(WalletRepository walletRepository,
                           TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public void execute(String walletId, Long amountInCents) {
        if (amountInCents <= 0) {
            throw new InvalidAmountException();
        }

        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException();
        }

        transactionRepository.addTransaction(
                Transaction.builder()
                        .id(UUID.randomUUID().toString())
                        .walletId(walletId)
                        .amount(amountInCents)
                        .operation(Operation.CREDIT)
                        .origin(Origin.DEPOSIT)
                        .build()
        );
    }

}
