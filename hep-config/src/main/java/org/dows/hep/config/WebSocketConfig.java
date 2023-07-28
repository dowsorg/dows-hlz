package org.dows.hep.config;


import org.dows.framework.websocket.annotation.EnableWebSocket;
import org.dows.hep.websocket.config.WsProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties({WsProperties.class})
@EnableWebSocket(scanBasePackages = "org.dows.hep.websocket")
public class WebSocketConfig {
    
}
