package com.jiayi.model.response.sys;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户数据响应对象
 *
 * @author cjw
 * @date 2020-10-18
 */
@Data
public class UserResponse implements Serializable {

    private static final long serialVersionUID = 955871387506474668L;
    private String userId;

    private Byte userType;

    private String gender;

    private String available;

    private String userName;

    private String realName;

    private String userMobile;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private String email;

    private String orgId;

    private String orgName;

    private String provinceCode;

    private String cityCode;

    private String countyCode;

    private String provinceName;

    private String cityName;

    private String countyName;

    private String userTypeStr;

    private Byte userStatus;

    private String userStatusStr;

    private List<String> orgFullName;

    /**
     * 角色code数据
     */
    private List<String> roleCodes;

    private List<String> roleCodesShow;

    /**
     * 是否已绑定微信用户：1是，2否
     */
    private Byte hasBoundWechat;
}
