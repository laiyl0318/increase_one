package com.jiayi.auth.security.interfaces;

import com.jiayi.auth.security.model.CustomerSecurityConfigAttributes;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 权限认证相关处理数据接口
 *
 * @author laiyilong
 */
public interface CustomerSecurityService {

    /**
     * 是否使用验证码验证（自定义判断逻辑，来定义是否使用验证码）
     *
     * @param request 请求消息
     * @return
     */
    Boolean useVerification(HttpServletRequest request);

    /**
     * 验证表单验证码是否正确（与自定义保存的验证码进行比对）
     *
     * @param request          请求消息
     * @param formVerification 表单验证码
     * @return
     */
    Boolean checkVerification(HttpServletRequest request, String formVerification);

    /**
     * 账号密码加密、验证器
     *
     * @return
     */
    PasswordEncoder passwordEncoderBuilder();

    /**
     * 通过账号查询账号信息，并拼接成UserDetails
     *
     * @param userName
     * @return
     * @throws UsernameNotFoundException
     */
    UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException;

    /**
     * 权限认证器，默认返回空即可
     *
     * @return
     */
    List<AccessDecisionVoter<?>> customAccessDecisionVoter();

    /**
     * 权限数据源
     *
     * @return
     */
    List<CustomerSecurityConfigAttributes> loadPermissionData();

    /**
     * 通过用户token 获取用户信息（识别用户登录）
     *
     * @param request
     * @param token
     * @return
     */
    Authentication loadUserInfoByToken(ServletRequest request, String token);

    /**
     * 从request 中解析 token值
     *
     * @param request
     * @param tokenHeader
     * @return
     */
    String parseLoginToken(HttpServletRequest request, String tokenHeader);

}
