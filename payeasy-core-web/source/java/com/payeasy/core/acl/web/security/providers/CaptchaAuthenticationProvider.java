package com.payeasy.core.acl.web.security.providers;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.util.Assert;

import com.octo.captcha.service.CaptchaService;
import com.octo.captcha.service.CaptchaServiceException;
import com.payeasy.core.acl.web.security.CaptchaBadResponseException;

@SuppressWarnings("unchecked")
public class CaptchaAuthenticationProvider extends AclAuthenticationProvider {

    private CaptchaService captchaService;

    public void setCaptchaService(CaptchaService captchaService) {
        this.captchaService = captchaService;
    }

    public boolean supports(Class authentication) {
        return (CaptchaAuthenticationToken.class.isAssignableFrom(authentication));
    }

    protected void doAfterPropertiesSet() throws Exception {
        super.doAfterPropertiesSet();
        Assert.notNull(this.captchaService, "captchaService is required");
    }

    protected final void preAuthentication(Authentication authentication) {
        Assert.isInstanceOf(CaptchaAuthenticationToken.class, authentication,
                this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports",
                        "Only CaptchaAuthenticationToken is supported"));

        this.validateCaptcha((CaptchaAuthenticationToken) authentication);
    }

    private void validateCaptcha(CaptchaAuthenticationToken authentication) {

        String sessionId = authentication.getSessionId();
        String captcha = authentication.getCaptcha();

        if (StringUtils.isBlank(sessionId)) {
            throw new CaptchaBadResponseException("SessionId is blank");
        }

        if (StringUtils.isBlank(captcha)) {
            throw new CaptchaBadResponseException("Captcha is blank");
        }

        boolean result = false;

        try {
            result = this.captchaService.validateResponseForID(sessionId, captcha);
        } catch (CaptchaServiceException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }

        if (!result) {
            throw new CaptchaBadResponseException("Bad captcha");
        }
    }

}