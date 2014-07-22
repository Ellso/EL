package com.payeasy.core.acl.web.struts.actions;

import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.security.AuthenticationException;

import com.payeasy.core.acl.web.security.ui.switcuser.AclSwitchUserProcessingFilter;

public class SwitchAction extends AbstractErrorMessageAction {

    private static final long serialVersionUID = -2276530578820066021L;

    // struts form fields
    private String username;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @SkipValidation
    public String switchForm() {
        return SUCCESS;
    }

    public String switchLogin() {
        return NONE;
    }

    @SkipValidation
    public String switchLogout() {
        return NONE;
    }

    @SkipValidation
    public String switchError() {

        HttpSession session = this.getSession(true);

        // step1 - check login failed message
        AuthenticationException exception = (AuthenticationException) session.getAttribute(AclSwitchUserProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);

        // step2 - validate exception is null
        if (exception == null) {
            return SUCCESS;
        }

        // step4 - handle action message
        this.handleErrorMessage(exception.getClass().getName());

        return SUCCESS;
    }
}
