package com.jiayi.common.model;

import lombok.Data;

import java.io.Serializable;

/**
 * 业务逻辑返回对象
 *
 * @author laiyilong
 */
@Data
public class CallResult<T> implements Serializable {
    public static final int CODE_FAILURE = -1;
    public static final int CODE_SUCCESS = 1;
    private static final long serialVersionUID = -4361282531440085439L;
    private final boolean success;
    private final int code;
    private final String msg;
    private final T resultObject;

    public CallResult(boolean isSuccess, int code, String msg, T resultObject) {
        this.success = isSuccess;
        this.code = code;
        this.msg = msg;
        this.resultObject = resultObject;
    }

    public static <T> CallResult<T> success() {
        return new CallResult(true, 1, "default success", (Object) null);
    }

    public static <T> CallResult<T> success(T resultObject) {
        return new CallResult(true, 1, "default success", resultObject);
    }

    public static <T> CallResult<T> success(int code, String msg, T resultObject) {
        return new CallResult(true, code, msg, resultObject);
    }

    public static <T> CallResult<T> failure() {
        return new CallResult(false, -1, "default failure", (Object) null);
    }

    public static <T> CallResult<T> failure(String msg) {
        return new CallResult(false, -1, msg, (Object) null);
    }

    public static <T> CallResult<T> failure(int code, String msg) {
        return new CallResult(false, code, msg, (Object) null);
    }

    public boolean hasData() {
        return this.resultObject != null;
    }

    public boolean isSuccess() {
        return this.success;
    }
}