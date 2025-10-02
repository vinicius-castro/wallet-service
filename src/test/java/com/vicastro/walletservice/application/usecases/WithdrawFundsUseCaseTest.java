package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.domain.enums.Operation;
import com.vicastro.walletservice.domain.enums.Origin;
import com.vicastro.walletservice.shared.exception.InvalidAmountException;
import com.vicastro.walletservice.shared.exception.WalletNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class WithdrawFundsUseCaseTest {

    private WalletRepository walletRepository;
    private TransactionRepository transactionRepository;
    private WithdrawFundsUseCase useCase;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        useCase = new WithdrawFundsUseCase(walletRepository, transactionRepository);
    }

    @Test
    void shouldThrowInvalidAmountExceptionWhenAmountIsZeroOrNegative() {
        assertThrows(InvalidAmountException.class, () -> useCase.execute("wallet-1", 0L));
        assertThrows(InvalidAmountException.class, () -> useCase.execute("wallet-1", -100L));
    }

    @Test
    void shouldThrowWalletNotFoundExceptionWhenWalletDoesNotExist() {
        when(walletRepository.existsById("wallet-2")).thenReturn(false);
        assertThrows(WalletNotFoundException.class, () -> useCase.execute("wallet-2", 100L));
    }

    @Test
    void shouldThrowInvalidAmountExceptionWhenInsufficientFunds() {
        when(walletRepository.existsById("wallet-3")).thenReturn(true);
        when(transactionRepository.getBalance("wallet-3")).thenReturn(50L);
        assertThrows(InvalidAmountException.class, () -> useCase.execute("wallet-3", 100L));
    }

    @Test
    void shouldAddTransactionWhenValid() {
        var walletId = "wallet-4";
        var amount = 200L;
        when(walletRepository.existsById(walletId)).thenReturn(true);
        when(transactionRepository.getBalance(walletId)).thenReturn(500L);

        useCase.execute(walletId, amount);

        verify(transactionRepository).addTransaction(argThat(tx ->
                tx.walletId().equals(walletId)
                        && tx.amount().equals(amount)
                        && tx.operation() == Operation.DEBIT
                        && tx.origin() == Origin.WITHDRAW
        ));
    }
}
