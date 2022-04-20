package com.jiayi.auth.security.filter;

import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

/**
 * 过滤器，过滤request params 空格
 * Created by cjw on 2017/9/29.
 */
//@Configuration
//@Order(-102)
//@WebFilter(filterName = "trimFilter", urlPatterns = "/*")
//@Slf4j
public class TrimFilter extends GenericFilterBean {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        CustomRequestWrapper requestWrapper = new CustomRequestWrapper(request);
        trimParams(requestWrapper.getParameterMap());
        filterChain.doFilter(requestWrapper, response);
    }

    /**
     * 过滤参数
     *
     * @param params
     */
    private void trimParams(Map<String, String[]> params) {
        if (!params.isEmpty()) {
            for (String key : params.keySet()) {
                String[] values = params.get(key);
                if (values.length > 0) {
                    for (int i = 0; i < values.length; i++) {
                        values[i] = values[i].trim();
                    }
                }
            }
        }
    }
}
