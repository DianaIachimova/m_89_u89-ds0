package com.example.insurance_app.infrastructure.config.cache;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.interceptor.CacheErrorHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import tools.jackson.databind.ObjectMapper;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableCaching
@ConditionalOnClass(RedisConnectionFactory.class)
@ConditionalOnProperty(name = "spring.cache.type", havingValue = "redis")
public class RedisCacheManagerConfig {

    @Value("${app.cache.prefix}")
    private String cachePrefix;

    @Value("${app.cache.default-ttl-minutes:10}")
    private long defaultTtlMinutes;

    @Bean
    public CacheManager cacheManager(
            RedisConnectionFactory connectionFactory,
            ObjectMapper objectMapper
    ) {

        var keySerializer = new StringRedisSerializer();
        var valueSerializer = new GenericJacksonJsonRedisSerializer(objectMapper);

        RedisCacheConfiguration defaultConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofMinutes(defaultTtlMinutes))
                .disableCachingNullValues()
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(keySerializer))
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(valueSerializer))
                .computePrefixWith(this::buildPrefix);

        Map<String, RedisCacheConfiguration> cacheConfigurations = buildCacheConfigurations(defaultConfig);

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaultConfig)
                .withInitialCacheConfigurations(cacheConfigurations)
                .transactionAware()
                .build();
    }

    private Map<String, RedisCacheConfiguration> buildCacheConfigurations(RedisCacheConfiguration defaultConfig) {

        Map<String, RedisCacheConfiguration> configs = new HashMap<>();

        configs.put(CacheNames.COUNTRIES, defaultConfig.entryTtl(Duration.ofHours(24)));
        configs.put(CacheNames.COUNTIES, defaultConfig.entryTtl(Duration.ofHours(24)));
        configs.put(CacheNames.CITIES, defaultConfig.entryTtl(Duration.ofHours(24)));
        configs.put(CacheNames.CURRENCIES, defaultConfig.entryTtl(Duration.ofMinutes(30)));
        configs.put(CacheNames.FEES_CONFIGS, defaultConfig.entryTtl(Duration.ofMinutes(15)));
        configs.put(CacheNames.RISK_FACTORS, defaultConfig.entryTtl(Duration.ofMinutes(15)));

        return configs;
    }

    private String buildPrefix(String cacheName) {
        return cachePrefix + ":" + cacheName + ":";
    }

    @Bean
    public CacheErrorHandler errorHandler() {
        return new LoggingCacheErrorHandler();
    }
}