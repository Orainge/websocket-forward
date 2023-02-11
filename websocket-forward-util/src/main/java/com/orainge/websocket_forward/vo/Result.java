package com.orainge.websocket_forward.vo;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.springframework.http.HttpStatus;

/**
 * 返回结果类
 *
 * @author orainge
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Result {
    private Integer code;
    private String message;
    private Object data;

    public static Result build() {
        return new Result();
    }

    public static Result build(HttpStatus status) {
        Result result = new Result();
        result.setCode(status.value());
        result.setMessage(status.getReasonPhrase());
        return result;
    }

    public static Result ok() {
        return build(HttpStatus.OK);
    }

    public static Result error() {
        return build(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    public static Result forbidden() {
        return build(HttpStatus.FORBIDDEN);
    }

    public static Result notFound() {
        return build(HttpStatus.NOT_FOUND);
    }

    public Integer getCode() {
        return code;
    }

    public Result setCode(Integer code) {
        this.code = code;
        return this;
    }

    public Boolean isOk() {
        return this.getCode() == HttpStatus.OK.value();
    }

    public Boolean isError() {
        return this.getCode() == HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

    public String getMessage() {
        return message;
    }

    public Result setMessage(String message) {
        this.message = message;
        return this;
    }

    public Object getData() {
        return data;
    }

    public Result setData(Object data) {
        this.data = data;
        return this;
    }

    @Override
    public String toString() {
        return "{" +
                "code: " + code +
                ", message: \"" + message + '\"' +
                ", data: \"" + data + '\"' +
                '}';
    }
}