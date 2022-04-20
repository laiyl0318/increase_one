package com.jiayi.auth.security.filter;

import com.google.common.base.Strings;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 过滤器，过滤options请求并定义接口允许跨域
 * Created by cjw on 2017/9/29.
 */
//@Component
//@Slf4j
//@Order(-103)
//@WebFilter(filterName = "httpOptionFilter", urlPatterns = "/*")
public class HttpOptionFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        // 定义options 允许跨域
        String originHost = request.getHeader("Origin");
        if (!Strings.isNullOrEmpty(originHost)) {
            response.setHeader("Access-Control-Allow-Origin", "*");
        } else {
            response.setHeader("Access-Control-Allow-Origin", "*");
        }
        String optionsMethodName = "OPTIONS";
        if (optionsMethodName.equals(request.getMethod().toUpperCase())) {
            response.setHeader("Access-Control-Allow-Methods", "GET,POST,PUT,DELETE,OPTIONS");
            response.setHeader("Access-Control-Allow-Headers", "authorization,Authorization,DNT,X-CustomHeader,Keep-Alive,User-Agent,X-Requested-With,If-Modified-Since,Cache-Control,Content-Type");
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }
        filterChain.doFilter(request, response);
    }
}
