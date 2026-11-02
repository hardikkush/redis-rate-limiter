package com.RateLimitDemo.RateLimitDemo.Service;

import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.stereotype.Service;

@Service
public class RedisHealthService {

    private final RedisConnectionFactory redisConnectionFactory;

    public RedisHealthService(RedisConnectionFactory redisConnectionFactory) {
        this.redisConnectionFactory = redisConnectionFactory;
    }

    public String pingRedis() {
        return redisConnectionFactory.getConnection().ping();
    }
}
