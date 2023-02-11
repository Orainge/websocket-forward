package com.orainge.websocket_forward.client.processor.impl;

import com.alibaba.fastjson.JSON;
import com.orainge.websocket_forward.client.processor.OnMessageProcessor;
import com.orainge.websocket_forward.client.util.WebsocketClientUtil;
import com.orainge.websocket_forward.vo.ExchangeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DefaultOnMessageProcessor implements OnMessageProcessor {
    @Autowired
    private WebsocketClientUtil websocketClientUtil;

    @Override
    public void process(ExchangeMessage<?> exchangeMessage) {
        log.debug("[Websocket 客户端] 收到服务端请求: " + JSON.toJSONString(exchangeMessage));

        if (exchangeMessage.isRequireResponse()) {
            ExchangeMessage<String> replyMessage = new ExchangeMessage<>();
            replyMessage.setBody("客户端回复了消息");
            websocketClientUtil.replyMessage(exchangeMessage, replyMessage);
        }
    }
}
