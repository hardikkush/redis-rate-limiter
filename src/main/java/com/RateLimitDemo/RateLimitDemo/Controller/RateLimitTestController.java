package com.RateLimitDemo.RateLimitDemo.Controller;

import com.RateLimitDemo.RateLimitDemo.Service.SlidingWindowRateLimiterService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/rate-limit")
public class RateLimitTestController {

    private final SlidingWindowRateLimiterService rateLimiterService;

    public RateLimitTestController(
            SlidingWindowRateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @GetMapping("/check/{userId}")
    public ResponseEntity<String> checkRateLimit(@PathVariable String userId) {

        boolean allowed = rateLimiterService.isAllowed(userId);

        if (allowed) {
            return ResponseEntity.ok("Request Allowed");
        }

        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                .body("Rate Limit Exceeded");
    }



}
