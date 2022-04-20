package com.jiayi.model.response.sys;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 角色列表输出模型
 *
 * @author wyq
 * @date 2020/10/14
 */
@Data
public class RoleListResponse implements Serializable {

    private static final long serialVersionUID = 1462782623173437349L;

    private String roleId;

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
     * 状态 1:valid, 0:invalid
     */
    private String roleStatusStr;
    /**
     * 是否隐藏(0:不隐藏;1:隐藏)
     */
    private Boolean hiddenStatus;
    /**
     * 是否隐藏(0:不隐藏;1:隐藏)
     */
    private String hiddenStatusStr;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
