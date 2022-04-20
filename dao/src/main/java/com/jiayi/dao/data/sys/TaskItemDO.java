package com.jiayi.dao.data.sys;

import lombok.Data;

import java.util.List;

@Data
public class TaskItemDO {
    private Integer taskItemId;

    private String taskCode;

    private String itemCode;

    @Data
    public static class Query extends TaskItemDO {
        private List<String> taskCodes;

        private List<String> itemCodes;
    }
}