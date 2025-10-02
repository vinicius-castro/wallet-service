package com.vicastro.walletservice.infra.repository.jpa.entity;

import com.vicastro.walletservice.domain.WalletBalance;
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
@Table(name = "wallet_balance")
@Access(AccessType.FIELD)
public class WalletBalanceEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long balance;

    @Column(name = "wallet_id")
    private String walletId;

    @Column(name = "reference_date", columnDefinition = "TIMESTAMPTZ")
    private OffsetDateTime referenceDate;

    public WalletBalanceEntity() { }

    public WalletBalanceEntity(String walletId, Long balance, OffsetDateTime referenceDate) {
        this.walletId = walletId;
        this.balance = balance;
        this.referenceDate = referenceDate;
    }

    public WalletBalanceEntity(WalletBalance walletBalance) {
        this.walletId = walletBalance.walletId();
        this.balance = walletBalance.balance();
        this.referenceDate = walletBalance.referenceDate();
    }

    public String getWalletId() { return walletId; }
    public Long getBalance() { return balance; }
    public OffsetDateTime getReferenceDate() { return referenceDate; }
}
