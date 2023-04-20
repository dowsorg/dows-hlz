package org.dows.hep.config;

import org.dows.sequence.api.IdGenerator;
import org.dows.sequence.snowflake.SnowflakeIdGenerator;
import org.dows.sequence.snowflake.config.SnowFlakeConfiguration;
import org.dows.sequence.snowflake.config.SnowFlakeProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties({SnowFlakeProperties.class})
public class IdConfig {

    @Autowired
    private SnowFlakeProperties snowFlakeProperties;

    @Bean
    public IdGenerator idGenerator() {
        return new SnowflakeIdGenerator(SnowFlakeConfiguration.parse(snowFlakeProperties));
    }

}
