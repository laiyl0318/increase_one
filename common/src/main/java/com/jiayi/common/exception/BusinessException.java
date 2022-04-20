package com.jiayi.common.exception;

/**
 * @author cjw
 * @date 2020/09/05 22:28
 */
public class BusinessException extends RuntimeException {

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(String message, Throwable ex) {
        super(message, ex);
    }

    public BusinessException(Throwable ex) {
        super(ex);
    }
}
