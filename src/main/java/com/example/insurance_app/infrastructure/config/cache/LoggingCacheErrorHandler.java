package com.example.insurance_app.infrastructure.config.cache;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.cache.interceptor.CacheErrorHandler;

public class LoggingCacheErrorHandler implements CacheErrorHandler {
    private static final Logger log = LoggerFactory.getLogger(LoggingCacheErrorHandler.class);

    @Override
    public void handleCacheGetError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Cache GET error (cache={}, key={}) -> fallback to DB. {}",
                safe(cache), safeKey(key), exception.toString());
    }

    @Override
    public void handleCachePutError(RuntimeException exception, Cache cache, Object key, Object value) {
        log.warn("Cache PUT error (cache={}, key={}) -> ignoring. {}",
                safe(cache), safeKey(key), exception.toString());
    }

    @Override
    public void handleCacheEvictError(RuntimeException exception, Cache cache, Object key) {
        log.warn("Cache EVICT error (cache={}, key={}) -> ignoring. {}",
                safe(cache), safeKey(key), exception.toString());
    }

    @Override
    public void handleCacheClearError(RuntimeException exception, Cache cache) {
        log.warn("Cache CLEAR error (cache={}) -> ignoring. {}",
                safe(cache), exception.toString());
    }

    private static String safe(Cache cache) {
        return cache != null ? cache.getName() : "null";
    }

    private static String safeKey(Object key) {
        if (key == null) return "null";
        String s = String.valueOf(key);
        return s.length() > 200 ? s.substring(0, 200) + "…" : s;
    }
}
