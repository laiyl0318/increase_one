package com.jiayi.auth.security.component;

import org.springframework.security.access.AccessDecisionVoter;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.ConfigAttribute;
import org.springframework.security.access.vote.AbstractAccessDecisionManager;
import org.springframework.security.authentication.InsufficientAuthenticationException;
import org.springframework.security.core.Authentication;

import java.util.Collection;
import java.util.List;

/**
 * @author cjw
 * @date 2020/7/20
 */
public class CustomAccessDecisionManager extends AbstractAccessDecisionManager {
    public CustomAccessDecisionManager(List<AccessDecisionVoter<?>> decisionVoters) {
        super(decisionVoters);
    }

    @SuppressWarnings("unchecked")
    @Override
    public void decide(Authentication authentication, Object object, Collection<ConfigAttribute> configAttributes) throws AccessDeniedException, InsufficientAuthenticationException {
        for (AccessDecisionVoter accessDecisionVoter : this.getDecisionVoters()) {
            int result = accessDecisionVoter.vote(authentication, object, configAttributes);
            switch (result) {
                case AccessDecisionVoter.ACCESS_GRANTED:
                    return;
                case AccessDecisionVoter.ACCESS_DENIED:
                    throw new AccessDeniedException("没有权限访问");
                default:
                    throw new RuntimeException("没有权限自定义");
            }
        }
    }
}
