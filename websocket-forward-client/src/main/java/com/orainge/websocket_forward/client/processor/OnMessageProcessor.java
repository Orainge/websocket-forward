package com.orainge.websocket_forward.client.processor;

import com.orainge.websocket_forward.vo.ExchangeMessage;

public interface OnMessageProcessor {
    void process(ExchangeMessage<?> exchangeMessage);
}
