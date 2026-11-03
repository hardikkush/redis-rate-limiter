package com.RateLimitDemo.RateLimitDemo.Filter;

import com.RateLimitDemo.RateLimitDemo.Service.SlidingWindowRateLimiterService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class RateLimitFilter extends OncePerRequestFilter{

    private final SlidingWindowRateLimiterService rateLimiterService;

    public RateLimitFilter(SlidingWindowRateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        String userId = request.getHeader("X-User-Id");

        if (userId == null || userId.isBlank()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "User ID is required");
            return;
        }

        boolean allowed = rateLimiterService.isAllowed(userId);

        if (!allowed) {
            response.sendError(429, "Rate limit exceeded");
            return;
        }

        filterChain.doFilter(request, response);

    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getServletPath().startsWith("/api/");
    }


}
