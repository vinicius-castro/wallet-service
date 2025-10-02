package com.vicastro.walletservice.infra.repository;

import com.vicastro.walletservice.application.repository.WalletBalanceRepository;
import com.vicastro.walletservice.domain.WalletBalance;
import com.vicastro.walletservice.infra.repository.jpa.WalletBalanceJpaRepository;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public class WalletBalanceRepositoryImpl implements WalletBalanceRepository {

    private final WalletBalanceJpaRepository walletBalanceJpaRepository;

    public WalletBalanceRepositoryImpl(WalletBalanceJpaRepository walletBalanceJpaRepository) {
        this.walletBalanceJpaRepository = walletBalanceJpaRepository;
    }

    @Override
    public Optional<Boolean> existsByWalletIdAndReferenceDate(String walletId, OffsetDateTime startDate) {
        return walletBalanceJpaRepository.existsByWalletIdAndReferenceDate(walletId, startDate);
    }

    @Override
    public Optional<WalletBalance> findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(String walletId, OffsetDateTime referenceDate) {
        var entity = walletBalanceJpaRepository.findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(walletId, referenceDate);
        return entity.map(walletBalanceEntity -> WalletBalance.builder()
                .walletId(walletBalanceEntity.getWalletId())
                .balance(walletBalanceEntity.getBalance())
                .referenceDate(walletBalanceEntity.getReferenceDate())
                .build()
        );
    }
}
