package com.vicastro.walletservice.infra.repository.jpa;

import com.vicastro.walletservice.infra.repository.jpa.entity.WalletBalanceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface WalletBalanceJpaRepository extends JpaRepository<WalletBalanceEntity, Long> {
    Optional<WalletBalanceEntity> findTopByWalletIdOrderByReferenceDateDesc(String walletId);
}
