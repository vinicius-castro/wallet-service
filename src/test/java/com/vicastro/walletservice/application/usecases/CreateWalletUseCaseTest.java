package com.vicastro.walletservice.application.usecases;

import com.vicastro.walletservice.application.dto.CreateWalletInput;
import com.vicastro.walletservice.application.dto.CreateWalletOutput;
import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.domain.Wallet;
import com.vicastro.walletservice.shared.exception.InvalidWalletCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class CreateWalletUseCaseTest {

    private WalletRepository walletRepository;
    private CreateWalletUseCase useCase;

    @BeforeEach
    void setUp() {
        walletRepository = mock(WalletRepository.class);
        useCase = new CreateWalletUseCase(walletRepository);
    }

    @Test
    void shouldCreateWalletSuccessfully() {
        var userId = "user-1";
        var walletCode = "wallet-123";
        var input = new CreateWalletInput(userId);

        when(walletRepository.existsByUserId(userId)).thenReturn(false);
        var wallet = new Wallet(walletCode, userId);
        when(walletRepository.create(userId)).thenReturn(wallet);

        CreateWalletOutput output = useCase.execute(input);

        assertNotNull(output);
        assertEquals(walletCode, output.walletId());
        verify(walletRepository).create(userId);
    }

    @Test
    void shouldThrowExceptionWhenWalletAlreadyExists() {
        String userId = "user-1";
        CreateWalletInput input = new CreateWalletInput(userId);

        when(walletRepository.existsByUserId(userId)).thenReturn(true);

        assertThrows(InvalidWalletCreationException.class, () -> useCase.execute(input));
        verify(walletRepository, never()).create(anyString());
    }
}
