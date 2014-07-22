package com.payeasy.core.acl.web.security.providers;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

public class PasswordAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = -7420975489869552033L;

    private transient String oldPassword;

    public PasswordAuthenticationToken(Object principal, Object credentials, String oldPassword) {
        super(principal, credentials);
        this.oldPassword = oldPassword;
    }

    public PasswordAuthenticationToken(Object principal, Object credentials, GrantedAuthority[] authorities, String oldPassword) {
        super(principal, credentials, authorities);
        this.oldPassword = oldPassword;
    }

    public String getOldPassword() {
        return this.oldPassword;
    }

}
