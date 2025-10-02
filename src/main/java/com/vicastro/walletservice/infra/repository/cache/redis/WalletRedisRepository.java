package com.vicastro.walletservice.infra.repository.cache.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletRedisRepository extends RedisRepository<Boolean> {

    public static final String WALLET_CACHE_PREFIX = "W:";

    public WalletRedisRepository(RedisTemplate<String, Boolean> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String generateKey(String id) {
        return WALLET_CACHE_PREFIX + id;
    }
}
