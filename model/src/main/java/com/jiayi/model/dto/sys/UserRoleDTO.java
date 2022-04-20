package com.jiayi.model.dto.sys;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * @author cjw
 */
@Data
public class UserRoleDTO implements Serializable {
    private static final long serialVersionUID = -7160106930790914089L;
    /**
     * Database Column Remarks:
     * 主键id
     */
    private Integer id;

    /**
     * Database Column Remarks:
     * 用户id
     */
    private Integer userId;

    /**
     * Database Column Remarks:
     * 角色id
     */
    private String roleCode;

    /**
     * Database Column Remarks:
     * 创建时间
     */
    private LocalDateTime createTime;

}