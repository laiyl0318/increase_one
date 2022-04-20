package com.jiayi.model.dto.sys;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @author cjw
 * @date 2020-10-18
 */
@Data
public class UserDTO implements Serializable {
    private static final long serialVersionUID = -2009172861355035338L;

    private Integer userId;

    private Byte userStatus;

    private String userName;

    private String password;

    private String nickName;

    private String confirmPassword;

    private String userMobile;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    private Integer orgId;

    private String orgFullCode;

    /**
     * 角色code数据
     */
    private List<String> roleCodes;

    /**
     * 标识用户登录平台来源 web  wechat
     */
    private String platform;


    /**
     * 用户角色下权限项, 用于判断权限。
     */
    private List<String> taskCodes;



    @Data
    public static class Query extends UserDTO {
        private static final long serialVersionUID = 1146926327745990946L;

        private String orgFullCodeLike;

        private List<String> notInRoleCodes;

        private String searchKey;

    }

}
