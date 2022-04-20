package com.jiayi.repository.user;

import com.jiayi.dao.data.sys.UserDO;
import com.jiayi.dao.mapper.sys.UserMapper;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

/**
 * @author laiyilong
 */
@Service
public class UserRepository {
    @Resource
    private UserMapper userMapper;

    /**
     * 通过用户登录名查询用户信息
     *
     * @return
     */
    public UserDO getUserByUserName(String userName) {
        UserDO.Query query = new UserDO.Query();
        query.setUserName(userName);
        return userMapper.getUserByQuery(query);
    }


    /**
     * 通过用户手机号查询用户信息
     *
     * @param mobile
     * @return
     */
    public UserDO getUserByMobile(String mobile) {
        UserDO.Query query = new UserDO.Query();
        query.setUserMobile(mobile);
        return userMapper.getUserByQuery(query);
    }

}
