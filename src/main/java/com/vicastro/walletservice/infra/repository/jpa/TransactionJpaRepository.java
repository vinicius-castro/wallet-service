package com.vicastro.walletservice.infra.repository.jpa;

import com.vicastro.walletservice.infra.repository.jpa.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;

@Repository
public interface TransactionJpaRepository extends JpaRepository<TransactionEntity, Long> {

    @Query("""
        SELECT SUM(
            CASE 
                WHEN t.operation = 'CREDIT' THEN t.amount
                WHEN t.operation = 'DEBIT' THEN -t.amount
                ELSE 0 
            END
        ) 
        FROM TransactionEntity t 
        WHERE t.walletId = :walletId 
        AND t.createdAt > :startDate
    """)
    Long calculateBalanceByWalletAndDate(
            @Param("walletId") String walletId,
            @Param("startDate") OffsetDateTime startDate
    );

    @Query("""
        SELECT SUM(
            CASE 
                WHEN t.operation = 'CREDIT' THEN t.amount
                WHEN t.operation = 'DEBIT' THEN -t.amount
                ELSE 0 
            END
        ) 
        FROM TransactionEntity t 
        WHERE t.walletId = :walletId
    """)
    Optional<Long> calculateBalanceByWallet(
            @Param("walletId") String walletId
    );
}
