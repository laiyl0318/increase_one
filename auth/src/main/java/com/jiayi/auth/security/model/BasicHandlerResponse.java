package com.jiayi.auth.security.model;

import org.springframework.http.HttpStatus;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * 处理handlerResponse 到 HttpServletResponse 中的处理类
 *
 * @author cjw
 * @date 2020/7/20
 */
public class BasicHandlerResponse {
    public void handleResponse(HttpServletRequest request, HttpServletResponse response, HandlerResponse handlerResponse) throws IOException {
        if (handlerResponse == null) {
            handlerResponse = new HandlerResponse();
        }
        // 定义处理器响应对象状态默认值
        if (handlerResponse.getHttpCode() == null) {
            handlerResponse.setHttpCode(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        // 定义处理器响应对象 消息体
        if (handlerResponse.getMessage() == null) {
            handlerResponse.setMessage("{\"message\":\"服务处理异常\"}");
        }
        // 定义响应状态
        response.setStatus(handlerResponse.getHttpCode().value());
        response.setHeader("Content-Type", "application/json;charset=utf-8");
        // 定义响应
        PrintWriter out = response.getWriter();
        out.println(handlerResponse.getMessage());
        out.flush();
        out.close();
    }
}
