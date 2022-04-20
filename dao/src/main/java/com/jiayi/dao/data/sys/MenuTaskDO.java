package com.jiayi.dao.data.sys;

import lombok.Data;

import java.util.List;

@Data
public class MenuTaskDO {
    private Integer id;

    private String menuCode;

    private String taskCode;

    @Data
    public static class Query extends MenuTaskDO {
        private List<String> taskCodes;
    }
}