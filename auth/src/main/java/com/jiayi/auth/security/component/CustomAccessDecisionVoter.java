package com.jiayi.auth.security.component;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.FilterInvocation;

import java.util.Collection;
import java.util.Iterator;

/**
 * @author cjw
 * @date 2020/7/20
 */
public class CustomAccessDecisionVoter implements AccessDecisionVoter<FilterInvocation> {
    @Override
    public boolean supports(ConfigAttribute attribute) {
        return true;
    }

    @Override
    public boolean supports(Class clazz) {
        return true;
    }

    @Override
    public int vote(Authentication authentication, FilterInvocation object, Collection<ConfigAttribute> collection) {
        Iterator<ConfigAttribute> iterator = collection.iterator();
        if (authentication == null || authentication.getAuthorities() == null || authentication.getAuthorities().isEmpty()) {
            return AccessDecisionVoter.ACCESS_DENIED;
        }
        while (iterator.hasNext()) {
            String needRole = iterator.next().getAttribute();
            for (GrantedAuthority authority : authentication.getAuthorities()) {
                if (authority.getAuthority().equals(needRole)) {
                    return AccessDecisionVoter.ACCESS_GRANTED;
                }
            }
        }
        return AccessDecisionVoter.ACCESS_DENIED;
    }
}
