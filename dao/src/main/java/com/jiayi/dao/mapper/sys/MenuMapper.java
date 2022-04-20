package com.jiayi.dao.mapper.sys;

import com.jiayi.dao.data.sys.MenuDO;

import java.util.List;

public interface MenuMapper {

    /**
     * 添加菜单
     *
     * @param record
     * @return
     */
    int insertSelective(MenuDO record);

    /**
     * 查询单条菜单
     *
     * @param menuId
     * @return
     */
    MenuDO selectByPrimaryKey(Integer menuId);

    /**
     * 菜单列表
     *
     * @param query
     * @return
     */
    List<MenuDO> listMenu(MenuDO.Query query);
}