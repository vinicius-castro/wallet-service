package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.domain.Transaction;
import com.vicastro.walletservice.shared.exception.InvalidAmountException;
import com.vicastro.walletservice.shared.exception.WalletNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class TransferUseCaseTest {

    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;
    private TransferUseCase useCase;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        useCase = new TransferUseCase(walletRepository, transactionRepository);
    }

    @Test
    void shouldThrowExceptionWhenAmountIsZero() {
        assertThrows(InvalidAmountException.class, () -> useCase.execute("from", "to", 0L));
        verifyNoInteractions(walletRepository, transactionRepository);
    }

    @Test
    void shouldThrowExceptionWhenAmountIsNegative() {
        assertThrows(InvalidAmountException.class, () -> useCase.execute("from", "to", -100L));
        verifyNoInteractions(walletRepository, transactionRepository);
    }

    @Test
    void shouldThrowExceptionWhenFromWalletNotFound() {
        when(walletRepository.existsById("from")).thenReturn(false);
        when(walletRepository.existsById("to")).thenReturn(true);

        assertThrows(WalletNotFoundException.class, () -> useCase.execute("from", "to", 100L));

        verify(walletRepository).existsById("from");
        verify(walletRepository, never()).existsById("to");
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldThrowExceptionWhenToWalletNotFound() {
        when(walletRepository.existsById("from")).thenReturn(true);
        when(walletRepository.existsById("to")).thenReturn(false);

        assertThrows(WalletNotFoundException.class, () -> useCase.execute("from", "to", 100L));

        verify(walletRepository).existsById("from");
        verify(walletRepository).existsById("to");
        verifyNoInteractions(transactionRepository);
    }

    @Test
    void shouldThrowExceptionWhenInsufficientFunds() {
        when(walletRepository.existsById("from")).thenReturn(true);
        when(walletRepository.existsById("to")).thenReturn(true);
        when(transactionRepository.getBalance("from")).thenReturn(50L);

        assertThrows(InvalidAmountException.class, () -> useCase.execute("from", "to", 100L));

        verify(walletRepository).existsById("from");
        verify(walletRepository).existsById("to");
        verify(transactionRepository).getBalance("from");
        verifyNoMoreInteractions(transactionRepository);
    }

    @Test
    void shouldTransferSuccessfully() {
        when(walletRepository.existsById("from")).thenReturn(true);
        when(walletRepository.existsById("to")).thenReturn(true);
        when(transactionRepository.getBalance("from")).thenReturn(200L);

        useCase.execute("from", "to", 100L);

        verify(walletRepository).existsById("from");
        verify(walletRepository).existsById("to");
        verify(transactionRepository).getBalance("from");
        verify(transactionRepository).addTransferTransaction(any(Transaction.class), any(Transaction.class));
    }
}