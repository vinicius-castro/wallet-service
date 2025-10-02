package com.vicastro.walletservice.infra.service.scheduler;

import com.vicastro.walletservice.adapter.WalletBalanceController;
import com.vicastro.walletservice.domain.WalletBalance;
import com.vicastro.walletservice.infra.repository.TransactionRepositoryImpl;
import com.vicastro.walletservice.infra.repository.WalletBalanceRepositoryImpl;
import com.vicastro.walletservice.infra.repository.jpa.entity.WalletEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class DailySettlementProcessorTest {

    private WalletBalanceRepositoryImpl walletBalanceRepository;
    private TransactionRepositoryImpl transactionRepository;
    private DailySettlementProcessor processor;

    @BeforeEach
    void setUp() {
        walletBalanceRepository = mock(WalletBalanceRepositoryImpl.class);
        transactionRepository = mock(TransactionRepositoryImpl.class);
        processor = new DailySettlementProcessor(walletBalanceRepository, transactionRepository, "2024-06-01");
    }

    @Test
    void shouldReturnWalletBalanceEntityWhenProcessingWallet() {
        var wallet = mock(WalletEntity.class);
        when(wallet.getCode()).thenReturn("wallet-123");

        var expectedEntity = mock(WalletBalance.class);
        var controllerMock = mock(WalletBalanceController.class);
        when(controllerMock.execute(any(), any(), any())).thenReturn(expectedEntity);

        var result = processor.process(wallet);
        assertNotNull(result);
    }

    @Test
    void shouldSetStartAndEndDatesCorrectlyInConstructor() {
        var referenceDateStr = "2024-06-01";
        var processor = new DailySettlementProcessor(walletBalanceRepository, transactionRepository, referenceDateStr);

        var referenceDate = LocalDate.parse(referenceDateStr);
        var zoneId = ZoneId.of("America/Sao_Paulo");
        var expectedStart = referenceDate.atStartOfDay(zoneId).toOffsetDateTime();
        var expectedEnd = referenceDate.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime();

        try {
            var startDateField = DailySettlementProcessor.class.getDeclaredField("startDate");
            var endDateField = DailySettlementProcessor.class.getDeclaredField("endDate");
            startDateField.setAccessible(true);
            endDateField.setAccessible(true);
            var actualStart = (OffsetDateTime) startDateField.get(processor);
            var actualEnd = (OffsetDateTime) endDateField.get(processor);

            assertEquals(expectedStart, actualStart);
            assertEquals(expectedEnd, actualEnd);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
        }
    }
}