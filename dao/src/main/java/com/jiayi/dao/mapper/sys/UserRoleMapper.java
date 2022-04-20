package com.jiayi.dao.mapper.sys;

import com.jiayi.dao.data.sys.UserRoleDO;

import java.util.List;

public interface UserRoleMapper {
    int insert(UserRoleDO record);

    int insertSelective(UserRoleDO record);

    UserRoleDO selectByPrimaryKey(Integer id);

    /**
     * 批量保存数据
     *
     * @param userRoleList
     * @return
     */
    int batchSave(List<UserRoleDO> userRoleList);

    /**
     * 用户-  角色数据
     *
     * @param userId
     * @return
     */
    List<UserRoleDO> userRoleList(Integer userId);

    /**
     * 查询多个用户角色数据
     *
     * @param userIds
     * @return
     */
    List<UserRoleDO> usersRoleList(List<Integer> userIds);

    /**
     * 清除用户角色数据
     *
     * @param userId
     * @return
     */
    int cleanUserRole(Integer userId);


}