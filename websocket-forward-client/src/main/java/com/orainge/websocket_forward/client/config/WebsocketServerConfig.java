package com.orainge.websocket_forward.client.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "websocket-server")
@Data
public class WebsocketServerConfig {
    private String url;
    private Integer reconnectWait;
}
