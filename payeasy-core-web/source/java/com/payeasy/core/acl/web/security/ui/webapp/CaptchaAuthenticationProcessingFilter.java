package com.payeasy.core.acl.web.security.ui.webapp;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.ui.webapp.AuthenticationProcessingFilter;
import org.springframework.security.util.TextUtils;
import org.springframework.util.Assert;

import com.payeasy.core.acl.web.security.providers.CaptchaAuthenticationToken;

public class CaptchaAuthenticationProcessingFilter extends AuthenticationProcessingFilter {

    public static final String SPRING_SECURITY_FORM_CAPTCHA_KEY = "j_captcha";

    private String captchaParameter = SPRING_SECURITY_FORM_CAPTCHA_KEY;

    public String getCaptchaParameter() {
        return this.captchaParameter;
    }

    public void setCaptchaParameter(String captchaParameter) {
        this.captchaParameter = captchaParameter;
    }

    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        Assert.hasLength(this.captchaParameter, "captchaParameter is required");
    }

    public Authentication attemptAuthentication(HttpServletRequest request) throws AuthenticationException {
        HttpSession session = request.getSession(false);

        if (session == null && this.getAllowSessionCreation()) {
            session = request.getSession(true);
        }

        String username = this.obtainUsername(request);
        String password = this.obtainPassword(request);
        String captcha = this.obtainCaptcha(request);
        String sessionId = (session == null) ? null : session.getId();

        if (StringUtils.isBlank(username)) {
            username = "";
        }

        if (StringUtils.isBlank(password)) {
            password = "";
        }

        if (StringUtils.isBlank(captcha)) {
            captcha = "";
        }

        if (StringUtils.isBlank(sessionId)) {
            sessionId = "";
        }

        username = username.trim();

        CaptchaAuthenticationToken authRequest = new CaptchaAuthenticationToken(username, password, sessionId, captcha);

        if (session != null || this.getAllowSessionCreation()) {
            request.getSession().setAttribute(SPRING_SECURITY_LAST_USERNAME_KEY, TextUtils.escapeEntities(username));
        }

        // Allow subclasses to set the "details" property
        this.setDetails(request, authRequest);

        return this.getAuthenticationManager().authenticate(authRequest);
    }

    protected String obtainCaptcha(HttpServletRequest request) {
        return request.getParameter(this.captchaParameter);
    }

}