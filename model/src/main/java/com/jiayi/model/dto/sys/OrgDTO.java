package com.jiayi.model.dto.sys;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 组织架构数据
 *
 * @author laiyilong
 */
@Data
public class OrgDTO implements Serializable {
    private static final long serialVersionUID = -5889998691592487394L;
    private Integer orgId;

    private String orgName;

    private String orgCode;

    private String orgFullCode;

    private Integer parentId;

    private Boolean status;

    private Byte level;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


}
