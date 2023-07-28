package org.dows.hep.config;


import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.dows.framework.websocket.annotation.EnableWebSocket;
import org.dows.hep.websocket.config.WsProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Slf4j
@EnableWebSocket(scanBasePackages = "org.dows.hep.websocket")
public class WebSocketConfig {


    @Configuration
    @EnableConfigurationProperties({WsProperties.class})
    public static class InitWsProperties {
        @Autowired
        private WsProperties wsProperties;

        @PostConstruct
        public void init() {
            log.info("");
        }
    }


}
