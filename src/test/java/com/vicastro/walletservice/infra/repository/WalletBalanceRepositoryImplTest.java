package com.vicastro.walletservice.infra.repository;


import com.vicastro.walletservice.domain.WalletBalance;
import com.vicastro.walletservice.infra.repository.jpa.WalletBalanceJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.entity.WalletBalanceEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WalletBalanceRepositoryImplTest {

    private WalletBalanceJpaRepository walletBalanceJpaRepository;
    private WalletBalanceRepositoryImpl repository;

    @BeforeEach
    void setUp() {
        walletBalanceJpaRepository = mock(WalletBalanceJpaRepository.class);
        repository = new WalletBalanceRepositoryImpl(walletBalanceJpaRepository);
    }

    @Test
    void shouldReturnExistsByWalletIdAndReferenceDate() {
        String walletId = "wallet-1";
        OffsetDateTime date = OffsetDateTime.now();
        Optional<Boolean> expected = Optional.of(true);

        when(walletBalanceJpaRepository.existsByWalletIdAndReferenceDate(walletId, date)).thenReturn(expected);

        Optional<Boolean> result = repository.existsByWalletIdAndReferenceDate(walletId, date);

        assertEquals(expected, result);
        verify(walletBalanceJpaRepository).existsByWalletIdAndReferenceDate(walletId, date);
    }

    @Test
    void shouldReturnWalletBalanceDomainObjectWhenEntityFound() {
        String walletId = "wallet-2";
        OffsetDateTime date = OffsetDateTime.now();
        WalletBalanceEntity entity = new WalletBalanceEntity(walletId, 1000L, date);

        when(walletBalanceJpaRepository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, date))
                .thenReturn(Optional.of(entity));

        Optional<WalletBalance> result = repository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, date);

        assertTrue(result.isPresent());
        assertEquals(walletId, result.get().walletId());
        assertEquals(1000L, result.get().balance());
        assertEquals(date, result.get().referenceDate());
        verify(walletBalanceJpaRepository).findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, date);
    }

    @Test
    void shouldReturnEmptyWhenNoEntityFound() {
        String walletId = "wallet-3";
        OffsetDateTime date = OffsetDateTime.now();

        when(walletBalanceJpaRepository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, date))
                .thenReturn(Optional.empty());

        Optional<WalletBalance> result = repository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, date);

        assertTrue(result.isEmpty());
        verify(walletBalanceJpaRepository).findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, date);
    }
}