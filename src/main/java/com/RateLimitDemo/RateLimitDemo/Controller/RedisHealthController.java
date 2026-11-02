package com.RateLimitDemo.RateLimitDemo.Controller;

import com.RateLimitDemo.RateLimitDemo.Service.RedisHealthService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/redis")
public class RedisHealthController {

    private final RedisHealthService redisHealthService;

    public RedisHealthController(RedisHealthService redisHealthService) {
        this.redisHealthService = redisHealthService;
    }

    @GetMapping("/ping")
    public String pingRedis() {
        return redisHealthService.pingRedis();
    }
}
