package org.dows.hep.biz.cache;

import com.github.benmanes.caffeine.cache.Cache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@RequiredArgsConstructor
@Component
@Slf4j
public class LocalCache {

    private final Cache<String, Object> caffeineCache;

    public void set(String key, Object value, long duration, TimeUnit unit) {
        caffeineCache.policy().expireVariably().ifPresent(e -> {
            e.put(key, value, duration, unit);
        });
    }

    public <T> T get(String key) {
        return (T) caffeineCache.getIfPresent(key);
    }
}