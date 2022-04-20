package com.jiayi.auth.security.component;

import com.jiayi.auth.security.interfaces.CustomerSecurityService;
import com.jiayi.auth.security.model.CustomerSecurityConfigAttributes;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.web.FilterInvocation;
import org.springframework.security.web.access.intercept.FilterInvocationSecurityMetadataSource;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.util.Collection;
import java.util.List;

/**
 * 权限源数据
 *
 * @author cjw
 * @date 2020/7/20
 */
public class CustomSecurityMetadataSource implements FilterInvocationSecurityMetadataSource {
    private CustomerSecurityService customerSecurityService;

    public CustomSecurityMetadataSource(CustomerSecurityService customerSecurityService) {
        this.customerSecurityService = customerSecurityService;
    }


    @Override
    public Collection<ConfigAttribute> getAttributes(Object object) throws IllegalArgumentException {
        FilterInvocation fi = (FilterInvocation) object;

        List<CustomerSecurityConfigAttributes> attributes = customerSecurityService.loadPermissionData();
        if (attributes != null && !attributes.isEmpty()) {
            for (CustomerSecurityConfigAttributes attribute : attributes) {
                RequestMatcher requestMatcher = new AntPathRequestMatcher(attribute.getUri().trim());
                // 匹配uri
                if (requestMatcher.matches(fi.getHttpRequest())) {
                    return attribute.getAttributes();
                }
            }
        }
        throw new AccessDeniedException("没有权限访问");
    }

    @Override
    public Collection<ConfigAttribute> getAllConfigAttributes() {
        return null;
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return FilterInvocation.class.isAssignableFrom(clazz);
    }
}