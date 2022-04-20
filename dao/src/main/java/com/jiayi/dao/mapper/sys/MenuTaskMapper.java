package com.jiayi.dao.mapper.sys;

import com.jiayi.dao.data.sys.MenuTaskDO;

import java.util.List;

public interface MenuTaskMapper {
    /**
     * 添加菜单权限关系
     *
     * @param record
     * @return
     */
    int insert(MenuTaskDO record);

    /**
     * 添加菜单权限关系
     *
     * @param record
     * @return
     */
    int insertSelective(MenuTaskDO record);

    /**
     * 查询菜单与权限关系
     *
     * @param query
     * @return
     */
    List<MenuTaskDO> listByQuery(MenuTaskDO.Query query);
}