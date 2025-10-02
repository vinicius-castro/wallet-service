package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.shared.exception.BalanceNotFoundException;
import com.vicastro.walletservice.shared.exception.InvalidDateException;
import com.vicastro.walletservice.shared.exception.WalletNotFoundException;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.Objects;

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

    public Long execute(String walletId, OffsetDateTime date) {
        var dateToCompare = OffsetDateTime.of(LocalDate.now(), LocalTime.MIDNIGHT, OffsetDateTime.now().getOffset())
                .plusDays(1);
        if (date == null || date.isAfter(dateToCompare)) {
            throw new InvalidDateException();
        }

        if (!walletRepository.existsById(walletId)) {
            throw new WalletNotFoundException();
        }

        var balance = transactionRepository.getBalanceByDate(walletId, date);
        if (Objects.isNull(balance)) {
            throw new BalanceNotFoundException();
        }
        return balance;
    }
}
