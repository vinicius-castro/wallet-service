package com.vicastro.walletservice.application.repository;

import com.vicastro.walletservice.domain.Wallet;

public interface WalletRepository {
    boolean existsById(String walletId);
    boolean existsByUserId(String userId);
    Wallet create(String userId);
}
