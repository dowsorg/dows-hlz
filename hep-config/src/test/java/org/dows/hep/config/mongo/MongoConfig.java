package org.dows.hep.config.mongo;

import org.dows.framework.api.util.YamlPropertySourceFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.data.mongodb.MongoDatabaseFactory;
import org.springframework.data.mongodb.MongoTransactionManager;

//@PropertySource(value = "classpath:application.yml" ,factory = YamlPropertySourceFactory.class)
@Configuration
public class MongoConfig {
    @Bean
    @ConditionalOnProperty(name="spring.data.mongodb.transactionEnabled",havingValue = "true")
    MongoTransactionManager transactionManager(MongoDatabaseFactory factory){
        return new MongoTransactionManager(factory);
    }

}