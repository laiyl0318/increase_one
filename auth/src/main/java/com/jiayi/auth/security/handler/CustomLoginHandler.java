package com.jiayi.auth.security.handler;

import com.jiayi.auth.security.interfaces.BaseCustomerSecurity;
import com.jiayi.auth.security.model.BasicHandlerResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author cjw
 * @date 2020/7/20
 */
public class CustomLoginHandler extends BasicHandlerResponse implements AuthenticationSuccessHandler, AuthenticationFailureHandler {
    private BaseCustomerSecurity baseCustomerSecurity;

    public CustomLoginHandler(BaseCustomerSecurity baseCustomerSecurity) {
        this.baseCustomerSecurity = baseCustomerSecurity;
    }

    /**
     * 登录验证失败处理方法（接口响应数据，并返回数据到客户端）
     *
     * @param request
     * @param response
     * @param exception
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
        // 接收登录失败处理器数据
        handleResponse(request, response, baseCustomerSecurity.loginFailureHandler(request, exception));
    }

    /**
     * 登录成功后处理器
     *
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     * @throws ServletException
     */
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        // 接收登录成功处理器数据
        handleResponse(request, response, baseCustomerSecurity.loginSuccessHandler(request, authentication));
    }
}
