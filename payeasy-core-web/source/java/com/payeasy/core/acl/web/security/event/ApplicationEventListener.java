package com.payeasy.core.acl.web.security.event;

import java.util.List;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.util.Assert;

public class ApplicationEventListener implements ApplicationListener, InitializingBean {

    private List<ApplicationEventHandler> handlers;

    public void afterPropertiesSet() throws Exception {
        Assert.notEmpty(this.handlers, "handlers must contain at least 1 element");
        Assert.noNullElements(this.handlers.toArray(), "handlers must not contain any null elements");
    }

    public void setHandlers(List<ApplicationEventHandler> handlers) {
        this.handlers = handlers;
    }

    public void onApplicationEvent(ApplicationEvent event) {

        for (ApplicationEventHandler handler : this.handlers) {
            if (handler.supports(event)) {
                handler.handle(event);
            }
        }
    }

}