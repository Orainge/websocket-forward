package com.orainge.websocket_forward.consts;

/**
 * Websocket 常量
 */
public class MessageCode {
    /**
     * 用于保活的文本消息
     */
    public static final String KEEP_ALIVE_TAG = "ping";

    /**
     * 回应保活的文本消息
     */
    public static final String KEEP_ALIVE_TAG_RESPONSE = "pong";

    /**
     * 已有相同ID的客户端连接到服务器
     */
    public static final String DUPLICATE_CLIENT_TAG = "duplicate";
}
