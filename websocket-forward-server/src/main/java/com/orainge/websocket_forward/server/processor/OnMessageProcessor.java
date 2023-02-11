package com.orainge.websocket_forward.server.processor;

import com.orainge.websocket_forward.vo.ExchangeMessage;

public interface OnMessageProcessor {
    void process(String clientId, ExchangeMessage<?> exchangeMessage);
}
