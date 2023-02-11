package com.orainge.websocket_forward.server.util;

import com.orainge.websocket_forward.server.config.WebsocketClientConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Objects;

@Component
@Slf4j
public class WebsocketClientUtil {
    @Autowired
    private WebsocketClientConfig websocketClientConfig;

    /**
     * 验证凭据是否合法, 合法存入客户端信息
     *
     * @param clientId   客户端 ID
     * @param clientCode 客户端动态验证码
     * @return true: 该节点合法; false: 该节点不合法
     */
    public boolean verifyClient(String clientId, String clientCode) {
        if (StringUtils.isEmpty(clientId)) {
            // 没有 ID 或 动态验证码
            return false;
        }

        WebsocketClientConfig.Config config = websocketClientConfig.getConfig(clientId);

        if (config == null) {
            log.error("[Websocket 服务端] - 客户端不存在 [ID: " + clientId + "]");
            return false;
        }

        // 验证 clientCode
        boolean isValid = Objects.equals(config.getKey(), clientCode);

        if (isValid) {
            log.info("[Websocket 服务端] - 客户端验证成功 [ID: " + clientId + "]");
        } else {
            log.error("[Websocket 服务端] - 客户端验证失败 [ID: " + clientId + "]");
        }

        return isValid;
    }
}
