package com.payeasy.core.acl.web.security.event.authentication;

import org.springframework.security.Authentication;
import org.springframework.security.event.authentication.AuthenticationSuccessEvent;
import org.springframework.util.Assert;

public class AclAuthenticationSuccessEvent extends AuthenticationSuccessEvent {

    private static final long serialVersionUID = 1622513468600568062L;

    private AclAuthenticationEventActType actType;

    public AclAuthenticationSuccessEvent(Authentication authentication, AclAuthenticationEventActType actType) {
        super(authentication);
        Assert.notNull(actType, "actType is required");
        this.actType = actType;
    }

    public AclAuthenticationEventActType getActType() {
        return this.actType;
    }

}