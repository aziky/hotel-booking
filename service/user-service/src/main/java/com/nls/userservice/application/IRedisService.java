package com.nls.userservice.application;

import java.time.Duration;

public interface IRedisService {
    void save(String key, Object value, Duration ttl);
    <T> T get(String key, Class<T> clazz);
    void delete(String key);
}
