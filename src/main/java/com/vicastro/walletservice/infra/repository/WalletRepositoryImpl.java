package com.vicastro.walletservice.infra.repository;

import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.domain.Wallet;
import com.vicastro.walletservice.infra.repository.entity.WalletEntity;
import org.springframework.stereotype.Component;

@Component
public class WalletRepositoryImpl implements WalletRepository {

    private final WalletJpaRepository jpaRepository;

    public WalletRepositoryImpl(WalletJpaRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public boolean existsByUserId(String userId) {
        return jpaRepository.existsByUserId(userId);
    }

    @Override
    public Wallet create(String userId) {
        var walletEntity = jpaRepository.save(new WalletEntity(userId));
        return Wallet.builder()
                .code(walletEntity.getCode())
                .userId(walletEntity.getUserId())
                .build();
    }
}
