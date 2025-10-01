package com.vicastro.walletservice.infra.repository.cache.redis;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
abstract class RedisRepository<T> {

    private final RedisTemplate<String, T> redisTemplate;

    public RedisRepository(RedisTemplate<String, T> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void save(String key, T object) {
        var keyGenerated = generateKey(key);
        redisTemplate.opsForValue().set(keyGenerated, object);
    }

    public Optional<T> get(String key) {
        var keyGenerated = generateKey(key);
        return Optional.ofNullable(redisTemplate.opsForValue().get(keyGenerated));
    }

    public void update(String key, T object) {
        var keyGenerated = generateKey(key);
        redisTemplate.opsForValue().set(keyGenerated, object);
    }

    public void invalidate(String key) {
        var keyGenerated = generateKey(key);
        redisTemplate.delete(keyGenerated);
    }

    abstract String generateKey(String id);
}