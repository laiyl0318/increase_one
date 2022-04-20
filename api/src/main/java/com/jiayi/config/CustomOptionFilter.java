package com.jiayi.config;

import com.jiayi.auth.security.filter.HttpOptionFilter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import javax.servlet.annotation.WebFilter;

/**
 * @author cjw
 * @date 2020-10-25
 */
@Component
@Slf4j
@Order(-103)
@WebFilter(filterName = "httpOptionFilter", urlPatterns = "/*")
public class CustomOptionFilter extends HttpOptionFilter {
}
