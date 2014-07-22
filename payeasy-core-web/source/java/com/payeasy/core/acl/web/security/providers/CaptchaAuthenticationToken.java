package com.payeasy.core.acl.web.security.providers;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;

public class CaptchaAuthenticationToken extends UsernamePasswordAuthenticationToken {

    private static final long serialVersionUID = 9034760725431164522L;

    private transient String sessionId;
    private transient String captcha;

    public CaptchaAuthenticationToken(Object principal, Object credentials, String sessionId, String captcha) {
        super(principal, credentials);
        this.sessionId = sessionId;
        this.captcha = captcha;
    }

    public CaptchaAuthenticationToken(Object principal, Object credentials, GrantedAuthority[] authorities, String sessionId, String captcha) {
        super(principal, credentials, authorities);
        this.sessionId = sessionId;
        this.captcha = captcha;
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getCaptcha() {
        return this.captcha;
    }

}