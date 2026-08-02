package com.ratelimter.tokenbucket.core;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class RateLimiterService {

    private final ConcurrentHashMap<String, TokenBucket> buckets = new ConcurrentHashMap<>();

    private final long capacity;
    private  final double refillRate;

    public RateLimiterService(
            @Value("${ratelimiter.capacity}") long capacity,
            @Value("${ratelimiter.refill-rate}") double refillRate){
        this.capacity = capacity;
        this.refillRate = refillRate;
    }

    public boolean isAllowed(String key){
        TokenBucket bucket = buckets.computeIfAbsent(key, k -> new TokenBucket(capacity, refillRate));
        return bucket.tryConsume();
    }
}