package com.vicastro.walletservice.infra.repository.cache.redis;

import com.vicastro.walletservice.domain.WalletBalance;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class WalletBalanceRedisRepository extends RedisRepository<WalletBalance> {

    public static final String WALLET_BALANCE_CACHE_PREFIX = "WB:";

    public WalletBalanceRedisRepository(RedisTemplate<String, WalletBalance> redisTemplate) {
        super(redisTemplate);
    }

    @Override
    protected String generateKey(String walletId) {
        return WALLET_BALANCE_CACHE_PREFIX + walletId;
    }
}
