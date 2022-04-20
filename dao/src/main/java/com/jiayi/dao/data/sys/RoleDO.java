package com.jiayi.dao.data.sys;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class RoleDO {
    private Integer roleId;

    private String roleCode;

    private String roleName;

    private String description;

    private String dataAuthority;

    private Integer serialNum;

    private Byte roleStatus;

    private Boolean hiddenStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Data
    public static class Query extends RoleDO {

    }
}