package com.vicastro.walletservice.infra.repository;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.vicastro.walletservice.infra.repository.cache.redis.WalletRedisRepository;
import com.vicastro.walletservice.infra.repository.jpa.WalletJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.entity.WalletEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class WalletRepositoryImplTest {

    private WalletJpaRepository jpaRepository;
    private WalletRedisRepository redisRepository;
    private WalletRepositoryImpl walletRepository;

    @BeforeEach
    void setUp() {
        jpaRepository = mock(WalletJpaRepository.class);
        redisRepository = mock(WalletRedisRepository.class);
        walletRepository = new WalletRepositoryImpl(jpaRepository, redisRepository);
    }

    @Test
    void shouldReturnTrueWhenWalletExistsByUserId() {
        var userId = "user-1";
        when(jpaRepository.existsByUserId(userId)).thenReturn(true);

        var exists = walletRepository.existsByUserId(userId);

        assertTrue(exists);
        verify(jpaRepository).existsByUserId(userId);
    }

    @Test
    void shouldReturnFalseWhenWalletDoesNotExistByUserId() {
        var userId = "user-2";
        when(jpaRepository.existsByUserId(userId)).thenReturn(false);

        var exists = walletRepository.existsByUserId(userId);

        assertFalse(exists);
        verify(jpaRepository).existsByUserId(userId);
    }

    @Test
    void shouldCreateWalletSuccessfully() {
        var userId = "user-3";
        var code = "wallet-123";
        var walletEntity = mock(WalletEntity.class);

        when(walletEntity.getCode()).thenReturn(code);
        when(walletEntity.getUserId()).thenReturn(userId);
        when(jpaRepository.save(any(WalletEntity.class))).thenReturn(walletEntity);

        var wallet = walletRepository.create(userId);

        assertNotNull(wallet);
        assertEquals(code, wallet.code());
        assertEquals(userId, wallet.userId());
        verify(jpaRepository).save(any(WalletEntity.class));
    }

    @Test
    void shouldReturnTrueWhenWalletExistsById() {
        var walletId = "wallet-1";
        when(jpaRepository.existsByCode(walletId)).thenReturn(true);

        var exists = walletRepository.existsById(walletId);

        assertTrue(exists);
        verify(jpaRepository).existsByCode(walletId);
    }

    @Test
    void shouldReturnFalseWhenWalletDoesNotExistById() {
        var walletId = "wallet-2";
        when(jpaRepository.existsByCode(walletId)).thenReturn(false);

        var exists = walletRepository.existsById(walletId);

        assertFalse(exists);
        verify(jpaRepository).existsByCode(walletId);
    }
}