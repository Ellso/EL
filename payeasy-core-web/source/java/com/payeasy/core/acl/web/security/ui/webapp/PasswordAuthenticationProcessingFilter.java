package com.payeasy.core.acl.web.security.ui.webapp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationCredentialsNotFoundException;
import org.springframework.security.AuthenticationException;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.ui.AbstractProcessingFilter;
import org.springframework.security.ui.FilterChainOrder;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.util.TextUtils;
import org.springframework.util.Assert;

import com.payeasy.core.acl.web.security.providers.PasswordAuthenticationToken;

public class PasswordAuthenticationProcessingFilter extends AbstractProcessingFilter {

    public static final String SPRING_SECURITY_FORM_OLD_PASSWORD_KEY = "j_old_password";
    public static final String SPRING_SECURITY_FORM_NEW_PASSWORD_KEY = "j_old_password";
    public static final String SPRING_SECURITY_LAST_USERNAME_KEY = AuthenticationProcessingFilter.SPRING_SECURITY_LAST_USERNAME_KEY;

    private String oldPasswordParameter = SPRING_SECURITY_FORM_OLD_PASSWORD_KEY;
    private String newPasswordParameter = SPRING_SECURITY_FORM_NEW_PASSWORD_KEY;

    public String getOldPasswordParameter() {
        return this.oldPasswordParameter;
    }

    public void setOldPasswordParameter(String oldPasswordParameter) {
        this.oldPasswordParameter = oldPasswordParameter;
    }

    public String getNewPasswordParameter() {
        return this.newPasswordParameter;
    }

    public void setNewPasswordParameter(String newPasswordParameter) {
        this.newPasswordParameter = newPasswordParameter;
    }

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.hasLength(this.oldPasswordParameter, "oldPasswordParameter is required");
        Assert.hasLength(this.newPasswordParameter, "newPasswordParameter is required");
    }

    public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
        HttpSession session = request.getSession(false);

        if (session == null && this.getAllowSessionCreation()) {
            session = request.getSession(true);
        }

        if (session == null || session.getAttribute(SPRING_SECURITY_LAST_USERNAME_KEY) == null) {
            throw new AuthenticationCredentialsNotFoundException("Can't change password as no last username key in session");
        }

        String username = (String) session.getAttribute(SPRING_SECURITY_LAST_USERNAME_KEY);
        String oldPassword = this.obtainOldPassword(request);
        String newPassword = this.obtainNewPassword(request);

        if (StringUtils.isBlank(username)) {
            username = "";
        }

        if (StringUtils.isBlank(oldPassword)) {
            oldPassword = "";
        }

        if (StringUtils.isBlank(newPassword)) {
            newPassword = "";
        }

        username = username.trim();

        PasswordAuthenticationToken authRequest = new PasswordAuthenticationToken(username, newPassword, oldPassword);

        if (session != null || this.getAllowSessionCreation()) {
            request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextUtils.escapeEntities(username));
        }

        // Allow subclasses to set the "details" property
        this.setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    public String getDefaultFilterProcessesUrl() {
        return "/j_spring_security_check";
    }

    public int getOrder() {
        return FilterChainOrder.AUTHENTICATION_PROCESSING_FILTER;
    }

    protected void setDetails(HttpServletRequest request, UsernamePasswordAuthenticationToken authRequest) {
        authRequest.setDetails(this.authenticationDetailsSource.buildDetails(request));
    }

    protected String obtainOldPassword(HttpServletRequest request) {
        return request.getParameter(this.oldPasswordParameter);
    }

    protected String obtainNewPassword(HttpServletRequest request) {
        return request.getParameter(this.newPasswordParameter);
    }

}