package org.dows.hep.biz.config;

import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

/**
 * @author runsix
 */
@Configuration
@Slf4j
public class RedissonConfig {
    @Bean
    @Profile("dev")
    @ConditionalOnMissingBean(value = RedissonClient.class)
    @ConditionalOnResource(resources = {"application-redisson-dev.yml"})
    public RedissonClient redissonClientDev() {
        try {
            return Redisson.create(Config.fromYAML(new ClassPathResource("application-redisson-dev.yml").getInputStream()));
        } catch (IOException e) {
            log.error("Init RedissonClient failed!!!", e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Profile("test")
    @ConditionalOnMissingBean(value = RedissonClient.class)
    @ConditionalOnResource(resources = {"application-redisson-test.yml"})
    public RedissonClient redissonClientTest() {
        try {
            return Redisson.create(Config.fromYAML(new ClassPathResource("application-redisson-test.yml").getInputStream()));
        } catch (IOException e) {
            log.error("Init RedissonClient failed!!!", e);
            throw new RuntimeException(e);
        }
    }

    @Bean
    @Profile("staging")
    @ConditionalOnMissingBean(value = RedissonClient.class)
    @ConditionalOnResource(resources = {"application-redisson-staging.yml"})
    public RedissonClient redissonClientStaging() {
        try {
            return Redisson.create(Config.fromYAML(new ClassPathResource("application-redisson-staging.yml").getInputStream()));
        } catch (IOException e) {
            log.error("Init RedissonClient failed!!!", e);
            throw new RuntimeException(e);
        }
    }
}