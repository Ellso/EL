package com.payeasy.core.acl.web.security;

import org.springframework.security.AuthenticationServiceException;

public class OtpServiceException extends AuthenticationServiceException {

    private static final long serialVersionUID = -262624225297851533L;

    public OtpServiceException(String msg) {
        super(msg);
    }

    public OtpServiceException(String msg, Throwable t) {
        super(msg, t);
    }

}
