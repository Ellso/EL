package com.payeasy.core.acl.web.security;

import org.springframework.security.AuthenticationException;

public class OtpBadTokenException extends AuthenticationException {

    private static final long serialVersionUID = 7468806266045820378L;

    public OtpBadTokenException(String msg) {
        super(msg);
    }

    public OtpBadTokenException(String msg, Object extraInformation) {
        super(msg, extraInformation);
    }

    public OtpBadTokenException(String msg, Throwable t) {
        super(msg, t);
    }

}
