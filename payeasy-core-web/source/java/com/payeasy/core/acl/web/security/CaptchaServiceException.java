package com.payeasy.core.acl.web.security;

import org.springframework.security.AuthenticationServiceException;

public class CaptchaServiceException extends AuthenticationServiceException {

    private static final long serialVersionUID = -3923970549196850831L;

    public CaptchaServiceException(String msg) {
        super(msg);
    }

    public CaptchaServiceException(String msg, Throwable t) {
        super(msg, t);
    }
}
