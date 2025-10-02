package com.vicastro.walletservice.application.repository;

import com.vicastro.walletservice.domain.WalletBalance;

import java.time.OffsetDateTime;
import java.util.Optional;

public interface WalletBalanceRepository {
    Optional<Boolean> existsByWalletIdAndReferenceDate(String walletId, OffsetDateTime startDate);

    Optional<WalletBalance> findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(
            String walletId,
            OffsetDateTime referenceDate
    );
}
