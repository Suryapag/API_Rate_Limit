package com.ratelimter.tokenbucket.config;

import com.ratelimter.tokenbucket.core.RateLimiterService;
import com.ratelimter.tokenbucket.interceptor.RateLimitInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class RateLimiterConfig implements WebMvcConfigurer {

    private final RateLimiterService rateLimiterService;

    public RateLimiterConfig(RateLimiterService rateLimiterService) {
        this.rateLimiterService = rateLimiterService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new RateLimitInterceptor(rateLimiterService))
                .addPathPatterns("/**");
    }

}