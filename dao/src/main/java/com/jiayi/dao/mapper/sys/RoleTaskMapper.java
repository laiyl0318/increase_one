package com.jiayi.dao.mapper.sys;

import com.jiayi.dao.data.sys.RoleTaskDO;

import java.util.List;

public interface RoleTaskMapper {
    /**
     * 添加角色任务关系
     *
     * @param record
     * @return
     */
    int insertSelective(RoleTaskDO record);

    /**
     * 角色任务关系数据
     *
     * @param query
     * @return
     */
    List<RoleTaskDO> roleTaskList(RoleTaskDO.Query query);

}