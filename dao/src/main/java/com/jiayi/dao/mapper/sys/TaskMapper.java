package com.jiayi.dao.mapper.sys;

import com.jiayi.dao.data.sys.TaskDO;

public interface TaskMapper {
    int insert(TaskDO record);

    int insertSelective(TaskDO record);

    TaskDO selectByPrimaryKey(Integer taskId);
}