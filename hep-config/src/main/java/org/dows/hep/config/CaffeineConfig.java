package org.dows.hep.config;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.Expiry;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class CaffeineConfig {
    @Bean
    public Cache<String, Object> caffeineCache() {
        return Caffeine.newBuilder()
                .expireAfter(new CaffeineExpiry())
                .initialCapacity(100)
                .maximumSize(1000)
                .build();
    }

    class CaffeineExpiry implements Expiry<String, Object> {
        @Override
        public long expireAfterCreate(String key, Object value, long currentTime) {
            return 0;
        }

        @Override
        public long expireAfterUpdate(String key, Object value, long currentTime, long currentDuration) {
            return currentDuration;
        }

        @Override
        public long expireAfterRead(String key, Object value, long currentTime, long currentDuration) {
            return currentDuration;
        }
    }
}