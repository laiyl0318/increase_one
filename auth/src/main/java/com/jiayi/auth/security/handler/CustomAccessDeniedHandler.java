package com.jiayi.auth.security.handler;

import com.jiayi.auth.security.interfaces.BaseCustomerSecurity;
import com.jiayi.auth.security.model.BasicHandlerResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author cjw
 * @date 2020/7/20
 */
public class CustomAccessDeniedHandler extends BasicHandlerResponse implements AccessDeniedHandler, AuthenticationEntryPoint {
    private BaseCustomerSecurity baseCustomerSecurity;

    public CustomAccessDeniedHandler(BaseCustomerSecurity baseCustomerSecurity) {
        this.baseCustomerSecurity = baseCustomerSecurity;
    }

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException, ServletException {
        handleResponse(request, response, baseCustomerSecurity.noLoginDeniedHandler(authException));
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException, ServletException {
        handleResponse(request, response, baseCustomerSecurity.haveLoginDeniedHandler(accessDeniedException));
    }
}