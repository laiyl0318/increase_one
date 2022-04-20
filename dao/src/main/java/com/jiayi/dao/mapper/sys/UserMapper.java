package com.jiayi.dao.mapper.sys;

import com.jiayi.dao.data.sys.UserDO;

import java.util.List;

public interface UserMapper {
    int insert(UserDO record);

    UserDO selectByPrimaryKey(Integer userId);

    /**
     * 通过查询条件获得单条用户数据
     *
     * @param query
     * @return
     */
    UserDO getUserByQuery(UserDO.Query query);

    /**
     * 保存用户数据
     *
     * @param userDO
     * @return
     */
    int saveUser(UserDO userDO);

    /**
     * 用户数据列表
     *
     * @param query
     * @return
     */
    List<UserDO> userList(UserDO.Query query);

    /**
     * 编辑用户
     *
     * @param userDO
     * @return
     */
    int editUser(UserDO userDO);
}