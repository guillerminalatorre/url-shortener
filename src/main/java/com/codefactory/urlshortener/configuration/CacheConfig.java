package com.codefactory.urlshortener.configuration;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;


@Configuration
@EnableCaching
public class CacheConfig {

    @Bean
    @Profile("dev")
    public RedisCacheManager cacheManager(RedisConnectionFactory redisConnectionFactory) {
        return RedisCacheManager.builder(redisConnectionFactory).build();
    }
}
