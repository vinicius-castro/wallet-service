package com.vicastro.walletservice.infra.repository.jpa.entity;

import com.vicastro.walletservice.domain.Transaction;
import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.OffsetDateTime;

@Entity
@Table(name = "transaction")
@Access(AccessType.FIELD)
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_id")
    private String walletId;

    @Column(name = "wallet_id_related")
    private String walletIdRelated;

    private String code;

    private Long amount;

    private String operation;

    private String origin;

    @Column(name = "created_at", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime createdAt;

    public TransactionEntity(Transaction transaction) {
        this.walletId = transaction.walletId();
        this.walletIdRelated = transaction.walletIdRelated();
        this.code = transaction.id();
        this.amount = transaction.amount();
        this.operation = transaction.operation().name();
        this.origin = transaction.origin().name();
    }

    public String getWalletId() { return walletId; }
    public String getWalletIdRelated() { return walletIdRelated; }
    public String getCode() { return code; }
    public Long getAmount() { return amount; }
    public String getOperation() { return operation; }
    public String getOrigin() { return origin; }
    public OffsetDateTime getCreatedAt() { return createdAt; }

    @PrePersist
    protected void onCreate() {
        createdAt = OffsetDateTime.now();
    }
}
