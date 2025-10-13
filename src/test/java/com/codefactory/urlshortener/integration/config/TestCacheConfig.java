package com.codefactory.urlshortener.integration.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableCaching
public class TestCacheConfig {

    @Bean
    @Profile("test")
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("urls");
    }
}
