package com.payeasy.core.acl.web.security.event;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.ApplicationEvent;
import org.springframework.security.event.authorization.AuthenticationCredentialsNotFoundEvent;
import org.springframework.security.event.authorization.AuthorizationFailureEvent;
import org.springframework.security.event.authorization.AuthorizedEvent;
import org.springframework.security.event.authorization.PublicInvocationEvent;

import com.payeasy.core.acl.web.security.event.authorization.StrutsAuthorizationFailureEvent;

public class LogAuthorizationEventHandler extends AbstractAuthorizationEventHandler {

    private static final Log logger = LogFactory.getLog(LogAuthorizationEventHandler.class);

    private boolean logAuthorizedEvents = false;

    private boolean logPublicInvocationEvents = false;

    public void setLogAuthorizedEvents(boolean logAuthorizedEvents) {
        this.logAuthorizedEvents = logAuthorizedEvents;
    }

    public void setLogPublicInvocationEvents(boolean logPublicInvocationEvents) {
        this.logPublicInvocationEvents = logPublicInvocationEvents;
    }

    public void handle(ApplicationEvent event) {

        if (!this.logAuthorizedEvents && event instanceof AuthorizedEvent) {
            return;
        }

        if (!this.logPublicInvocationEvents && event instanceof PublicInvocationEvent) {
            return;
        }

        if (event instanceof AuthenticationCredentialsNotFoundEvent) {
            AuthenticationCredentialsNotFoundEvent actualEvent = (AuthenticationCredentialsNotFoundEvent) event;

            if (logger.isWarnEnabled()) {
                logger.warn("Security interception failed due to: " + actualEvent.getCredentialsNotFoundException()
                    + "; secure object: " + actualEvent.getSource()
                    + "; configuration attributes: " + actualEvent.getConfigAttributeDefinition());
            }
        }

        if (event instanceof AuthorizationFailureEvent) {
            AuthorizationFailureEvent actualEvent = (AuthorizationFailureEvent) event;

            if (logger.isWarnEnabled()) {
                logger.warn("Security authorization failed due to: " + actualEvent.getAccessDeniedException()
                    + "; authenticated principal: " + actualEvent.getAuthentication()
                    + "; secure object: " + actualEvent.getSource()
                    + "; configuration attributes: " + actualEvent.getConfigAttributeDefinition());
            }
        }

        if (event instanceof StrutsAuthorizationFailureEvent) {
            StrutsAuthorizationFailureEvent actualEvent = (StrutsAuthorizationFailureEvent) event;

            if (logger.isWarnEnabled()) {
                logger.warn("Security authorization failed due to: " + actualEvent.getAccessDeniedException()
                    + "; authenticated principal: " + actualEvent.getAuthentication()
                    + "; secure object: " + actualEvent.getSource()
                    + "; configuration attributes: " + actualEvent.getConfigAttribute());
            }
        }

        if (event instanceof AuthorizedEvent) {
            AuthorizedEvent actualEvent = (AuthorizedEvent) event;

            if (logger.isInfoEnabled()) {
                logger.info("Security authorized for authenticated principal: " + actualEvent.getAuthentication()
                    + "; secure object: " + actualEvent.getSource()
                    + "; configuration attributes: " + actualEvent.getConfigAttributeDefinition());
            }
        }

        if (event instanceof PublicInvocationEvent) {
            PublicInvocationEvent actualEvent = (PublicInvocationEvent) event;

            if (logger.isInfoEnabled()) {
                logger.info("Security interception not required for public secure object: " + actualEvent.getSource());
            }
        }
    }

}
