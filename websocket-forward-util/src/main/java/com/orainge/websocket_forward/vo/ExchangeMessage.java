package com.orainge.websocket_forward.vo;

/**
 * 客户端服务端交换信息类
 */
public class ExchangeMessage<T> {
    /**
     * 请求 ID
     */
    private String requestId;

    /**
     * 响应报文 ID<br>
     * 如果该消息是另外一条消息的响应<br>
     * 该请求为客户端响应服务端的请求
     */
    private String responseId;

    /**
     * 动态验证码<br>
     * 该参数必填
     */
    private String authCode;

    /**
     * 是否要求回复
     */
    private boolean requireResponse = false;

    /**
     * 交换的信息
     */
    private T body = null;

    public String getRequestId() {
        return requestId;
    }

    public ExchangeMessage<T> setRequestId(String requestId) {
        this.requestId = requestId;
        return this;
    }

    public String getResponseId() {
        return responseId;
    }

    public ExchangeMessage<T> setResponseId(String responseId) {
        this.responseId = responseId;
        return this;
    }

    public boolean isRequireResponse() {
        return requireResponse;
    }

    public ExchangeMessage<T> setRequireResponse(boolean requireResponse) {
        this.requireResponse = requireResponse;
        return this;
    }

    public String getAuthCode() {
        return authCode;
    }

    public ExchangeMessage<T> setAuthCode(String authCode) {
        this.authCode = authCode;
        return this;
    }

    public T getBody() {
        return body;
    }

    public ExchangeMessage<T> setBody(T body) {
        this.body = body;
        return this;
    }
}
