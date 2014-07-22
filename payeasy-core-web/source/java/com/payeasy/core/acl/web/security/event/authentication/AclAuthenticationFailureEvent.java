package com.payeasy.core.acl.web.security.event.authentication;

import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.event.authentication.AbstractAuthenticationFailureEvent;
import org.springframework.util.Assert;

public class AclAuthenticationFailureEvent extends AbstractAuthenticationFailureEvent {

    private static final long serialVersionUID = -4537572030738231630L;

    private AclAuthenticationEventActType actType;

    public AclAuthenticationFailureEvent(Authentication authentication, AuthenticationException exception, AclAuthenticationEventActType actType) {
        super(authentication, exception);
        Assert.notNull(actType, "actType is required");
        this.actType = actType;
    }

    public AclAuthenticationEventActType getActType() {
        return this.actType;
    }

}