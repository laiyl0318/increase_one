package com.jiayi.model.response.sys;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author cjw
 * @date 2020-10-28
 */
@Data
public class UserInfoResponse implements Serializable {
    private static final long serialVersionUID = -8644898303657872775L;
    /**
     * 加密后的用户id
     */
    private String uId;
    /**
     * 用户名称
     */
    private String name;
    /**
     * 用户手机号
     */
    private String userMobile;
    /**
     * 用户标识:前端用于判断是否超级管理员
     */
    private List<String> roles;
    /**
     * 角色标签, 前端判断用户角色
     */
    private List<String> roleTags;
    /**
     * 当前服务器时间
     */
    private LocalDateTime gtn;
    /**
     * 用户类型
     */
    private Byte userType;
    /**
     * 省份
     */
    private String provinceName;
    /**
     * 城市
     */
    private String cityName;
    /**
     * 区县
     */
    private String countyName;
    /**
     * 用户姓名
     */
    private String realName;

    /**
     * 用户区域code
     */
    private String regionCode;

    /**
     * 头像
     */
    private String avatarUrl;

    public UserInfoResponse() {

    }

    public UserInfoResponse(String uId, String name, List<String> roles, String userMobile) {
        this.uId = uId;
        this.name = name;
        this.roles = roles;
        this.userMobile = userMobile;
    }

    public UserInfoResponse(String uId, String name, List<String> roles) {
        this.uId = uId;
        this.name = name;
        this.roles = roles;
    }

}
