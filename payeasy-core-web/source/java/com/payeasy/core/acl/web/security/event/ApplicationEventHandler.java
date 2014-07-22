package com.payeasy.core.acl.web.security.event;

import org.springframework.context.ApplicationEvent;

public interface ApplicationEventHandler {

    public boolean supports(ApplicationEvent event);

    public void handle(ApplicationEvent event);
}
