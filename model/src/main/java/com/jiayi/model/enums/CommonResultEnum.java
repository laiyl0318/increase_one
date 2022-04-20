package com.jiayi.model.enums;

/**
 * 返回码枚举
 *
 * @author cjw
 * @date 2020-10-29
 */
public enum CommonResultEnum {
    /**
     * 调用成功
     */
    SUCCEED(10000, "调用成功"),
    /**
     * 服务拒绝访问
     */
    ACCESS_DENIED(20000, "服务拒绝访问"),
    /**
     * 用户未登录
     */
    ACCESS_DENIED_UNAUTHORIZED(20001, "用户未登录"),
    /**
     * 用户没有权限访问
     */
    ACCESS_DENIED_FORBIDDEN(20002, "用户没有权限访问"),
    /**
     * 操作冲突,稍后再试30s
     */
    REDISLOCK_FAILED(30001, "操作冲突,稍后再试30s"),
    /**
     * 业务处理失败
     */
    BUSINESS_FAILED(30000, "业务处理失败"),
    /**
     * 业务处理失败
     */
    BUSINESS_Exception(40000, "业务处理失败");

    /**
     * 返回码
     */
    private Integer code;

    /**
     * 返回码描述
     */
    private String msg;

    CommonResultEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * @return code
     */
    public Integer getCode() {
        return code;
    }

    /**
     * @return msg
     */
    public String getMsg() {
        return msg;
    }


}
