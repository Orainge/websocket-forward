package com.orainge.websocket_forward.server.websocket;

import com.alibaba.fastjson.JSON;
import com.orainge.websocket_forward.ApplicationContextUtils;
import com.orainge.websocket_forward.server.config.WebsocketClientConfig;
import com.orainge.websocket_forward.server.util.WebsocketServerUtil;
import com.orainge.websocket_forward.server.config.WebSocketServerConfigurator;
import com.orainge.websocket_forward.consts.AuthHeader;
import com.orainge.websocket_forward.consts.MessageCode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;

import javax.websocket.*;
import javax.websocket.server.ServerEndpoint;
import java.io.EOFException;

@Slf4j
@Controller
@ServerEndpoint(value = "/exchange", configurator = WebSocketServerConfigurator.class)
public class WebsocketServerEndpoint {
    @Autowired
    private WebsocketServerUtil webSocketUtil;

    @Autowired
    private WebsocketClientConfig websocketClientConfig;

    /**
     * 客户端信息
     */
    private WebsocketClientConfig.Config clientConfig;

    /**
     * 与某个客户端的连接对话，需要通过它来给客户端发送消息
     */
    private Session session;

    public WebsocketServerEndpoint() {
        this.webSocketUtil = ApplicationContextUtils.getBeanByClass(WebsocketServerUtil.class);
        this.websocketClientConfig = ApplicationContextUtils.getBeanByClass(WebsocketClientConfig.class);
    }

    @OnOpen
    public void onOpen(Session session, EndpointConfig config) {
        // 获取客户端 ID
        String clientId = (String) config.getUserProperties().get(AuthHeader.ID_HEADER_NAME);
        this.session = session;
        this.clientConfig = websocketClientConfig.getConfig(clientId);

        // 如果没有该客户端的信息，就断开连接
        if (this.clientConfig == null) {
            try {
                log.error("[Websocket 服务端] - 无法获取用户的客户端信息，关闭连接");
                session.close();
            } catch (Exception ignore) {
            }
            return;
        }

        boolean addSuccess = webSocketUtil.add(this);

        if (!addSuccess) {
            // 如果已有相同ID的客户端连接，则拒绝连接
            try {
                // 发送错误信息给客户端
                session.getBasicRemote().sendText(MessageCode.DUPLICATE_CLIENT_TAG);
                log.error("[Websocket 服务端] - 已有已有相同 ID [{}] 的客户端连接", JSON.toJSONString(this.clientConfig));
                // 关闭连接
                session.close();
            } catch (Exception ignore) {
            }
            return;
        }
        log.info("[Websocket 服务端] - 客户端 [{}] 连接成功", JSON.toJSONString(this.clientConfig));
    }

    @OnClose
    public void onClose() {
        webSocketUtil.remove(this);
        if (this.clientConfig != null) {
            log.info("[Websocket 服务端] - 客户端 [{}] 断开连接", this.clientConfig);
        }
    }

    @OnMessage
    public void onMessage(String message) {
        // 收到客户端发来的请求信息，交给工具类处理
        webSocketUtil.onMessage(clientConfig.getId(), session, message);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        if (error instanceof EOFException) {
            // 不显示错误信息
        } else {
            log.error("[Websocket 服务端] - 连接异常", error);
        }
    }

    public Session getSession() {
        return this.session;
    }

    public WebsocketClientConfig.Config getClientConfig() {
        return clientConfig;
    }
}
