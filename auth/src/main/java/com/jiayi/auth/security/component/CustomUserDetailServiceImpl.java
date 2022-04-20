package com.jiayi.auth.security.component;

import com.jiayi.auth.security.interfaces.BaseCustomerSecurity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * 自定义账号查询service
 *
 * @author laiyilong
 * @date 2022/04/19
 */
public class CustomUserDetailServiceImpl implements UserDetailsService {
    private BaseCustomerSecurity baseCustomerSecurity;

    public CustomUserDetailServiceImpl(BaseCustomerSecurity baseCustomerSecurity) {
        this.baseCustomerSecurity = baseCustomerSecurity;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return baseCustomerSecurity.loadUserByUsername(username);
    }
}
