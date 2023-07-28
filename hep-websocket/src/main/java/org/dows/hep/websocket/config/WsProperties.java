package org.dows.hep.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.NestedConfigurationProperty;

@Data
@ConfigurationProperties("dows.websocket")
public class WsProperties {

    @NestedConfigurationProperty
    private Producer producer;


    @Data
    public static class Producer {
        @NestedConfigurationProperty
        private Retry retry;
    }


    @Data
    public static class Retry {
        //cron: 0/3 * * * * ?
        private String cron;
        // duration: 3
        private Long duration;
    }

}
