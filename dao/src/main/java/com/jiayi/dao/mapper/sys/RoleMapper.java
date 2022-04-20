package com.jiayi.dao.mapper.sys;

import com.jiayi.dao.data.sys.RoleDO;

import java.util.List;

public interface RoleMapper {
    /**
     * 添加角色
     *
     * @param record
     * @return
     */
    int insertSelective(RoleDO record);

    /**
     * 查询角色
     *
     * @param roleId
     * @return
     */
    RoleDO selectByPrimaryKey(Integer roleId);

    /**
     * 角色列表
     *
     * @param query
     * @return
     */
    List<RoleDO> roleList(RoleDO.Query query);
}