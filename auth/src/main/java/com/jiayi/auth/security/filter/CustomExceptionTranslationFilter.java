package com.jiayi.auth.security.filter;


import com.jiayi.auth.security.interfaces.BaseCustomerSecurity;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.access.ExceptionTranslationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

/**
 * @author cjw
 * @date 2020/8/9
 */
public class CustomExceptionTranslationFilter extends ExceptionTranslationFilter {
    private BaseCustomerSecurity baseCustomerSecurity;

    public CustomExceptionTranslationFilter(BaseCustomerSecurity baseCustomerSecurity, AuthenticationEntryPoint authenticationEntryPoint, AccessDeniedHandler accessDeniedHandler) {
        super(authenticationEntryPoint);
        super.setAccessDeniedHandler(accessDeniedHandler);
        this.baseCustomerSecurity = baseCustomerSecurity;
    }

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {
        if (baseCustomerSecurity.matches((HttpServletRequest) req)) {
            super.doFilter(req, res, chain);
        } else {
            chain.doFilter(req, res);
        }
    }
}
