package com.jiayi.dao.data.sys;

import lombok.Data;

import java.util.List;

@Data
public class RoleTaskDO {
    private Integer roleTaskId;

    private String roleCode;

    private String taskCode;

    @Data
    public static class Query extends RoleTaskDO {
        private List<String> roleCodes;

        private List<String> taskCodes;
    }
}