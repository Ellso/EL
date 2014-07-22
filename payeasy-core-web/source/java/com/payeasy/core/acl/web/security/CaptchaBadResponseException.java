package com.payeasy.core.acl.web.security;

import org.springframework.security.AuthenticationException;

public class CaptchaBadResponseException extends AuthenticationException {

    private static final long serialVersionUID = 3427850957148094953L;

    public CaptchaBadResponseException(String msg) {
        super(msg);
    }

    public CaptchaBadResponseException(String msg, Object extraInformation) {
        super(msg, extraInformation);
    }

    public CaptchaBadResponseException(String msg, Throwable t) {
        super(msg, t);
    }
}
