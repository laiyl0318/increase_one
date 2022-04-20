package com.jiayi.auth.security.interfaces;

import com.google.common.collect.Lists;
import com.jiayi.auth.security.model.CustomerSecurityConfigAttributes;
import com.jiayi.auth.security.model.HandlerResponse;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.util.Assert;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;

/**
 * security接口实现
 * @author laiyilong
 * @date 2022/4/19
 */
public abstract class BaseCustomerSecurity implements CustomerSecurityHandler,CustomerSecurityProperty,CustomerSecurityService{
    /**
     * 过滤器匹配url
     */
    private List<RequestMatcher> requestMatchers;

    public BaseCustomerSecurity(String... filterPattern) {
        Assert.notNull(filterPattern, "过滤器匹配url需要定义");
        requestMatchers = Lists.newArrayList();
        Arrays.stream(filterPattern).forEach(path -> {
            requestMatchers.add(new AntPathRequestMatcher(path));
        });
    }
    /**
     * 验证是否匹配当前uri
     *
     * @return
     */
    public boolean matches(HttpServletRequest request) {
        return requestMatchers.stream().anyMatch(requestMatcher -> requestMatcher.matches(request));
    }

    @Override
    public HandlerResponse loginFailureHandler(HttpServletRequest request, AuthenticationException exception) {
        return null;
    }

    @Override
    public HandlerResponse loginSuccessHandler(HttpServletRequest request, Authentication authentication) {
        return null;
    }

    @Override
    public HandlerResponse noLoginDeniedHandler(AuthenticationException authException) {
        return null;
    }

    @Override
    public HandlerResponse haveLoginDeniedHandler(AccessDeniedException accessDeniedException) {
        return null;
    }

    @Override
    public HandlerResponse logoutSuccessHandler(Authentication authentication) {
        return null;
    }

    @Override
    public void logoutHandler(HttpServletRequest request, Authentication authentication) {

    }

    /**
     * 登录处理接口地址
     *
     * @return
     */
    @Override
    public String loginUrl() {
        return "/login";
    }

    /**
     * 登出处理接口地址
     *
     * @return
     */
    @Override
    public String logoutUrl() {
        return "/logout";
    }

    /**
     * 登录表单账号字段key
     */
    @Override
    public String userNameKey() {
        return "username";
    }

    /**
     * 登录表单密码字段key
     */
    @Override
    public String passWordKey() {
        return "password";
    }

    /**
     * 识别用户登录的token key
     *
     * @return
     */
    @Override
    public String loginToken() {
        return "Authorization";
    }

    @Override
    public String verificationKey() {
        return "verification";
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
