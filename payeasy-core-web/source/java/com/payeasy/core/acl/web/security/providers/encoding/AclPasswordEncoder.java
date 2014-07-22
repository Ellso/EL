package com.payeasy.core.acl.web.security.providers.encoding;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.providers.encoding.PasswordEncoder;
import org.springframework.util.Assert;

import com.payeasy.acl.authentication.service.delegate.AuthenticationBD;
import com.payeasy.acl.authentication.service.exception.AclAuthenticationException;

public class AclPasswordEncoder implements PasswordEncoder, InitializingBean {

    private AuthenticationBD authenticationBD;

    public void setAuthenticationBD(AuthenticationBD authenticationBD) {
        this.authenticationBD = authenticationBD;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.notNull(this.authenticationBD, "authenticationBD is required");
    }

    public String encodePassword(String rawPass, Object salt) {
        try {
            return this.authenticationBD.encodePassword(rawPass);
        } catch (AclAuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

    public boolean isPasswordValid(String encPass, String rawPass, Object salt) {
        try {
            return this.authenticationBD.isPasswordValid(encPass, rawPass);
        } catch (AclAuthenticationException e) {
            throw new AuthenticationServiceException(e.getMessage(), e);
        }
    }

}
