package com.jiayi.auth.security.handler;

import com.jiayi.auth.security.interfaces.BaseCustomerSecurity;
import com.jiayi.auth.security.model.BasicHandlerResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author cjw
 * @date 2020/7/20
 */
public class CustomLogoutHandler extends BasicHandlerResponse implements LogoutHandler, LogoutSuccessHandler {

    private BaseCustomerSecurity baseCustomerSecurity;

    public CustomLogoutHandler(BaseCustomerSecurity baseCustomerSecurity) {
        this.baseCustomerSecurity = baseCustomerSecurity;
    }

    /**
     * 登出操作处理器
     *
     * @param request
     * @param response
     * @param authentication
     */
    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try {
            baseCustomerSecurity.logoutHandler(request, authentication);
        } catch (Exception e) {
        }
    }

    /**
     * 登出成功后的处理器
     *
     * @param request
     * @param response
     * @param authentication
     * @throws IOException
     */
    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException {
        handleResponse(request, response, baseCustomerSecurity.logoutSuccessHandler(authentication));
    }
}
