package com.orainge.websocket_forward.server.util;

import com.orainge.websocket_forward.consts.MessageCode;
import com.orainge.websocket_forward.server.config.WebsocketClientConfig;
import com.orainge.websocket_forward.server.processor.OnMessageProcessor;
import com.orainge.websocket_forward.server.websocket.ClientResponseContainer;
import com.orainge.websocket_forward.server.websocket.WebsocketServerEndpoint;
import com.orainge.websocket_forward.server.websocket.WebsocketServerEndpointContainer;
import com.orainge.websocket_forward.util.JSONUtil;
import com.orainge.websocket_forward.util.uuid.UUIDUtil;
import com.orainge.websocket_forward.vo.Result;
import com.orainge.websocket_forward.vo.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.websocket.Session;

@Component
@EnableScheduling
@Slf4j
public class WebsocketServerUtil {
    @Autowired
    private OnMessageProcessor onMessageProcessor;

    @Autowired
    private WebsocketClientConfig websocketClientConfig;

    /**
     * 等待回复报文的时间
     */
    @Value("${websocket-server.response.time-out: 20000}")
    private int responseTimeout;

    /**
     * 添加 Websocket 服务端对象
     *
     * @param server Websocket 服务端对象
     */
    public boolean add(WebsocketServerEndpoint server) {
        boolean isSuccess = WebsocketServerEndpointContainer.add(server);
        if (isSuccess) {
            log.info("[Websocket 服务端] - 当前连接数: {}", WebsocketServerEndpointContainer.size());
            return true;
        } else {
            return false;
        }
    }

    /**
     * 删除 Websocket 服务端对象
     *
     * @param server 要删除的 Websocket 服务端对象
     */
    public void remove(WebsocketServerEndpoint server) {
        boolean isRemove = WebsocketServerEndpointContainer.remove(server);
        if (isRemove) {
            log.info("[Websocket 服务端] - 当前连接数: {}", WebsocketServerEndpointContainer.size());
        }
    }

    /**
     * 获取 Websocket 服务端对象
     *
     * @param id Websocket 服务端 ID
     * @return Websocket 服务端对象
     */
    public WebsocketServerEndpoint get(String id) {
        return WebsocketServerEndpointContainer.get(id);
    }

    /**
     * 处理客户端发送来的消息
     */
    public void onMessage(String clientId, Session session, String message) {
        if (MessageCode.KEEP_ALIVE_TAG.equals(message)) {
            // 客户端的心跳包，返回心跳响应
            try {
                session.getBasicRemote().sendText(MessageCode.KEEP_ALIVE_TAG_RESPONSE);
                log.debug("[Websocket 服务端] 发送心跳响应包成功 [ID: " + clientId + "]");
                return;
            } catch (Exception e) {
                log.error("[Websocket 服务端] 发送心跳响应包失败 [ID: " + clientId + "]", e);
                return;
            }
        }

        if (MessageCode.KEEP_ALIVE_TAG_RESPONSE.equals(message)) {
            // 客户端对服务端心跳包的回应，不做处理
            log.debug("[Websocket 服务端] 收到来自客户端的心跳检测响应 [ID: " + clientId + "]");
            return;
        }

        // 转换交换数据
        ExchangeMessage<?> receiveMessage = JSONUtil.parseObject(message, ExchangeMessage.class);
        if (receiveMessage == null) {
            throw new NullPointerException("将数据转换为 [ExchangeMessage] 时发生错误");
        }

        // 实现业务逻辑
        onMessageProcessor.process(clientId, receiveMessage);

        // 将客户端的回报信息添加到容器中，供其他人读取
        ClientResponseContainer.add(receiveMessage);
    }

    /**
     * 发送消息给客户端
     *
     * @param clientId        客户端 ID
     * @param exchangeMessage 要发送的消息
     * @return 发送结果
     */
    public Result send(String clientId, ExchangeMessage<?> exchangeMessage) {
        try {
            if (StringUtils.isEmpty(clientId)) {
                return Result.error();
            }

            // 获取 Websocket 对象
            WebsocketServerEndpoint server = WebsocketServerEndpointContainer.get(clientId);
            if (server == null) {
                // 不存在这个 ID 的客户端
                String tips = "客户端离线/不存在 [ID: " + clientId + "]";
                log.error("[Websocket 服务端] - " + tips);
                return Result.error().setMessage(tips);
            }

            // 自动添加请求 ID
            if (StringUtils.isEmpty(exchangeMessage.getRequestId())) {
                exchangeMessage.setRequestId(UUIDUtil.uuid());
            }

            // 发送数据
            String messageStr = JSONUtil.toJSONString(exchangeMessage);
            server.getSession().getBasicRemote().sendText(messageStr);

            // 返回结果
            Result result = Result.ok();

            // 如果需要客户端响应信息，就需要等待
            if (exchangeMessage.isRequireResponse()) {
                // 需要获取客户端的回复信息
                long startTime = System.currentTimeMillis();

                while (true) {
                    if (System.currentTimeMillis() - startTime <= this.responseTimeout) {
                        // 未超时，重试
                        ExchangeMessage<?> clientResponseMsg = ClientResponseContainer.getByRequestId(exchangeMessage.getRequestId());
                        if (clientResponseMsg == null) {
                            // 没有该客户端的信息，等待 1 秒后重试
                            Thread.sleep(1000);
                        } else {
                            // 获取到客户端的信息，终止
                            result.setData(clientResponseMsg.getBody()); // 设置获取到的信息
                            break;
                        }
                    } else {
                        // 响应超时
                        String tips = "客户端响应超时 [ID: " + clientId + "]";
                        log.error("[Websocket 服务端] - " + tips);
                        return Result.error().setMessage(tips);
                    }
                }
            }

            return result;
        } catch (Exception e) {
            String tips = "客户端请求失败 [ID: " + clientId + "]";
            log.error("[Websocket 服务端] - " + tips, e);
            return Result.error().setMessage(tips);
        }
    }

    /**
     * 每分钟第 15 秒和第 45 秒发送心跳包给客户端
     */
    @Scheduled(cron = "15,45 * * * * ?")
    public void sendKeepAlive() {
        for (WebsocketClientConfig.Config config : websocketClientConfig.getList()) {
            Boolean keepAlive = config.getKeepAlive();
            if (keepAlive != null && keepAlive) {
                String clientId = config.getId();
                WebsocketServerEndpoint endpoint = WebsocketServerEndpointContainer.get(clientId);
                try {
                    if (endpoint != null && endpoint.getSession().isOpen()) {
//                    if (endpoint != null) {
                        endpoint.getSession().getBasicRemote().sendText(MessageCode.KEEP_ALIVE_TAG);
                        log.debug("[Websocket 服务端] 发送心跳检测包成功 - [ID: " + clientId + "]");
                    }
                } catch (Exception e) {
                    log.error("[Websocket 服务端] 发送心跳检测包失败 - [ID: " + clientId + "]", e);
                    WebsocketServerEndpointContainer.remove(endpoint); // 移除这个连接
                }
            }
        }
    }
}
