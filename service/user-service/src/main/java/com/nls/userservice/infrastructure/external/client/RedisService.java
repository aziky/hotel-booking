package com.nls.userservice.infrastructure.external.client;

import com.nls.userservice.application.IRedisService;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class RedisService implements IRedisService {

    RedisTemplate<String, Object> redisTemplate;

    @Override
    public void save(String key, Object value, Duration ttl) {
        redisTemplate.opsForValue().set(key, value, ttl);
    }

    @Override
    public <T> T get(String key, Class<T> clazz) {
        Object value = redisTemplate.opsForValue().get(key);
        return value != null ? clazz.cast(value) : null;
    }

    @Override
    public void delete(String key) {
        redisTemplate.delete(key);
    }
}
