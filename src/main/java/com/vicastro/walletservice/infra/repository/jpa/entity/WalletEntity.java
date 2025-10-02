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
@Table(name = "wallet")
@Access(AccessType.FIELD)
public class WalletEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String code;

    @Column(name = "user_id")
    private String userId;

    public WalletEntity() { }

    public WalletEntity(String userId) {
        this.userId = userId;
        this.code = java.util.UUID.randomUUID().toString();
    }

    public String getCode() { return code; }
    public String getUserId() { return userId; }
}
