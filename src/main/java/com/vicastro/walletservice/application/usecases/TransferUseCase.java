package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.domain.Transaction;
import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.domain.enums.Origin;
import com.vicastro.walletservice.shared.exception.InvalidAmountException;
import com.vicastro.walletservice.shared.exception.WalletNotFoundException;

import java.util.UUID;

public class TransferUseCase {

    private final WalletRepository walletRepository;
    private final TransactionRepository transactionRepository;

    public TransferUseCase(WalletRepository walletRepository, TransactionRepository transactionRepository) {
        this.walletRepository = walletRepository;
        this.transactionRepository = transactionRepository;
    }

    public void execute(String fromWalletId, String toWalletId, Long amountInCents) {
        validateTransfer(fromWalletId, toWalletId, amountInCents);

        transactionRepository.addTransferTransaction(
                Transaction.builder()
                        .id(UUID.randomUUID().toString())
                        .walletId(fromWalletId)
                        .walletIdRelated(toWalletId)
                        .amount(amountInCents)
                        .operation(Operation.DEBIT)
                        .origin(Origin.TRANSFER)
                        .build(),
                Transaction.builder()
                        .id(UUID.randomUUID().toString())
                        .walletId(toWalletId)
                        .walletIdRelated(fromWalletId)
                        .amount(amountInCents)
                        .operation(Operation.CREDIT)
                        .origin(Origin.TRANSFER)
                        .build()
        );
    }

    private void validateTransfer(String fromWalletId, String toWalletId, Long amountInCents) {
        validateAmount(amountInCents);
        validateSourceWallet(fromWalletId);
        validateDestinationWallet(toWalletId);
        validateBalance(fromWalletId, amountInCents);
    }

    private void validateBalance(String fromWalletId, Long amountInCents) {
        if (transactionRepository.getBalance(fromWalletId) < amountInCents) {
            throw new InvalidAmountException("Insufficient funds");
        }
    }

    private void validateDestinationWallet(String toWalletId) {
        if (!walletRepository.existsById(toWalletId)) {
            throw new WalletNotFoundException("Destination wallet not found");
        }
    }

    private void validateSourceWallet(String fromWalletId) {
        if (!walletRepository.existsById(fromWalletId)) {
            throw new WalletNotFoundException("Source wallet not found");
        }
    }

    private static void validateAmount(Long amountInCents) {
        if (amountInCents <= 0) {
            throw new InvalidAmountException("Amount must be greater than zero");
        }
    }
}
