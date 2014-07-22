package com.payeasy.core.acl.web.security.event.authentication;

import org.springframework.security.Authentication;
import org.springframework.security.event.authentication.AbstractAuthenticationEvent;
import org.springframework.util.Assert;

public class AuthenticationAuditEvent extends AbstractAuthenticationEvent {

    private static final long serialVersionUID = 8390701468402319908L;

    private String message;

    public AuthenticationAuditEvent(Authentication authentication, String message) {
        super(authentication);
        Assert.hasText(message, "message is required");
        this.message = message;
    }

    public String getMessage() {
        return this.message;
    }

}