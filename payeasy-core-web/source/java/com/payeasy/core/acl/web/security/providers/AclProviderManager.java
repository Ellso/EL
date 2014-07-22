package com.payeasy.core.acl.web.security.providers;

import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.AbstractAuthenticationManager;
import org.springframework.security.AccountStatusException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.SpringSecurityMessageSource;
import org.springframework.security.concurrent.ConcurrentLoginException;
import org.springframework.security.concurrent.ConcurrentSessionController;
import org.springframework.security.concurrent.NullConcurrentSessionController;
import org.springframework.security.providers.AbstractAuthenticationToken;
import org.springframework.security.providers.AuthenticationProvider;
import org.springframework.security.providers.ProviderManager;
import org.springframework.security.providers.ProviderNotFoundException;
import org.springframework.util.Assert;

import com.payeasy.core.acl.web.security.event.authentication.AclAuthenticationEventActType;
import com.payeasy.core.acl.web.security.event.authentication.AclAuthenticationFailureEvent;
import com.payeasy.core.acl.web.security.event.authentication.AclAuthenticationSuccessEvent;

@SuppressWarnings("unchecked")
public class AclProviderManager extends AbstractAuthenticationManager implements InitializingBean, MessageSourceAware, ApplicationEventPublisherAware  {

    private static final Log logger = LogFactory.getLog(ProviderManager.class);

    private ApplicationEventPublisher applicationEventPublisher;
    private ConcurrentSessionController sessionController = new NullConcurrentSessionController();
    private List providers;
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.messages, "A message source must be set");
    }

    /**
     * Attempts to authenticate the passed {@link Authentication} object.
     * <p>
     * The list of {@link AuthenticationProvider}s will be successively tried until an
     * <code>AuthenticationProvider</code> indicates it is  capable of authenticating the type of
     * <code>Authentication</code> object passed. Authentication will then be attempted with that
     * <code>AuthenticationProvider</code>.
     * <p>
     * If more than one <code>AuthenticationProvider</code> supports the passed <code>Authentication</code>
     * object, only the first <code>AuthenticationProvider</code> tried will determine the result. No subsequent
     * <code>AuthenticationProvider</code>s will be tried.
     *
     * @param authentication the authentication request object.
     *
     * @return a fully authenticated object including credentials.
     *
     * @throws AuthenticationException if authentication fails.
     */
    public Authentication doAuthentication(Authentication authentication) throws AuthenticationException {

        // 判斷目前進行的動作，是登入還是變更密碼
        AclAuthenticationEventActType actType = (authentication instanceof PasswordAuthenticationToken) ?
                AclAuthenticationEventActType.PASSWORD : AclAuthenticationEventActType.AUTHENTICATION;

        Iterator iter = this.getProviders().iterator();

        Class toTest = authentication.getClass();

        AuthenticationException lastException = null;

        while (iter.hasNext()) {
            AuthenticationProvider provider = (AuthenticationProvider) iter.next();

            if (!provider.supports(toTest)) {
                continue;
            }

            logger.debug("Authentication attempt using " + provider.getClass().getName());

            Authentication result;

            try {
                result = provider.authenticate(authentication);

                if (result != null) {
                    this.copyDetails(authentication, result);
                    this.sessionController.checkAuthenticationAllowed(result);
                }
            } catch (AuthenticationException ae) {
                lastException = ae;
                result = null;
            }

            // SEC-546: Avoid polling additional providers if auth failure is due to invalid account status or
            // disallowed concurrent login.
            if (lastException instanceof AccountStatusException || lastException instanceof ConcurrentLoginException) {
                break;
            }

            if (result != null) {
                this.sessionController.registerSuccessfulAuthentication(result);
                this.publishEvent(new AclAuthenticationSuccessEvent(authentication, actType));

                return result;
            }
        }

        if (lastException == null) {
            lastException = new ProviderNotFoundException(this.messages.getMessage("ProviderManager.providerNotFound",
                        new Object[] {toTest.getName()}, "No AuthenticationProvider found for {0}"));
        }

        this.publishEvent(new AclAuthenticationFailureEvent(authentication, lastException, actType));

        throw lastException;
    }

    /**
     * Copies the authentication details from a source Authentication object to a destination one, provided the
     * latter does not already have one set.
     *
     * @param source source authentication
     * @param dest the destination authentication object
     */
    private void copyDetails(Authentication source, Authentication dest) {
        if ((dest instanceof AbstractAuthenticationToken) && (dest.getDetails() == null)) {
            AbstractAuthenticationToken token = (AbstractAuthenticationToken) dest;

            token.setDetails(source.getDetails());
        }
    }

    public List getProviders() {
        if (this.providers == null || this.providers.size() == 0) {
            throw new IllegalArgumentException("A list of AuthenticationProviders is required");
        }

        return this.providers;
    }

    /**
     * The configured {@link ConcurrentSessionController} is returned or the {@link
     * NullConcurrentSessionController} if a specific one has not been set.
     *
     * @return {@link ConcurrentSessionController} instance
     */
    public ConcurrentSessionController getSessionController() {
        return this.sessionController;
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    /**
     * Sets the {@link AuthenticationProvider} objects to be used for authentication.
     *
     * @param providers the list of authentication providers which will be used to process authentication requests.
     *
     * @throws IllegalArgumentException if the list is empty or null, or any of the elements in the list is not an
     * AuthenticationProvider instance.
     */
    public void setProviders(List providers) {
        Assert.notEmpty(providers, "A list of AuthenticationProviders is required");
        Iterator iter = providers.iterator();

        while (iter.hasNext()) {
            Object currentObject = iter.next();
            Assert.isInstanceOf(AuthenticationProvider.class, currentObject,
                    "Can only provide AuthenticationProvider instances");
        }

        this.providers = providers;
    }

    /**
     * Set the {@link ConcurrentSessionController} to be used for limiting users' sessions. The {@link
     * NullConcurrentSessionController} is used by default.
     *
     * @param sessionController {@link ConcurrentSessionController}
     */
    public void setSessionController(ConcurrentSessionController sessionController) {
        this.sessionController = sessionController;
    }

    private void publishEvent(ApplicationEvent event) {
        if (this.applicationEventPublisher != null) {
            this.applicationEventPublisher.publishEvent(event);
        }
    }

}