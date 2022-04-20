package com.jiayi.auth.security.filter;

import com.jiayi.auth.security.interfaces.BaseCustomerSecurity;
import org.springframework.security.access.AccessDecisionManager;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.access.intercept.FilterSecurityInterceptor;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import java.io.IOException;

/**
 * @author cjw
 * @date 2020/8/9
 */
public class CustomFilterSecurityInterceptor extends FilterSecurityInterceptor {
    private BaseCustomerSecurity baseCustomerSecurity;

    public CustomFilterSecurityInterceptor(BaseCustomerSecurity baseCustomerSecurity, FilterInvocationSecurityMetadataSource securityMetadataSource, AccessDecisionManager accessDecisionManager) {
        this.baseCustomerSecurity = baseCustomerSecurity;
        this.setSecurityMetadataSource(securityMetadataSource);
        this.setAccessDecisionManager(accessDecisionManager);
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        FilterInvocation fi = new FilterInvocation(request, response, chain);
        if (baseCustomerSecurity.matches(fi.getHttpRequest())) {
            this.invoke(fi);
        } else {
            fi.getChain().doFilter(fi.getRequest(), fi.getResponse());
        }
    }
}
