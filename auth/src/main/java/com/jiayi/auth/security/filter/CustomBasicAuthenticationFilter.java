package com.jiayi.auth.security.filter;

import com.google.common.base.Strings;
import com.jiayi.auth.security.interfaces.BaseCustomerSecurity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author cjw
 * @date 2020/7/20
 */
@Slf4j
public class CustomBasicAuthenticationFilter extends GenericFilterBean {

    private BaseCustomerSecurity baseCustomerSecurity;

    private AuthenticationEntryPoint authenticationEntryPoint;

    public CustomBasicAuthenticationFilter(BaseCustomerSecurity baseCustomerSecurity, AuthenticationEntryPoint authenticationEntryPoint) {
        this.baseCustomerSecurity = baseCustomerSecurity;
        this.authenticationEntryPoint = authenticationEntryPoint;
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        if (baseCustomerSecurity.matches(request)) {
            try {

                String token = baseCustomerSecurity.parseLoginToken(request, baseCustomerSecurity.loginToken());
                if (!Strings.isNullOrEmpty(token)) {
                    Authentication auth = baseCustomerSecurity.loadUserInfoByToken(request, token);
                    if (auth == null) {
                        throw new UsernameNotFoundException("用户未登录");
                    }
                    SecurityContextHolder.getContext().setAuthentication(auth);
                } else {
                    throw new UsernameNotFoundException("用户未登录");
                }
            } catch (AuthenticationException ex) {
                this.authenticationEntryPoint.commence(request, response, ex);
                return;
            }
        }
        filterChain.doFilter(servletRequest, servletResponse);
    }
}