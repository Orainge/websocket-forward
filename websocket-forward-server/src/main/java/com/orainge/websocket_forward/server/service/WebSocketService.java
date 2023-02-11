package com.orainge.websocket_forward.server.service;

import com.orainge.websocket_forward.vo.Result;

public interface WebSocketService {
    Result send(String clientId, String text);

    Result send(String clientId, String text, Boolean requireReply);
}
