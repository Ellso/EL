package com.payeasy.core.acl.web.struts.actions;

import javax.servlet.http.HttpSession;

import org.apache.struts2.interceptor.validation.SkipValidation;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;

public class PasswordAction extends AbstractErrorMessageAction {

    private static final long serialVersionUID = 3964451973481926892L;

    // struts form fields
    private String username;
    private String oldPassword;
    private String newPassword;
    private String confirmPassword;

    // message fields
    private String passwordMessage;

    public String getUsername() {
        return this.username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getOldPassword() {
        return this.oldPassword;
    }

    public void setOldPassword(String oldPassword) {
        this.oldPassword = oldPassword;
    }

    public String getNewPassword() {
        return this.newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    public String getConfirmPassword() {
        return this.confirmPassword;
    }

    public void setConfirmPassword(String confirmPassword) {
        this.confirmPassword = confirmPassword;
    }

    public String getPasswordMessage() {
        return this.passwordMessage;
    }

    public void setPasswordMessage(String passwordMessage) {
        this.passwordMessage = passwordMessage;
    }

    @SkipValidation
    public String passwordForm() {

        HttpSession session = this.getSession(true);

        // step1 - retrieve spring security username
        String username = (String) session.getAttribute(AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY);

        // step2 - check username, if null redirect to login timeout
        if (username == null) {
            return REDIRECT;
        }

        // step3 - clear spring security setting
        session.removeAttribute(AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);

        // step4 - reset form fields
        this.setUsername(username);

        // step5 - handle action message
        if (this.getAuthentication() == null) { // 表示為登入時，密碼過期
            this.handleActionMessage(this.passwordMessage);
        }

        return SUCCESS;
    }

    public String passwordUpdate() {
        return NONE;
    }

    public String passwordError() {
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
        this.setOldPassword(null);
        this.setNewPassword(null);
        this.setConfirmPassword(null);

        // step6 - handle action message
        this.handleErrorMessage(exceptionClassName);

        return SUCCESS;
    }

}