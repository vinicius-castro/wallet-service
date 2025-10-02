package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.shared.exception.InvalidAmountException;
import com.vicastro.walletservice.shared.exception.WalletNotFoundException;

public class WithdrawFundsUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public WithdrawFundsUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
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

        if (transactionRepository.getBalance(walletId) < amountInCents) {
            throw new InvalidAmountException("Insufficient funds");
        }

        transactionRepository.withdrawFunds(walletId, amountInCents);
    }
}
