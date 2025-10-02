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

    @Query(value = "SELECT m.balance FROM WalletBalanceEntity m " +
            "WHERE m.walletId = :walletId " +
            "AND m.referenceDate <= :referenceDate " +
            "ORDER BY m.referenceDate DESC " +
            "LIMIT 1")
    Optional<Long> findLastBalanceBeforeOrEqual(@Param("walletId") String walletId,
                                                @Param("referenceDate") OffsetDateTime referenceDate);
}
