package com.jiayi.auth.security.interfaces;

import com.jiayi.auth.security.model.HandlerResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import javax.servlet.http.HttpServletRequest;

/**
 * 自定义处理器接口
 * @author Admin
 */
public interface CustomerSecurityHandler {
    /**
     * 用户登录失败处理器
     *
     * @param request   请求对象
     * @param exception 验证异常
     * @return
     */
    HandlerResponse loginFailureHandler(HttpServletRequest request, AuthenticationException exception);

    /**
     * #####################################################################
     * 用户登录成功处理器(如使用redis保存用户信息，在此处添加)
     *
     * @param request        请求对象
     * @param authentication 认证对象
     * @return
     */
    HandlerResponse loginSuccessHandler(HttpServletRequest request, Authentication authentication);

    /**
     * 用户未登录无访问权限处理器
     *
     * @param authException 认证异常
     * @return
     */
    HandlerResponse noLoginDeniedHandler(AuthenticationException authException);

    /**
     * 用户已登录无访问权限处理器
     *
     * @param accessDeniedException 认证权限异常
     * @return
     */
    HandlerResponse haveLoginDeniedHandler(AccessDeniedException accessDeniedException);

    /**
     * 用户登出成功处理器
     *
     * @param authentication
     * @return
     */
    HandlerResponse logoutSuccessHandler(Authentication authentication);

    /**
     * ##############################################
     * 用户登出操作处理器
     *
     * @param request
     * @param authentication
     * @return
     */
    void logoutHandler(HttpServletRequest request, Authentication authentication);
}
