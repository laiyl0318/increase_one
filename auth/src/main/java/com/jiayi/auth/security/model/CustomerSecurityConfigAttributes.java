package com.jiayi.auth.security.model;

import lombok.Data;
import org.springframework.security.access.ConfigAttribute;

import java.util.Collection;

/**
 * 权限：url 与 roles关系model
 *
 * @author laiyilong
 */
@Data
public class CustomerSecurityConfigAttributes {
    /**
     * uri匹配字符串：可以是单个url或符合RequestMatcher 匹配规则的字符串
     */
    private String uri;

    /**
     * 对应的角色数据 转换为ConfigAttribute
     */
    private Collection<ConfigAttribute> attributes;
}
