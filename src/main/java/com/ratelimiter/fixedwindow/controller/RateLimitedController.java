package com.ratelimiter.fixedwindow.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.ratelimiter.fixedwindow.util.FixedWindowRateLimiter;

@RestController
public class RateLimitedController {

    // 5 requests per 10-second window, per client — small window so you can test fast
    private final FixedWindowRateLimiter limiter = new FixedWindowRateLimiter(1, 60_000);
    @Async
    @GetMapping("/api/resource")
    public ResponseEntity<?> getResource(@RequestHeader("X-Client-Id") String clientId) {
        if (!limiter.allowRequest(clientId)) {
            return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                    .body("Rate limit exceeded. Try again in the next window.");
        }
        return ResponseEntity.ok("Request served at " + System.currentTimeMillis());
    }
}
