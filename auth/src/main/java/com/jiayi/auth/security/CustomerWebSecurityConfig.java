package com.jiayi.auth.security;

import com.google.common.collect.Lists;
import com.jiayi.auth.security.component.CustomAccessDecisionManager;
import com.jiayi.auth.security.component.CustomAccessDecisionVoter;
import com.jiayi.auth.security.component.CustomSecurityMetadataSource;
import com.jiayi.auth.security.component.CustomUserDetailServiceImpl;
import com.jiayi.auth.security.filter.CustomAuthenticationFilter;
import com.jiayi.auth.security.filter.CustomBasicAuthenticationFilter;
import com.jiayi.auth.security.filter.CustomExceptionTranslationFilter;
import com.jiayi.auth.security.filter.CustomFilterSecurityInterceptor;
import com.jiayi.auth.security.handler.CustomAccessDeniedHandler;
import com.jiayi.auth.security.handler.CustomLoginHandler;
import com.jiayi.auth.security.handler.CustomLogoutHandler;
import com.jiayi.auth.security.interfaces.BaseCustomerSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.ExceptionTranslationFilter;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.annotation.Resource;
import java.util.List;

/**
 * SpringSecurity配置类
 * @author laiyilong
 */
@EnableWebSecurity
@Slf4j
public class CustomerWebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Resource
    private CustomerSecurityFilters customerSecurityFilters;

    @Override
    public void configure(WebSecurity web) throws Exception {
        super.configure(web);
        web.ignoring().antMatchers(customerSecurityFilters.ignorePath());
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        // 定义spring security 不使用会话，并且忽略所以会话（比如application创建的会话）
        http.sessionManagement().disable();
        // 关闭csrf验证
        http.csrf().disable();
        // 定义登录表单处理器
        defineCustomFilters(http);
    }

    /**
     * 自定义验证器
     */
    private void defineCustomFilters(HttpSecurity http) throws Exception {
        for (BaseCustomerSecurity baseCustomerSecurity : customerSecurityFilters.customerSecurityList()) {
            // 自定义登录验证器
            http.addFilterBefore(customAuthenticationFilter(baseCustomerSecurity), UsernamePasswordAuthenticationFilter.class);
            // 自定义退出处理过滤器
            CustomLogoutHandler customLogoutHandler = customLogoutHandler(baseCustomerSecurity);
            LogoutFilter customLogoutFilter = new LogoutFilter(customLogoutHandler, customLogoutHandler);
            customLogoutFilter.setFilterProcessesUrl(baseCustomerSecurity.logoutUrl());
            http.addFilterBefore(customLogoutFilter, LogoutFilter.class);
            // 访问拒绝处理器
            CustomAccessDeniedHandler customAccessDeniedHandler = customAccessDeniedHandler(baseCustomerSecurity);
            // 定义访问拒绝的处理方式 （1，登录后没有权限拒绝； 2，未登录没有权限拒绝）
            http.addFilterAfter(new CustomExceptionTranslationFilter(baseCustomerSecurity, customAccessDeniedHandler, customAccessDeniedHandler), ExceptionTranslationFilter.class);
            // 定义登录后，授权token认证过滤器（接口token, 生成 SecurityToken并传递）
            http.addFilterBefore(customBasicAuthenticationFilter(baseCustomerSecurity, customAccessDeniedHandler), BasicAuthenticationFilter.class);
            // 自定义权限数据源  与  权限认证管理器
            http.addFilterBefore(customSecurityInterceptor(baseCustomerSecurity), FilterSecurityInterceptor.class);
        }
    }

    /**
     * 登录认证 过滤器 filter
     *
     * @return
     * @throws Exception
     */
    private CustomAuthenticationFilter customAuthenticationFilter(BaseCustomerSecurity customerSecurity) throws Exception {
        // 实例化过滤器 构造函数传递property
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter(customerSecurity);
        // 定义登录认证接口地址
        customAuthenticationFilter.setFilterProcessesUrl(customerSecurity.loginUrl());
        // 定义认证
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(customUserDetailService(customerSecurity));
        provider.setHideUserNotFoundExceptions(false);
        provider.setPasswordEncoder(customerSecurity.passwordEncoderBuilder());
        ProviderManager authManager = new ProviderManager(Lists.newArrayList(provider));
        customAuthenticationFilter.setAuthenticationManager(authManager);
        // 实例化登录处理器
        CustomLoginHandler loginHandle = new CustomLoginHandler(customerSecurity);
        // 定义登录认证失败处理器
        customAuthenticationFilter.setAuthenticationFailureHandler(loginHandle);
        // 定义登录成功后的处理器
        customAuthenticationFilter.setAuthenticationSuccessHandler(loginHandle);
        return customAuthenticationFilter;
    }

    /**
     * 自定义用户service
     *
     * @return
     */
    private CustomUserDetailServiceImpl customUserDetailService(BaseCustomerSecurity baseCustomerSecurity) {
        CustomUserDetailServiceImpl customUserDetailService = new CustomUserDetailServiceImpl(baseCustomerSecurity);
        return customUserDetailService;
    }

    /**
     * 登出处理器 与登出成功处理器
     *
     * @return
     */
    private CustomLogoutHandler customLogoutHandler(BaseCustomerSecurity baseCustomerSecurity) {
        return new CustomLogoutHandler(baseCustomerSecurity);
    }

    /**
     * 自定义访问拒绝处理器
     *
     * @return
     */
    private CustomAccessDeniedHandler customAccessDeniedHandler(BaseCustomerSecurity baseCustomerSecurity) {
        // 定义访问拒绝处理器
        CustomAccessDeniedHandler accessDeniedHandler = new CustomAccessDeniedHandler(baseCustomerSecurity);
        return accessDeniedHandler;
    }

    /**
     * 定义权限过滤器
     *
     * @param baseCustomerSecurity
     * @return
     */
    private FilterSecurityInterceptor customSecurityInterceptor(BaseCustomerSecurity baseCustomerSecurity) {
        CustomFilterSecurityInterceptor customFilterSecurityInterceptor =
                new CustomFilterSecurityInterceptor(baseCustomerSecurity, customSecurityMetadataSource(baseCustomerSecurity),
                        customAccessDecisionManager(baseCustomerSecurity));
        return customFilterSecurityInterceptor;
    }

    /**
     * 自定义授权filter
     */
    private CustomBasicAuthenticationFilter customBasicAuthenticationFilter(BaseCustomerSecurity baseCustomerSecurity, AuthenticationEntryPoint authenticationEntryPoint) {
        return new CustomBasicAuthenticationFilter(baseCustomerSecurity, authenticationEntryPoint);
    }

    /**
     * 自定义权限数据源
     */
    private CustomSecurityMetadataSource customSecurityMetadataSource(BaseCustomerSecurity baseCustomerSecurity) {
        return new CustomSecurityMetadataSource(baseCustomerSecurity);
    }


    /**
     * 自定义权限认证管理器,判断用户角色是否有权限访问（所有认证器验证通过后，表示有权限）
     *
     * @return
     */
    private CustomAccessDecisionManager customAccessDecisionManager(BaseCustomerSecurity baseCustomerSecurity) {
        List<AccessDecisionVoter<?>> list = Lists.newArrayList();
        // 添加认证器
        list.add(new CustomAccessDecisionVoter());
        // 添加自定义认证器
        List<AccessDecisionVoter<?>> customVoters = baseCustomerSecurity.customAccessDecisionVoter();
        if (customVoters != null && !customVoters.isEmpty()) {
            list.addAll(customVoters);
        }
        CustomAccessDecisionManager decisionManager = new CustomAccessDecisionManager(list);
        return decisionManager;
    }
}
