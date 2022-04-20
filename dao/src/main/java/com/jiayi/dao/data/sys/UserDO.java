package com.jiayi.dao.data.sys;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class UserDO {
    private Integer userId;

    private String userName;

    private String password;

    private String userMobile;

    private String nickName;

    private Byte userStatus;

    private Integer orgId;

    private String orgFullCode;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Data
    public static class Query extends UserDO {
        private List<String> roleCodes;

        private List<Integer> userIds;

        private List<String> notInRoleCodes;

        private String searchKey;
    }
}