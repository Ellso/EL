package com.payeasy.core.acl.web.security.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.security.event.authentication.AbstractAuthenticationEvent;
import org.springframework.security.event.authentication.AbstractAuthenticationFailureEvent;
import org.springframework.security.event.authentication.AuthenticationSwitchUserEvent;
import org.springframework.security.event.authentication.InteractiveAuthenticationSuccessEvent;
import org.springframework.util.ClassUtils;

import com.payeasy.core.acl.web.security.event.authentication.AclAuthenticationFailureEvent;
import com.payeasy.core.acl.web.security.event.authentication.AclAuthenticationSuccessEvent;
import com.payeasy.core.acl.web.security.event.authentication.AuthenticationAuditEvent;

public class LogAuthenticationEventHandler extends AbstractAuthenticationEventHandler {

    private static final Log logger = LogFactory.getLog(LogAuthenticationEventHandler.class);

    private boolean logInteractiveAuthenticationSuccessEvents = true;

    public void setLogInteractiveAuthenticationSuccessEvents(boolean logInteractiveAuthenticationSuccessEvents) {
        this.logInteractiveAuthenticationSuccessEvents = logInteractiveAuthenticationSuccessEvents;
    }

    public void handle(ApplicationEvent event) {
        AbstractAuthenticationEvent authEvent = (AbstractAuthenticationEvent) event;

        if (!this.logInteractiveAuthenticationSuccessEvents
                && authEvent instanceof InteractiveAuthenticationSuccessEvent) {
            return;
        }

        if (logger.isWarnEnabled()) {
            String message = "Authentication event "
                +  ClassUtils.getShortName(authEvent.getClass())
                + ": " + authEvent.getAuthentication().getName()
                + "; details: " + authEvent.getAuthentication().getDetails();

            if (event instanceof AclAuthenticationSuccessEvent) {
                message = message + "; actType: "
                + ((AclAuthenticationSuccessEvent) event).getActType();
            }

            if (event instanceof AclAuthenticationFailureEvent) {
                message = message + "; actType: "
                + ((AclAuthenticationFailureEvent) event).getActType();
            }

            if (event instanceof AbstractAuthenticationFailureEvent) {
                message = message + "; exception: "
                + ((AbstractAuthenticationFailureEvent) event).getException().getMessage();
            }

            if (event instanceof AuthenticationSwitchUserEvent) {
                message = message + "; targetUser: "
                + ((AuthenticationSwitchUserEvent) event).getTargetUser().getUsername();
            }

            if (event instanceof AuthenticationAuditEvent) {
                message = message + "; message: "
                + ((AuthenticationAuditEvent) event).getMessage();
            }

            logger.warn(message);
        }
    }

}