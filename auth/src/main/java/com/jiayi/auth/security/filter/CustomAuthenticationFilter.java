package com.jiayi.auth.security.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectReader;
import com.google.common.io.CharStreams;
import com.jiayi.auth.security.exceptions.ErrorVerificationException;
import com.jiayi.auth.security.interfaces.BaseCustomerSecurity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Map;
import java.util.Objects;

/**
 * 自定义授权filter
 * @author laiyilong
 * @date 2022/4/19
 */
public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private BaseCustomerSecurity baseCustomerSecurity;
    private ObjectMapper objectMapper = new ObjectMapper();

    public CustomAuthenticationFilter(BaseCustomerSecurity baseCustomerSecurity) {
        this.baseCustomerSecurity = baseCustomerSecurity;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException {
        String userName = null, password = null, verifycation = null;

        UsernamePasswordAuthenticationToken token;
        userName = request.getParameter(baseCustomerSecurity.userNameKey());
        password = request.getParameter(baseCustomerSecurity.passWordKey());
        verifycation = request.getParameter(baseCustomerSecurity.verificationKey());
        if (Objects.isNull(userName) && Objects.isNull(password)) {
            try {
                String requestBody = CharStreams.toString(new InputStreamReader(request.getInputStream()));
                ObjectReader objectReader = objectMapper.readerFor(Map.class);
                Map<String, String> result = objectReader.readValue(requestBody);
                userName = result.get(baseCustomerSecurity.userNameKey());
                password = result.get(baseCustomerSecurity.passWordKey());
                verifycation = result.get(baseCustomerSecurity.verificationKey());
            } catch (IOException ex) {

            }
        }

        if (baseCustomerSecurity.useVerification(request) && !baseCustomerSecurity.checkVerification(request, verifycation)) {
            throw new ErrorVerificationException("验证码错误");
        }
        token = new UsernamePasswordAuthenticationToken(userName, password);
        return this.getAuthenticationManager().authenticate(token);
    }
}