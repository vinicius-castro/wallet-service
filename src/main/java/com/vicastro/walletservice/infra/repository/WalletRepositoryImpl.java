package com.vicastro.walletservice.infra.repository;

import com.vicastro.walletservice.application.repository.WalletRepository;
import com.vicastro.walletservice.domain.Wallet;
import com.vicastro.walletservice.infra.repository.cache.redis.WalletRedisRepository;
import com.vicastro.walletservice.infra.repository.jpa.WalletJpaRepository;
import com.vicastro.walletservice.infra.repository.jpa.entity.WalletEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional
public class WalletRepositoryImpl implements WalletRepository {

    private final WalletJpaRepository jpaRepository;
    private final WalletRedisRepository redisRepository;

    public WalletRepositoryImpl(WalletJpaRepository jpaRepository, WalletRedisRepository redisRepository) {
        this.jpaRepository = jpaRepository;
        this.redisRepository = redisRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsById(String walletId) {
        if (redisRepository.get(walletId).isPresent()) {
            return true;
        }
        var result = jpaRepository.existsByCode(walletId);
        if (result)
            redisRepository.save(walletId, true);
        return result;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByUserId(String userId) {
        if (redisRepository.get(userId).isPresent()) {
            return true;
        }
        var result = jpaRepository.existsByUserId(userId);
        if (result)
            redisRepository.save(userId, true);
        return result;
    }

    @Override
    public Wallet create(String userId) {
        var walletEntity = jpaRepository.save(new WalletEntity(userId));
        var wallet = Wallet.builder()
                .code(walletEntity.getCode())
                .userId(walletEntity.getUserId())
                .build();
        redisRepository.save(walletEntity.getCode(), true);
        redisRepository.save(walletEntity.getUserId(), true);
        return wallet;
    }
}
