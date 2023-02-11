package com.orainge.websocket_forward.server.service.impl;

import com.orainge.websocket_forward.server.service.WebSocketService;
import com.orainge.websocket_forward.server.util.WebsocketServerUtil;
import com.orainge.websocket_forward.vo.ExchangeMessage;
import com.orainge.websocket_forward.vo.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class WebsocketServiceImpl implements WebSocketService {
    @Autowired
    private WebsocketServerUtil websocketServerUtil;

    public Result send(String clientId, String text) {
        ExchangeMessage<String> exchangeMessage = new ExchangeMessage<>();
        exchangeMessage.setBody(text);

        return websocketServerUtil.send(clientId, exchangeMessage);
    }

    public Result send(String clientId, String text, Boolean requireReply) {
        ExchangeMessage<String> exchangeMessage = new ExchangeMessage<>();
        exchangeMessage.setRequireResponse(requireReply);
        exchangeMessage.setBody(text);

        return websocketServerUtil.send(clientId, exchangeMessage);
    }
}
