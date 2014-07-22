package com.payeasy.core.acl.web.security.securechannel;

import java.io.IOException;
import java.util.Iterator;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.springframework.security.ConfigAttribute;
import org.springframework.security.ConfigAttributeDefinition;
import org.springframework.security.intercept.web.FilterInvocation;
import org.springframework.security.securechannel.SecureChannelProcessor;
import org.springframework.util.Assert;

@SuppressWarnings("unchecked")
public class SSLEnabledSecureChannelProcessor extends SecureChannelProcessor {

    public void decide(FilterInvocation invocation, ConfigAttributeDefinition config)
        throws IOException, ServletException {
        Assert.isTrue((invocation != null) && (config != null), "Nulls cannot be provided");

        Iterator iter = config.getConfigAttributes().iterator();

        while (iter.hasNext()) {
            ConfigAttribute attribute = (ConfigAttribute) iter.next();

            if (this.supports(attribute)) {
                if (!this.isSSLEnabled(invocation.getHttpRequest())) {
                    this.getEntryPoint().commence(invocation.getRequest(), invocation.getResponse());
                }
            }
        }
    }

    private boolean isSSLEnabled(HttpServletRequest request) {
        if (request.getHeader("SSLEnabled") != null || request.isSecure()) {
            return true;
        } else {
            return false;
        }
    }
}
