package com.payeasy.core.acl.web.security.providers;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

public class OtpAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 6649958878077023836L;

    private transient String token;
    private transient String serial;

    public OtpAuthenticationToken(Object principal, Object credentials, String token, String serial) {
        super(principal, credentials);
        this.token = token;
        this.serial = serial;
    }

    public OtpAuthenticationToken(Object principal, Object credentials, GrantedAuthority[] authorities, String token, String serial) {
        super(principal, credentials, authorities);
        this.token = token;
        this.serial = serial;
    }

    public String getToken() {
        return this.token;
    }

    public String getSerial() {
        return this.serial;
    }

}
