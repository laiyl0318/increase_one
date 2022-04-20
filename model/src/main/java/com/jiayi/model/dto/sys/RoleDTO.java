package com.jiayi.model.dto.sys;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
public class RoleDTO implements Serializable {
    private static final long serialVersionUID = -5135236125071349590L;
    private Integer roleId;

    private String roleCode;

    private String roleName;

    private String description;

    private String dataAuthority;

    private Integer serialNum;
    /**
     * 状态 1:valid, 0:invalid
     */
    private Byte roleStatus;
    /**
     * 是否隐藏(0:不隐藏;1:隐藏)
     */
    private Boolean hiddenStatus;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;

    @Data
    public static class Query extends RoleDTO {
        private static final long serialVersionUID = 3729344829661156940L;
    }
}