package com.vicastro.walletservice.infra.repository.cache.redis;

import com.vicastro.walletservice.domain.WalletBalance;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

import java.time.Duration;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


class RedisRepositoryTest {

    private RedisTemplate<String, WalletBalance> redisTemplate;
    private ValueOperations<String, WalletBalance> valueOperations;
    private RedisRepository<WalletBalance> redisRepository;

    @BeforeEach
    void setUp() {
        redisTemplate = mock(RedisTemplate.class);
        valueOperations = mock(ValueOperations.class);
        when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        redisRepository = new WalletBalanceRedisRepository(redisTemplate);
    }

    @Test
    void shouldSaveSuccessfully() {
        var key = "key";
        var expectedKey = "WB:key";
        var value = mock(WalletBalance.class);

        redisRepository.save(key, value);

        verify(valueOperations, times(1)).set(expectedKey, value);
    }

    @Test
    void shouldReturnValueSuccessfully() {
        var key = "key";
        var expectedKey = "WB:key";
        var value = mock(WalletBalance.class);

        when(valueOperations.get(expectedKey)).thenReturn(value);

        Optional<WalletBalance> result = redisRepository.get(key);

        assertTrue(result.isPresent());
        assertEquals(value, result.get());
    }

    @Test
    void shouldReturnsEmptyOptionalWhenKeyNotFoundInCache() {
        var key = "key";
        when(valueOperations.get(key)).thenReturn(null);

        Optional<WalletBalance> result = redisRepository.get(key);

        assertFalse(result.isPresent());
    }

    @Test
    void shouldUpdateData() {
        var key = "key";
        var expectedKey = "WB:key";
        var value = mock(WalletBalance.class);

        redisRepository.update(key, value);

        verify(valueOperations, times(1)).set(expectedKey, value);
    }

    @Test
    void shouldInvalidateCacheByKey() {
        var key = "key";
        var expectedKey = "WB:key";
        redisRepository.invalidate(key);

        verify(redisTemplate, times(1)).delete(expectedKey);
    }
}