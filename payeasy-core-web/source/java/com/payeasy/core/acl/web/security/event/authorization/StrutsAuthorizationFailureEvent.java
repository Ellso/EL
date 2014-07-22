package com.payeasy.core.acl.web.security.event.authorization;

import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;
import org.springframework.security.event.authorization.AbstractAuthorizationEvent;
import org.springframework.util.Assert;

public class StrutsAuthorizationFailureEvent extends AbstractAuthorizationEvent {

    private static final long serialVersionUID = -3601472268991296845L;

    private String configAttribute;
    private Authentication authentication;
    private AccessDeniedException accessDeniedException;

    public StrutsAuthorizationFailureEvent(Object secureObject, String configAttribute,
            Authentication authentication, AccessDeniedException accessDeniedException) {
        super(secureObject);
        Assert.notNull(secureObject, "secureObject is required");
        Assert.hasText(configAttribute, "configAttribute is required");
        Assert.notNull(authentication, "authentication is required");
        Assert.notNull(accessDeniedException, "accessDeniedException is required");

        this.configAttribute = configAttribute;
        this.authentication = authentication;
        this.accessDeniedException = accessDeniedException;
    }

    public String getConfigAttribute() {
        return this.configAttribute;
    }

    public Authentication getAuthentication() {
        return this.authentication;
    }

    public AccessDeniedException getAccessDeniedException() {
        return this.accessDeniedException;
    }

}
