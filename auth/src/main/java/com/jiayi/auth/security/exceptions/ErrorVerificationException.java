package com.jiayi.auth.security.exceptions;

import org.springframework.security.core.AuthenticationException;

/**
 * 验证码错误异常类
 *
 * @author laiyilong
 * @date 2022-04-19
 */
public class ErrorVerificationException extends AuthenticationException {
    public ErrorVerificationException(String msg, Throwable t) {
        super(msg, t);
    }

    public ErrorVerificationException(String msg) {
        super(msg);
    }
}
