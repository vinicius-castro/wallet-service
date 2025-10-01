package com.vicastro.walletservice.infra.repository;

import com.vicastro.walletservice.infra.repository.entity.WalletEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface WalletJpaRepository extends JpaRepository<WalletEntity, Long> {
    boolean existsByUserId(String userId);
}
