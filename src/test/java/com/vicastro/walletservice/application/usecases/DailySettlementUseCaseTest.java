package com.vicastro.walletservice.application.usecases;


import com.vicastro.walletservice.application.repository.TransactionRepository;
import com.vicastro.walletservice.application.repository.WalletBalanceRepository;
import com.vicastro.walletservice.domain.WalletBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;

class DailySettlementUseCaseTest {

    private WalletBalanceRepository balanceRepository;
    private TransactionRepository transactionRepository;
    private DailySettlementUseCase useCase;

    @BeforeEach
    void setUp() {
        balanceRepository = mock(WalletBalanceRepository.class);
        transactionRepository = mock(TransactionRepository.class);
        useCase = new DailySettlementUseCase(balanceRepository, transactionRepository);
    }

    @Test
    void shouldReturnNullWhenSettlementAlreadyExists() {
        String walletId = "wallet-1";
        OffsetDateTime startDate = OffsetDateTime.now();
        OffsetDateTime endDate = startDate.plusDays(1);

        when(balanceRepository.existsByWalletIdAndReferenceDate(walletId, startDate)).thenReturn(Optional.of(true));

        WalletBalance result = useCase.execute(walletId, startDate, endDate);

        assertNull(result);
        verify(balanceRepository).existsByWalletIdAndReferenceDate(walletId, startDate);
        verifyNoMoreInteractions(balanceRepository, transactionRepository);
    }

    @Test
    void shouldReturnNullWhenPreviousBalanceIsNotFromDayBefore() {
        String walletId = "wallet-2";
        OffsetDateTime startDate = OffsetDateTime.of(2024, 6, 10, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = startDate.plusDays(1);

        when(balanceRepository.existsByWalletIdAndReferenceDate(walletId, startDate)).thenReturn(Optional.of(false));
        WalletBalance prevBalance = WalletBalance.builder()
                .walletId(walletId)
                .balance(100L)
                .referenceDate(startDate.minusDays(2)) // Not the day before
                .build();
        when(balanceRepository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, startDate))
                .thenReturn(Optional.of(prevBalance));

        WalletBalance result = useCase.execute(walletId, startDate, endDate);

        assertNull(result);
        verify(balanceRepository).existsByWalletIdAndReferenceDate(walletId, startDate);
        verify(balanceRepository).findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, startDate);
        verifyNoMoreInteractions(balanceRepository, transactionRepository);
    }

    @Test
    void shouldCalculateBalanceWhenNoPreviousBalance() {
        String walletId = "wallet-3";
        OffsetDateTime startDate = OffsetDateTime.of(2024, 6, 10, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = startDate.plusDays(1);

        when(balanceRepository.existsByWalletIdAndReferenceDate(walletId, startDate)).thenReturn(Optional.of(false));
        when(balanceRepository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, startDate))
                .thenReturn(Optional.empty());
        when(transactionRepository.calculateBalanceByWalletLessThanEndDate(walletId, endDate)).thenReturn(Optional.of(200L));

        WalletBalance result = useCase.execute(walletId, startDate, endDate);

        assertNotNull(result);
        assertEquals(walletId, result.walletId());
        assertEquals(200L, result.balance());
        assertEquals(startDate, result.referenceDate());
        verify(balanceRepository).existsByWalletIdAndReferenceDate(walletId, startDate);
        verify(balanceRepository).findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, startDate);
        verify(transactionRepository).calculateBalanceByWalletLessThanEndDate(walletId, endDate);
    }

    @Test
    void shouldCalculateBalanceWhenPreviousBalanceExists() {
        String walletId = "wallet-4";
        OffsetDateTime startDate = OffsetDateTime.of(2024, 6, 10, 0, 0, 0, 0, ZoneOffset.UTC);
        OffsetDateTime endDate = startDate.plusDays(1);

        when(balanceRepository.existsByWalletIdAndReferenceDate(walletId, startDate)).thenReturn(Optional.of(false));
        WalletBalance prevBalance = WalletBalance.builder()
                .walletId(walletId)
                .balance(300L)
                .referenceDate(startDate.minusDays(1))
                .build();
        when(balanceRepository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, startDate))
                .thenReturn(Optional.of(prevBalance));
        when(transactionRepository.calculateBalanceByWalletIdAndDateRange(walletId, startDate, endDate)).thenReturn(Optional.of(150L));

        WalletBalance result = useCase.execute(walletId, startDate, endDate);

        assertNotNull(result);
        assertEquals(walletId, result.walletId());
        assertEquals(450L, result.balance());
        assertEquals(startDate, result.referenceDate());
        verify(balanceRepository).existsByWalletIdAndReferenceDate(walletId, startDate);
        verify(balanceRepository).findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, startDate);
        verify(transactionRepository).calculateBalanceByWalletIdAndDateRange(walletId, startDate, endDate);
    }
}