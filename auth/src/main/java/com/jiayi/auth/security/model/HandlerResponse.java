package com.jiayi.auth.security.model;

import lombok.Data;
import org.springframework.http.HttpStatus;

/**
 * 处理器响应对象
 *
 * @author laiyilong
 * @date 2022/04/19
 */
@Data
public class HandlerResponse {

    /**
     * 响应http status code  httpStatusCode
     */
    private HttpStatus httpCode;
    /**
     * 响应消息体  responseBody  JSON字符串
     */
    private String message;

    public HandlerResponse() {

    }

    public HandlerResponse(HttpStatus httpCode, String message) {
        this.httpCode = httpCode;
        this.message = message;
    }
}
