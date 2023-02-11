package com.orainge.websocket_forward.server.websocket;

import com.orainge.websocket_forward.vo.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 存放 Websocket 服务端对象的容器
 *
 * @author orainge
 */
@Slf4j
public class ClientResponseContainer {
    /**
     * 所有 Websocket 发送报文后需要回报文集合对象<br>
     * key: 如果 responseId 不为空，则为 responseId，否则为 requestId
     */
    private static final ConcurrentHashMap<String, ExchangeMessage<?>> CALLBACK_MAP = new ConcurrentHashMap<>();

    /**
     * 添加消息对象
     */
    public static void add(ExchangeMessage<?> message) {
        // 如果存在 responseId，则该消息是 responseId 的回复
        if (!StringUtils.isEmpty(message.getResponseId())) {
            CALLBACK_MAP.put(message.getResponseId(), message);
        } else {
            CALLBACK_MAP.put(message.getRequestId(), message);
        }
    }

    /**
     * 删除消息对象
     */
    public static void remove(ExchangeMessage<?> message) {
        CALLBACK_MAP.remove(message.getResponseId());
        CALLBACK_MAP.remove(message.getRequestId());
    }

    /**
     * 获取消息对象
     *
     * @param id requestId / responseId
     * @return 消息对象
     */
    public static ExchangeMessage<?> get(String id) {
        // 获取消息对象时就移除，确保对象不会堆积过多
        ExchangeMessage<?> message = CALLBACK_MAP.get(id); // 获取消息对象
        CALLBACK_MAP.remove(id); // 删除消息对象
        return message;
    }

    /**
     * 根据 requestId 获取客户端的回复消息
     *
     * @param requestId requestId
     */
    public static ExchangeMessage<?> getByRequestId(String requestId) {
        for (Map.Entry<String, ExchangeMessage<?>> entry : CALLBACK_MAP.entrySet()) {
            ExchangeMessage<?> message = entry.getValue();
            if (message.getRequestId().equals(requestId)) {
                // 删除消息
                CALLBACK_MAP.remove(entry.getKey());
                return message;
            }
        }
        return null;
    }

    /**
     * 根据 responseId 获取客户端的回复消息
     *
     * @param responseId responseId
     */
    public static ExchangeMessage<?> getByResponseId(String responseId) {
        for (Map.Entry<String, ExchangeMessage<?>> entry : CALLBACK_MAP.entrySet()) {
            ExchangeMessage<?> message = entry.getValue();
            if (message.getResponseId().equals(responseId)) {
                // 删除消息
                CALLBACK_MAP.remove(entry.getKey());
                return message;
            }
        }
        return null;
    }
}

