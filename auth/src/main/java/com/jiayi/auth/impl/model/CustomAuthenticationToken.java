package com.jiayi.auth.impl.model;

import com.jiayi.model.dto.sys.UserDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by cjw on 2017/10/26.
 *
 * @author cjw
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class CustomAuthenticationToken extends AbstractAuthenticationToken {
    private static final long serialVersionUID = 7274896347464405361L;
    private String userName;
    private String password;
    private UserDTO userDTO;

    public CustomAuthenticationToken(Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
    }

    @Override
    public Object getCredentials() {
        return password;
    }

    @Override
    public Object getPrincipal() {
        return userName;
    }
}
