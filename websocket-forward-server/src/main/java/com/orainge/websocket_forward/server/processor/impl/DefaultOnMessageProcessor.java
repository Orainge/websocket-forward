package com.orainge.websocket_forward.server.processor.impl;

import com.alibaba.fastjson.JSON;
import com.orainge.websocket_forward.server.processor.OnMessageProcessor;
import com.orainge.websocket_forward.server.util.WebsocketServerUtil;
import com.orainge.websocket_forward.vo.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultOnMessageProcessor implements OnMessageProcessor {
    @Autowired
    private WebsocketServerUtil websocketServerUtil;

    @Override
    public void process(String clientId, ExchangeMessage<?> exchangeMessage) {
        log.info("[Websocket 服务端] 收到客户端请求: " + JSON.toJSONString(exchangeMessage));

        if (exchangeMessage.isRequireResponse()) {
            ExchangeMessage<String> replyMessage = new ExchangeMessage<>();
            replyMessage.setBody("服务端回复了消息");
            websocketServerUtil.send(clientId, replyMessage);
        }
    }
}
