package com.jiayi.config;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import java.io.IOException;
import java.util.UUID;

/**
 * 增加输出日志traceRootId
 *
 * @author wyq
 * @date 2020-09-04
 */
@Configuration
@Order(-1)
@Slf4j
@WebFilter(filterName = "logbackFilter", urlPatterns = "/*")
public class LogbackFilter implements Filter {

    public static final String UNIQUE_ID = "traceRootId";

    public static boolean insertMDC() {
        UUID uuid = UUID.randomUUID();
        String uniqueId = UNIQUE_ID + "-" + uuid.toString().replace("-", "");
        MDC.put(UNIQUE_ID, uniqueId);
        return true;
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {
        boolean bInsertMDC = insertMDC();
        try {
            chain.doFilter(request, response);
        } finally {
            if (bInsertMDC) {
                MDC.remove(UNIQUE_ID);
            }
        }
    }

    @Override
    public void destroy() {

    }

}