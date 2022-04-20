package com.jiayi.dao.data.sys;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrgDO {
    private Integer orgId;

    private String orgName;

    private String orgCode;

    private String orgFullCode;

    private Integer parentId;

    private Boolean status;

    private Byte level;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;


    @Data
    public static class Query extends OrgDO {

    }
}