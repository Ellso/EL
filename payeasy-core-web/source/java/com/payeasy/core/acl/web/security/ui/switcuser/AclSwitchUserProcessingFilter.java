package com.payeasy.core.acl.web.security.ui.switcuser;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.AccountExpiredException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationCredentialsNotFoundException;
import org.springframework.security.AuthenticationException;
import org.springframework.security.CredentialsExpiredException;
import org.springframework.security.DisabledException;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.LockedException;
import org.springframework.security.SpringSecurityMessageSource;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.event.authentication.AuthenticationSwitchUserEvent;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.security.ui.AuthenticationDetailsSource;
import org.springframework.security.ui.FilterChainOrder;
import org.springframework.security.ui.SpringSecurityFilter;
import org.springframework.security.ui.WebAuthenticationDetailsSource;
import org.springframework.security.ui.switchuser.SwitchUserAuthorityChanger;
import org.springframework.security.ui.switchuser.SwitchUserGrantedAuthority;
import org.springframework.security.ui.switchuser.SwitchUserProcessingFilter;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsChecker;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.security.userdetails.checker.AccountStatusUserDetailsChecker;
import org.springframework.security.util.RedirectUtils;
import org.springframework.util.Assert;

import com.payeasy.core.acl.web.security.userdetails.AclUserDetailsManager;

@SuppressWarnings("unchecked")
public class AclSwitchUserProcessingFilter extends SpringSecurityFilter implements InitializingBean,
        ApplicationEventPublisherAware, MessageSourceAware {

    public static final String SPRING_SECURITY_SWITCH_USERNAME_KEY = "j_username";
    public static final String ROLE_PREVIOUS_ADMINISTRATOR = "ROLE_PREVIOUS_ADMINISTRATOR";
    public static final String SPRING_SECURITY_LAST_EXCEPTION_KEY = "SPRING_SECURITY_LAST_EXCEPTION";

    private ApplicationEventPublisher eventPublisher;
    private AuthenticationDetailsSource authenticationDetailsSource = new WebAuthenticationDetailsSource();
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();
    private String exitUserUrl = "/j_spring_security_exit_user";
    private String switchUserUrl = "/j_spring_security_switch_user";
    private String targetUrl;
    private String switchFailureUrl;
    private SwitchUserAuthorityChanger switchUserAuthorityChanger;
    private AclUserDetailsManager userDetailsManager;
    private final UserDetailsChecker userDetailsChecker = new AccountStatusUserDetailsChecker();
    private boolean useRelativeContext;

    private String usernameParameter = SPRING_SECURITY_SWITCH_USERNAME_KEY;

    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.usernameParameter, "usernameParameter is required");
        Assert.hasLength(this.switchUserUrl, "switchUserUrl is required");
        Assert.hasLength(this.exitUserUrl, "exitUserUrl is required");
        Assert.hasLength(this.targetUrl, "targetUrl is required");
        Assert.notNull(this.userDetailsManager, "userDetailsManager is required");
        Assert.notNull(this.messages, "messages is required");
    }

    public String getUsernameParameter() {
        return this.usernameParameter;
    }

    public void setUsernameParameter(String usernameParameter) {
        this.usernameParameter = usernameParameter;
    }

    protected String obtainUsername(HttpServletRequest request) {
        return request.getParameter(this.usernameParameter);
    }

    /**
     * Attempt to exit from an already switched user.
     *
     * @param request The http servlet request
     *
     * @return The original <code>Authentication</code> object or <code>null</code> otherwise.
     *
     * @throws AuthenticationCredentialsNotFoundException If no <code>Authentication</code> associated with this
     *         request.
     */
    protected Authentication attemptExitUser(HttpServletRequest request)
            throws AuthenticationCredentialsNotFoundException {
        // need to check to see if the current user has a SwitchUserGrantedAuthority
        Authentication current = SecurityContextHolder.getContext().getAuthentication();

        if (null == current) {
            throw new AuthenticationCredentialsNotFoundException(this.messages.getMessage(
                    "SwitchUserProcessingFilter.noCurrentUser", "No current user associated with this request"));
        }

        // check to see if the current user did actual switch to another user
        // if so, get the original source user so we can switch back
        Authentication original = this.getSourceAuthentication(current);

        if (original == null) {
            this.logger.error("Could not find original user Authentication object!");
            throw new AuthenticationCredentialsNotFoundException(this.messages.getMessage(
                    "SwitchUserProcessingFilter.noOriginalAuthentication",
                    "Could not find original Authentication object"));
        }

        // get the source user details
        UserDetails originalUser = null;
        Object obj = original.getPrincipal();

        if ((obj != null) && obj instanceof UserDetails) {
            originalUser = (UserDetails) obj;
        }

        // publish event
        if (this.eventPublisher != null) {
            this.eventPublisher.publishEvent(new AuthenticationSwitchUserEvent(current, originalUser));
        }

        return original;
    }

    /**
     * Attempt to switch to another user. If the user does not exist or is not active, return null.
     *
     * @return The new <code>Authentication</code> request if successfully switched to another user, <code>null</code>
     *         otherwise.
     *
     * @throws UsernameNotFoundException If the target user is not found.
     * @throws LockedException if the account is locked.
     * @throws DisabledException If the target user is disabled.
     * @throws AccountExpiredException If the target user account is expired.
     * @throws CredentialsExpiredException If the target user credentials are expired.
     */
    protected Authentication attemptSwitchUser(HttpServletRequest request) throws AuthenticationException {
        UsernamePasswordAuthenticationToken targetUserRequest = null;

        String username = this.obtainUsername(request);

        if (username == null) {
            username = "";
        }

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Attempt to switch to user [" + username + "]");
        }

        UserDetails targetUser = this.userDetailsManager.loadUserByUsername(username);
        this.userDetailsChecker.check(targetUser);

        // ok, create the switch user token
        targetUserRequest = this.createSwitchUserToken(request, targetUser);

        if (this.logger.isDebugEnabled()) {
            this.logger.debug("Switch User Token [" + targetUserRequest + "]");
        }

        // publish event
        if (this.eventPublisher != null) {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            this.eventPublisher.publishEvent(new AuthenticationSwitchUserEvent(authentication, targetUser));
        }

        return targetUserRequest;
    }

    /**
     * Create a switch user token that contains an additional <tt>GrantedAuthority</tt> that contains the
     * original <code>Authentication</code> object.
     *
     * @param request The http servlet request.
     * @param targetUser The target user
     *
     * @return The authentication token
     *
     * @see SwitchUserGrantedAuthority
     */
    private UsernamePasswordAuthenticationToken createSwitchUserToken(HttpServletRequest request,
            UserDetails targetUser) {

        UsernamePasswordAuthenticationToken targetUserRequest;

        // grant an additional authority that contains the original Authentication object
        // which will be used to 'exit' from the current switched user.
        Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
        GrantedAuthority switchAuthority = new SwitchUserGrantedAuthority(ROLE_PREVIOUS_ADMINISTRATOR, currentAuth);

        // get the original authorities
        List orig = Arrays.asList(targetUser.getAuthorities());

        // Allow subclasses to change the authorities to be granted
        if (this.switchUserAuthorityChanger != null) {
            orig = this.switchUserAuthorityChanger.modifyGrantedAuthorities(targetUser, currentAuth, orig);
        }

        // add the new switch user authority
        List newAuths = new ArrayList(orig);
        newAuths.add(switchAuthority);

        GrantedAuthority[] authorities =
                (GrantedAuthority[]) newAuths.toArray(new GrantedAuthority[newAuths.size()]);

        // create the new authentication token
        targetUserRequest = new UsernamePasswordAuthenticationToken(targetUser, targetUser.getPassword(), authorities);

        // set details
        targetUserRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));

        return targetUserRequest;
    }

    public void doFilterHttp(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        // check for switch or exit request
        if (this.requiresSwitchUser(request)) {
            // if set, attempt switch and store original

            try {
                Authentication targetUser = this.attemptSwitchUser(request);

                // update the current context to the new target user
                SecurityContextHolder.getContext().setAuthentication(targetUser);

                // redirect to target url
                this.sendRedirect(request, response, this.targetUrl);
            } catch (AuthenticationException e) {
                request.getSession().setAttribute(SPRING_SECURITY_LAST_EXCEPTION_KEY, e);
                this.redirectToFailureUrl(request, response, e);
            }

            return;
        } else if (this.requiresExitUser(request)) {
            // get the original authentication object (if exists)
            Authentication originalUser = this.attemptExitUser(request);

            // update the current context back to the original user
            SecurityContextHolder.getContext().setAuthentication(originalUser);

            // redirect to target url
            this.sendRedirect(request, response, this.targetUrl);

            return;
        }

        chain.doFilter(request, response);
    }

    private void redirectToFailureUrl(HttpServletRequest request, HttpServletResponse response,
            AuthenticationException failed) throws IOException {
        this.logger.debug("Switch User failed", failed);

        if (this.switchFailureUrl != null) {
            this.sendRedirect(request, response, this.switchFailureUrl);
        } else {
            response.getWriter().print("Switch user failed: " + failed.getMessage());
            response.flushBuffer();
        }
    }

    protected void sendRedirect(HttpServletRequest request, HttpServletResponse response, String url)
            throws IOException {

        RedirectUtils.sendRedirect(request, response, url, this.useRelativeContext);
    }

    /**
     * Find the original <code>Authentication</code> object from the current user's granted authorities. A
     * successfully switched user should have a <code>SwitchUserGrantedAuthority</code> that contains the original
     * source user <code>Authentication</code> object.
     *
     * @param current The current  <code>Authentication</code> object
     *
     * @return The source user <code>Authentication</code> object or <code>null</code> otherwise.
     */
    private Authentication getSourceAuthentication(Authentication current) {
        Authentication original = null;

        // iterate over granted authorities and find the 'switch user' authority
        GrantedAuthority[] authorities = current.getAuthorities();

        for (GrantedAuthority authoritie : authorities) {
            // check for switch user type of authority
            if (authoritie instanceof SwitchUserGrantedAuthority) {
                original = ((SwitchUserGrantedAuthority) authoritie).getSource();
                this.logger.debug("Found original switch user granted authority [" + original + "]");
            }
        }

        return original;
    }

    /**
     * Checks the request URI for the presence of <tt>exitUserUrl</tt>.
     *
     * @param request The http servlet request
     *
     * @return <code>true</code> if the request requires a exit user, <code>false</code> otherwise.
     *
     * @see SwitchUserProcessingFilter#exitUserUrl
     */
    protected boolean requiresExitUser(HttpServletRequest request) {
        String uri = stripUri(request);

        return uri.endsWith(request.getContextPath() + this.exitUserUrl);
    }

    /**
     * Checks the request URI for the presence of <tt>switchUserUrl</tt>.
     *
     * @param request The http servlet request
     *
     * @return <code>true</code> if the request requires a switch, <code>false</code> otherwise.
     *
     * @see SwitchUserProcessingFilter#switchUserUrl
     */
    protected boolean requiresSwitchUser(HttpServletRequest request) {
        String uri = stripUri(request);

        return uri.endsWith(request.getContextPath() + this.switchUserUrl);
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher eventPublisher)
        throws BeansException {
        this.eventPublisher = eventPublisher;
    }

    public void setAuthenticationDetailsSource(AuthenticationDetailsSource authenticationDetailsSource) {
        Assert.notNull(authenticationDetailsSource, "AuthenticationDetailsSource required");
        this.authenticationDetailsSource = authenticationDetailsSource;
    }

    /**
     * Set the URL to respond to exit user processing.
     *
     * @param exitUserUrl The exit user URL.
     */
    public void setExitUserUrl(String exitUserUrl) {
        this.exitUserUrl = exitUserUrl;
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messages = new MessageSourceAccessor(messageSource);
    }

    /**
     * Set the URL to respond to switch user processing.
     *
     * @param switchUserUrl The switch user URL.
     */
    public void setSwitchUserUrl(String switchUserUrl) {
        this.switchUserUrl = switchUserUrl;
    }

    /**
     * Sets the URL to go to after a successful switch / exit user request.
     *
     * @param targetUrl The target url.
     */
    public void setTargetUrl(String targetUrl) {
        this.targetUrl = targetUrl;
    }

    /**
     * Sets the authentication data access object.
     *
     * @param userDetailsManager The authentication dao
     */
    public void setUserDetailsManager(AclUserDetailsManager userDetailsManager) {
        this.userDetailsManager = userDetailsManager;
    }

    /**
     * Analogous to the same property in {@link AbstractProcessingFilter}. If set, redirects will
     * be context-relative (they won't include the context path).
     *
     * @param useRelativeContext set to true to exclude the context path from redirect URLs.
     */
    public void setUseRelativeContext(boolean useRelativeContext) {
        this.useRelativeContext = useRelativeContext;
    }

    /**
     * Sets the URL to which a user should be redirected if the swittch fails. For example, this might happen because
     * the account they are attempting to switch to is invalid (the user doesn't exist, account is locked etc).
     * <p>
     * If not set, an error essage wil be written to the response.
     *
     * @param switchFailureUrl the url to redirect to.
     */
    public void setSwitchFailureUrl(String switchFailureUrl) {
        this.switchFailureUrl = switchFailureUrl;
    }

    /**
     * Strips any content after the ';' in the request URI
     *
     * @param request The http request
     *
     * @return The stripped uri
     */
    private static String stripUri(HttpServletRequest request) {
        String uri = request.getRequestURI();
        int idx = uri.indexOf(';');

        if (idx > 0) {
            uri = uri.substring(0, idx);
        }

        return uri;
    }

    /**
     * @param switchUserAuthorityChanger to use to fine-tune the authorities granted to subclasses (may be null if
     * SwitchUserProcessingFilter shoudl not fine-tune the authorities)
     */
    public void setSwitchUserAuthorityChanger(SwitchUserAuthorityChanger switchUserAuthorityChanger) {
        this.switchUserAuthorityChanger = switchUserAuthorityChanger;
    }

    public int getOrder() {
        return FilterChainOrder.SWITCH_USER_FILTER;
    }
}