package com.vicastro.walletservice.infra.repository.jpa;

import com.vicastro.walletservice.infra.repository.jpa.entity.WalletBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface WalletBalanceJpaRepository extends JpaRepository<WalletBalanceEntity, Long> {
    Optional<WalletBalanceEntity> findTopByWalletIdOrderByReferenceDateDesc(String walletId);

    Optional<WalletBalanceEntity> findTopByWalletIdAndReferenceDateLessThanEqualOrderByReferenceDateDesc(
            @Param("walletId") String walletId,
            @Param("referenceDate") OffsetDateTime referenceDate
    );

    Optional<Boolean> existsByWalletIdAndReferenceDate(@Param("walletId") String walletId,
                                                       @Param("referenceDate") OffsetDateTime referenceDate);
}
