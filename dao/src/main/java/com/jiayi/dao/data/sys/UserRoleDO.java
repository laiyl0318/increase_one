package com.jiayi.dao.data.sys;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class UserRoleDO {
    private Integer id;

    private Integer userId;

    private String roleCode;

    private LocalDateTime createTime;
}