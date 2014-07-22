package com.payeasy.core.acl.web.security.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.event.authorization.AbstractAuthorizationEvent;

public abstract class AbstractAuthorizationEventHandler implements ApplicationEventHandler {

    public final boolean supports(ApplicationEvent event) {
        return (event instanceof AbstractAuthorizationEvent);
    }

}
