package com.ratelimiter.fixedwindow.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class FixedWindowRateLimiter {

    private static final DateTimeFormatter TIME_FMT =
            DateTimeFormatter.ofPattern("HH:mm:ss.SSS").withZone(ZoneId.systemDefault());

    private final int maxRequests;
    private final long windowSizeInMillis;

    private final ConcurrentHashMap<String, WindowCounter> clientwindows = new ConcurrentHashMap<>();

    public FixedWindowRateLimiter(int maxRequests, long windowSizeInMillis) {
        this.maxRequests = maxRequests;
        this.windowSizeInMillis = windowSizeInMillis;
    }

    public boolean allowRequest(String clientId) {
        long now = System.currentTimeMillis();
        long currentWindowId = now / windowSizeInMillis;
        String nowFormatted = TIME_FMT.format(Instant.ofEpochMilli(now));

        WindowCounter counter = clientwindows.computeIfAbsent(
            clientId, k -> {
                System.out.println("[" + nowFormatted + "] NEW CLIENT '" + clientId
                        + "' -> created windowId=" + currentWindowId);
                return new WindowCounter(currentWindowId);
            }
        );

        synchronized (counter) {
            if (counter.windowId != currentWindowId) {
                long oldWindowId = counter.windowId;
                int requestsInOldWindow = counter.count.get();

                System.out.println("[" + nowFormatted + "] WINDOW EXPIRED for client '" + clientId
                        + "' | old windowId=" + oldWindowId
                        + " (had " + requestsInOldWindow + " requests)"
                        + " -> new windowId=" + currentWindowId
                        + " | counter RESET to 0");

                counter.windowId = currentWindowId;
                counter.count.set(0);
            }

            int newCount = counter.count.incrementAndGet();
            boolean allowed = newCount <= maxRequests;

            System.out.println("[" + nowFormatted + "] client='" + clientId
                    + "' windowId=" + currentWindowId
                    + " count=" + newCount + "/" + maxRequests
                    + " -> " + (allowed ? "ALLOWED" : "BLOCKED"));

            return allowed;
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