package com.payeasy.core.acl.web.struts.actions;

import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.util.Assert;

public class LoginAction extends AbstractErrorMessageAction {

    private static final long serialVersionUID = -735924760210121820L;

    // struts form fields
    private String username;
    private String password;

    // message fields
    private String logoutMessage;
    private String timeoutMessage;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return this.password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setLogoutMessage(String logoutMessage) {
        this.logoutMessage = logoutMessage;
    }

    public void setTimeoutMessage(String timeoutMessage) {
        this.timeoutMessage = timeoutMessage;
    }

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.hasText(this.logoutMessage, "logoutMessage must have text");
        Assert.hasText(this.timeoutMessage, "timeoutMessage must have text");
    }

    @SkipValidation
    public String loginForm() {

        // step1 - check user is login, invalidate session
        if (this.getRequest().getUserPrincipal() != null) {
            HttpSession session = this.getSession(true);
            session.invalidate();
            // must use redirect, for struts2 theme feed
            return REDIRECT;
        }

        return SUCCESS;
    }

    public String login() {
        return NONE;
    }

    @SkipValidation
    public String loginSuccess() {
        return SUCCESS;
    }

    @SkipValidation
    public String loginError() {

        HttpSession session = this.getSession(true);

        // step1 - check login failed message
        String username = (String) session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY);
        AuthenticationException exception = (AuthenticationException) session.getAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);

        // step2 - check username and exception, if null redirect to login form
        if (username == null || exception == null) {
            return REDIRECT;
        }

        // step3 - clear spring security setting
        session.removeAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);

        // step4 - retrieve exception class name
        String exceptionClassName = exception.getClass().getName();

        // step5 - reset form fields
        this.setUsername(username);
        this.setPassword(null);

        // step6 - handle action message
        this.handleErrorMessage(exceptionClassName);

        return SUCCESS;
    }

    @SkipValidation
    public String loginTimeout() {
        // step1 - handle action message
        this.handleActionMessage(this.timeoutMessage);
        return SUCCESS;
    }

    @SkipValidation
    public String logoutForm() {
        // step1 - handle action message
        this.handleActionMessage(this.logoutMessage);
        return SUCCESS;
    }

    @SkipValidation
    public String logout() {
        return NONE;
    }

}