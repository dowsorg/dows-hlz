package org.dows.hep.websocket.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties("dows.websocket")
public class WsProperties {

    private Producer producer;

    @Data
    public static class Producer {
        //cron: 0/3 * * * * ?
        private String cron;
        // duration: 3
        private Long duration;
    }

}
