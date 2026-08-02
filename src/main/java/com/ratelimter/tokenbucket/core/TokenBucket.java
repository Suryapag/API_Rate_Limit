package com.ratelimter.tokenbucket.core;

import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Thread-safe token bucket for a single rate-limit key (e.g. per API key, per IP, per tenant).
 * Refill is lazy: tokens are computed on demand, not via a scheduled background thread.
 */

public class TokenBucket
{
    private final long capacity;
    private final double refillTokensPerSecond;

    private double availableTokens;
    private  long lastRefillTimestamp;

    private final ReentrantLock lock =  new ReentrantLock();

    public TokenBucket(long capacity, double refillTokensPerSecond) {
        this.capacity = capacity;
        this.refillTokensPerSecond = refillTokensPerSecond;
        this.availableTokens = capacity;
        this.lastRefillTimestamp = System.nanoTime();
    }

    public boolean tryConsume()
    {
        lock.lock();
        try {
            refill();
            if (availableTokens >= 1){
                availableTokens--;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }
    public void refill()
    {
        long now = System.nanoTime();
        double elapsedSeconds = (now - lastRefillTimestamp) / 1_000_000_000.0;
        double tokensToAdd = elapsedSeconds * refillTokensPerSecond;

        if(tokensToAdd > 0)
        {
            availableTokens = Math.min(capacity, availableTokens + tokensToAdd);
            lastRefillTimestamp = now;
        }
    }
}