package com.vicastro.walletservice.infra.repository.jpa;

import com.vicastro.walletservice.infra.repository.jpa.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletJpaRepository extends JpaRepository<WalletEntity, Long> {
    boolean existsByUserId(String userId);
    boolean existsByCode(String walletId);
}
