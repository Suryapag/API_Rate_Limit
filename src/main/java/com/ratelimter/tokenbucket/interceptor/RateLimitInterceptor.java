package com.ratelimter.tokenbucket.interceptor;

import com.ratelimter.tokenbucket.core.RateLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.servlet.HandlerInterceptor;

public class RateLimitInterceptor implements HandlerInterceptor {

    private final RateLimiterService rateLimiterService;

    public RateLimitInterceptor(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String key = request.getHeader("user_id"); // or request.getRemoteAddr() for per-IP
        if (key == null) key = "anonymous";

        if (rateLimiterService.isAllowed(key)) {
            return true;
        }
        response.setStatus(429);
        response.setContentType("application/json");
        response.getWriter().write("{\"error\":\"Rate limit exceeded. Try again shortly.\"}");
        return false;
    }
}