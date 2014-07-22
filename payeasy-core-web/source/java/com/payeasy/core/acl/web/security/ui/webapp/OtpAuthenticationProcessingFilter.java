package com.payeasy.core.acl.web.security.ui.webapp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.util.TextUtils;
import org.springframework.util.Assert;

import com.payeasy.core.acl.web.security.providers.OtpAuthenticationToken;

public class OtpAuthenticationProcessingFilter extends AuthenticationProcessingFilter {

    public static final String SPRING_SECURITY_FORM_TOKEN_KEY = "j_token";
    public static final String SPRING_SECURITY_FORM_SERIAL_KEY = "j_serial";

    private String tokenParameter = SPRING_SECURITY_FORM_TOKEN_KEY;
    private String serialParameter = SPRING_SECURITY_FORM_SERIAL_KEY;

    public String getTokenParameter() {
        return this.tokenParameter;
    }

    public void setTokenParameter(String tokenParameter) {
        this.tokenParameter = tokenParameter;
    }

    public String getSerialParameter() {
        return this.serialParameter;
    }

    public void setSerialParameter(String serialParameter) {
        this.serialParameter = serialParameter;
    }

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.hasLength(this.tokenParameter, "tokenParameter is required");
        Assert.hasLength(this.serialParameter, "serialParameter is required");
    }

    public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
        // Place the last username attempted into HttpSession for views
        HttpSession session = request.getSession(false);

        String username = this.obtainUsername(request);
        String password = this.obtainPassword(request);
        String token = this.obtainToken(request);
        String serial = this.obtainSerial(request);

        if (StringUtils.isBlank(username)) {
            username = "";
        }

        if (StringUtils.isBlank(password)) {
            password = "";
        }

        if (StringUtils.isBlank(token)) {
            token = "";
        }

        if (StringUtils.isBlank(serial)) {
            serial = "";
        }

        username = username.trim();

        OtpAuthenticationToken authRequest = new OtpAuthenticationToken(username, password, token, serial);

        if (session != null || this.getAllowSessionCreation()) {
            request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextUtils.escapeEntities(username));
        }

        // Allow subclasses to set the "details" property
        this.setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected String obtainToken(HttpServletRequest request) {
        return request.getParameter(this.tokenParameter);
    }

    protected String obtainSerial(HttpServletRequest request) {
        return request.getParameter(this.serialParameter);
    }
}
