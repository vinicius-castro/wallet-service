package com.vicastro.walletservice.infra.service.scheduler;

import com.vicastro.walletservice.adapter.WalletBalanceController;
import com.vicastro.walletservice.infra.repository.TransactionRepositoryImpl;
import com.vicastro.walletservice.infra.repository.WalletBalanceRepositoryImpl;
import com.vicastro.walletservice.infra.repository.jpa.entity.WalletBalanceEntity;
import com.vicastro.walletservice.infra.repository.jpa.entity.WalletEntity;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneId;

@Component
@StepScope
public class DailySettlementProcessor implements ItemProcessor<WalletEntity, WalletBalanceEntity> {

    private final OffsetDateTime startDate;
    private final OffsetDateTime endDate;

    private final WalletBalanceRepositoryImpl walletBalanceRepository;
    private final TransactionRepositoryImpl transactionRepository;

    public DailySettlementProcessor(WalletBalanceRepositoryImpl walletBalanceRepository,
                                    TransactionRepositoryImpl transactionRepository,
                                    @Value("#{jobParameters['referenceDate']}") String referenceDateStr
    ) {
        this.walletBalanceRepository = walletBalanceRepository;
        this.transactionRepository = transactionRepository;
        var referenceDate = LocalDate.parse(referenceDateStr);
        var zoneId = ZoneId.of("America/Sao_Paulo");
        this.startDate = referenceDate.atStartOfDay(zoneId).toOffsetDateTime();
        this.endDate = referenceDate.plusDays(1).atStartOfDay(zoneId).toOffsetDateTime();
    }

    @Override
    public WalletBalanceEntity process(WalletEntity wallet) {
        var walletBalance = new WalletBalanceController(walletBalanceRepository, transactionRepository)
                .execute(wallet.getCode(), this.startDate, this.endDate);
        if (walletBalance == null) {
            return null;
        }
        return new WalletBalanceEntity(walletBalance);
    }
}
