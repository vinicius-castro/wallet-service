package com.vicastro.walletservice.infra.repository.cache.redis;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class WalletRedisRepositoryTest {

    private RedisTemplate<String, Boolean> redisTemplate;
    private ValueOperations<String, Boolean> valueOperations;
    private WalletRedisRepository repository;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        repository = new WalletRedisRepository(redisTemplate);
    }

    @Test
    void shouldGenerateKeyWithPrefix() {
        var id = "wallet-123";
        var expected = WalletRedisRepository.WALLET_CACHE_PREFIX + id;
        assertEquals(expected, repository.generateKey(id));
    }

    @Test
    void shouldDelegateSaveToParent() {
        repository.save("id1", true);
        verify(valueOperations).set(WalletRedisRepository.WALLET_CACHE_PREFIX + "id1", true);
    }

    @Test
    void shouldDelegateGetToParent() {
        repository.get("id2");
        verify(valueOperations).get(WalletRedisRepository.WALLET_CACHE_PREFIX + "id2");
    }

    @Test
    void shouldDelegateUpdateToParent() {
        repository.update("id3", false);
        verify(valueOperations).set(WalletRedisRepository.WALLET_CACHE_PREFIX + "id3", false);
    }

    @Test
    void shouldDelegateInvalidateToParent() {
        repository.invalidate("id4");
        verify(redisTemplate).delete(WalletRedisRepository.WALLET_CACHE_PREFIX + "id4");
    }
}