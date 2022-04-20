package com.jiayi.dao.mapper.sys;

import com.jiayi.dao.data.sys.TaskItemDO;

import java.util.List;

public interface TaskItemMapper {
    int insert(TaskItemDO record);

    int insertSelective(TaskItemDO record);

    TaskItemDO selectByPrimaryKey(Integer taskItemId);

    /**
     * 任务权限关系数据
     *
     * @param query
     * @return
     */
    List<TaskItemDO> taskItemList(TaskItemDO.Query query);
}