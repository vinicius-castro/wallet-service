package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.shared.exception.InvalidAmountException;
import com.vicastro.walletservice.shared.exception.WalletNotFoundException;

public class AddFundsUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public AddFundsUseCase(WalletRepository walletRepository,
                           TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public void execute(String walletId, Long amount) {
        if (amount <= 0) {
            throw new InvalidAmountException();
        }

        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException();
        }

        transactionRepository.addFunds(walletId, amount);
    }

}
