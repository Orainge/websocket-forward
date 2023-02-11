package com.orainge.websocket_forward.server.config;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.*;

@Configuration
@ConfigurationProperties("websocket-client")
@Data
@Slf4j
public class WebsocketClientConfig {
    private List<Config> list;

    private Map<String, Config> clientConfig = new HashMap<>();

    @Data
    public static class Config {
        private String id;
        private String key;
        private String description;
        private Boolean keepAlive;
    }

    @PostConstruct
    public void init() throws Exception {
        for (Config config : list) {
            String id = config.getId();
            if (id == null || "".equals(id)) {
                throw new NullPointerException("客户端 ID 为空");
            }

            if (clientConfig.containsKey(id)) {
                throw new Exception("客户端 ID 重复");
            }

            clientConfig.put(id, config);
        }
    }

    /**
     * 根据客户端 ID 获取配置信息
     */
    public Config getConfig(String id) {
        return clientConfig.get(id);
    }
}
