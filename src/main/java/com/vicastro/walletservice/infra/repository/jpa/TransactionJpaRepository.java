package com.vicastro.walletservice.infra.repository.jpa;

import com.vicastro.walletservice.infra.repository.jpa.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {
}
