package com.RateLimitDemo.RateLimitDemo.Service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.data.redis.core.script.DefaultRedisScript;
import java.util.Collections;
import java.util.UUID;

import java.time.Duration;

@Service
public class SlidingWindowRateLimiterService {

    private final StringRedisTemplate stringRedisTemplate;

    private final DefaultRedisScript<Long> slidingWindowScript;

    private static final long WINDOW_SIZE_MS = 60_000;

    private static final int REQUEST_LIMIT = 5;

    public SlidingWindowRateLimiterService(StringRedisTemplate stringRedisTemplate,
                                           DefaultRedisScript<Long> slidingWindowScript) {

        this.stringRedisTemplate = stringRedisTemplate;
        this.slidingWindowScript = slidingWindowScript;
    }

    public void addRequest(String userId, String requestId, long timestamp) {

        String redisKey = "rate:" + userId;

        stringRedisTemplate.opsForZSet()
                .add(redisKey, requestId, timestamp);
    }

    public void removeExpiredRequests(String userId, long cutoffTimestamp) {

        String redisKey = "rate:" + userId;

        stringRedisTemplate.opsForZSet()
                .removeRangeByScore(redisKey, Double.NEGATIVE_INFINITY, cutoffTimestamp);
    }

    public Long countRequests(String userId) {

        String redisKey = "rate:" + userId;

        return stringRedisTemplate.opsForZSet()
                .zCard(redisKey);
    }

    public void setKeyExpiration(String userId, long windowSize) {

        String redisKey = "rate:" + userId;

        stringRedisTemplate.expire(
                redisKey,
                Duration.ofMillis(windowSize)
        );
    }

    public boolean isAllowed(String userId) {

        String redisKey = "rate:" + userId;

        long currentTimeStamp = System.currentTimeMillis();

        String requestId = UUID.randomUUID().toString();

        Long result = stringRedisTemplate.execute(
                slidingWindowScript,
                Collections.singletonList(redisKey),
                String.valueOf(currentTimeStamp),
                String.valueOf(WINDOW_SIZE_MS),
                String.valueOf(REQUEST_LIMIT),
                requestId
        );

        return Long.valueOf(1).equals(result);
    }

}
