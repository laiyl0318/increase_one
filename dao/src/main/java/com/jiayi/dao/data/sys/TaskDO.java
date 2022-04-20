package com.jiayi.dao.data.sys;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class TaskDO {
    private Integer taskId;

    private String taskCode;

    private String taskName;

    private Boolean taskType;

    private Integer parentId;

    private String description;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}