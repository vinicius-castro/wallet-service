package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.shared.exception.WalletNotFoundException;

public class GetWalletBalanceUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public GetWalletBalanceUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public Long execute(String walletId) {
        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException();
        }

        return transactionRepository.getBalance(walletId);
    }
}
