package com.jiayi.model.support.response;

import lombok.Data;

import java.io.Serializable;

/**
 * 通用响应数据对象
 *
 * @author laiyilong
 */
@Data
public class SysResponse implements Serializable {
    private static final long serialVersionUID = 8975282429479756153L;
    /**
     * 响应状态码
     */
    private int code;
    /**
     * 响应状态子码
     */
    private String subCode;
    /**
     * 响应消息
     */
    private Object msg;
    /**
     * 子响应消息
     */
    private Object subMsg;
    /**
     * 响应数据
     */
    private Object response;
    /**
     * 响应时间
     */
    private long timeout;
    public SysResponse(int code, String subCode, Object msg, Object subMsg, Object response) {
        this.code = code;
        this.subCode = subCode;
        this.msg = msg;
        this.subMsg = subMsg;
        this.response = response;
    }
    public SysResponse(int code, Object msg, Object response) {
        this.code = code;
        this.msg = msg;
        this.response = response;
    }
}
