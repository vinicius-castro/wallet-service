package com.vicastro.walletservice.infra.repository.jpa.entity;

import jakarta.persistence.Access;
import jakarta.persistence.AccessType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "transaction")
@Access(AccessType.FIELD)
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "wallet_id")
    private String walletId;

    private String code;

    private Long amount;

    private String operation;

    private String origin;

    public TransactionEntity(String walletId, Long amount, String operation, String origin) {
        this.walletId = walletId;
        this.code = java.util.UUID.randomUUID().toString();
        this.amount = amount;
        this.operation = operation;
        this.origin = origin;
    }

    public String getWalletId() { return walletId; }
    public String getCode() { return code; }
    public Long getAmount() { return amount; }
    public String getOperation() { return operation; }
    public String getOrigin() { return origin; }
}
