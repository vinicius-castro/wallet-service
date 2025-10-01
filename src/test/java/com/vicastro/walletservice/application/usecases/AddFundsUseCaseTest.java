package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.shared.exception.InvalidAmountException;
import com.vicastro.walletservice.shared.exception.WalletNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;


class AddFundsUseCaseTest {

    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;
    private AddFundsUseCase useCase;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        useCase = new AddFundsUseCase(walletRepository, transactionRepository);
    }

    @Test
    void shouldAddFundsSuccessfully() {
        var walletId = "wallet-1";
        var amount = 100L;

        when(walletRepository.existsById(walletId)).thenReturn(true);

        useCase.execute(walletId, amount);

        verify(walletRepository).existsById(walletId);
        verify(transactionRepository).addFunds(walletId, amount);
    }

    @Test
    void shouldThrowExceptionWhenAmountIsZero() {
        var walletId = "wallet-1";
        var amount = 0L;

        assertThrows(InvalidAmountException.class, () -> useCase.execute(walletId, amount));
        verifyNoInteractions(walletRepository, transactionRepository);
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        var walletId = "wallet-1";
        var amount = -50L;

        assertThrows(InvalidAmountException.class, () -> useCase.execute(walletId, amount));
        verifyNoInteractions(walletRepository, transactionRepository);
    }

    @Test
    void shouldThrowExceptionWhenWalletNotFound() {
        var walletId = "wallet-2";
        var amount = 100L;

        when(walletRepository.existsById(walletId)).thenReturn(false);

        assertThrows(WalletNotFoundException.class, () -> useCase.execute(walletId, amount));

        verify(walletRepository).existsById(walletId);
        verifyNoMoreInteractions(walletRepository);
        verifyNoInteractions(transactionRepository);
    }
}