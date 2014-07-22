package com.payeasy.core.acl.web.security.event;

import org.springframework.context.ApplicationEvent;
import org.springframework.security.event.authentication.AbstractAuthenticationEvent;

public abstract class AbstractAuthenticationEventHandler implements ApplicationEventHandler {

    public boolean supports(ApplicationEvent event) {
        return (event instanceof AbstractAuthenticationEvent);
    }

}