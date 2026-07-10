package com.ratelimiter.fixedwindow.util;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter {
    
    private final int maxRequests; // e.g 100 requests
    private final long windowSizeInMillis; // e.g 60 seconds, 60_000 for 1 minute

    // key -> [ windowId, WindowCounter ] packed as a small holder class
    private final ConcurrentHashMap<String, WindowCounter> clientwindows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    public boolean allowRequest(String clientId) {
        long currentWindowId = System.currentTimeMillis() / windowSizeInMillis;

        WindowCounter counter = clientwindows.computeIfAbsent(
            clientId, k -> new WindowCounter(currentWindowId)
        );

        synchronized (counter) {
            // If we've rolled into a new window, reset the counter
            if (counter.windowId != currentWindowId) {
                counter.windowId = currentWindowId;
                counter.count.set(0);
            }
            // Atomically increment and check
            if (counter.count.incrementAndGet() <= maxRequests) {
                return true;
            }
            return false;
        }
    }

     private static class WindowCounter {
        volatile long windowId;
        final AtomicInteger count = new AtomicInteger(0);

        WindowCounter(long windowId) {
            this.windowId = windowId;
        }
    }
}
