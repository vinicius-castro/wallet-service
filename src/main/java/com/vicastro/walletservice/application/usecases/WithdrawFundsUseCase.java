package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.domain.Transaction;
import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.domain.enums.Origin;
import com.vicastro.walletservice.shared.exception.InvalidAmountException;
import com.vicastro.walletservice.shared.exception.WalletNotFoundException;

import java.util.UUID;

public class WithdrawFundsUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WithdrawFundsUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public void execute(String walletId, Long amountInCents) {
        validateWithdraw(walletId, amountInCents);

        transactionRepository.addTransaction(
                Transaction.builder()
                        .id(UUID.randomUUID().toString())
                        .walletId(walletId)
                        .amount(amountInCents)
                        .operation(Operation.DEBIT)
                        .origin(Origin.WITHDRAW)
                        .build()
        );
    }

    private void validateWithdraw(String walletId, Long amountInCents) {
        validateAmount(amountInCents);
        validateWallet(walletId);
        validateBalance(walletId, amountInCents);
    }

    private void validateBalance(String walletId, Long amountInCents) {
        if (transactionRepository.getBalance(walletId) < amountInCents) {
            throw new InvalidAmountException("Insufficient funds");
        }
    }

    private void validateWallet(String walletId) {
        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException();
        }
    }

    private static void validateAmount(Long amountInCents) {
        if (amountInCents <= 0) {
            throw new InvalidAmountException();
        }
    }
}
