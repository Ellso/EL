package com.payeasy.core.acl.web.security.providers;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.LockedException;
import org.springframework.util.Assert;

import com.payeasy.core.acl.web.security.userdetails.AclUserDetails;

public class AclAuthenticationProvider extends BaseAuthenticationProvider {

    private static final Log logger = LogFactory.getLog(AclAuthenticationProvider.class);

    private Long passwordCountLimit = 3l;

    public Long getPasswordCountLimit() {
        return this.passwordCountLimit;
    }

    public void setPasswordCountLimit(Long passwordCountLimit) {
        this.passwordCountLimit = passwordCountLimit;
    }

    protected void doAfterPropertiesSet() throws Exception {
        super.doAfterPropertiesSet();
        Assert.notNull(this.passwordCountLimit, "passwordCountLimit is required");
        Assert.isTrue(this.passwordCountLimit > 0l, "passwordCountLimit must be greater than 0");
    }

    protected final void successfulAuthentication(Authentication authentication) {
        this.resetPasswordCountByUsername(authentication.getName());
    }

    protected final void unsuccessfulAuthentication(AuthenticationException failed) {
        if (failed instanceof BadCredentialsException) {
            Object extraInformation = failed.getExtraInformation();

            if (extraInformation instanceof AclUserDetails) {
                AclUserDetails userDetails = (AclUserDetails) extraInformation;
                String username = userDetails.getUsername();
                long passwordCount = userDetails.getPasswordCount();

                this.addPasswordCountByUsername(username);

                if (passwordCount >= (this.passwordCountLimit - 1)) {
                    this.lockUserByUsername(username);
                    throw new LockedException(this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.locked",
                            "User account is locked"), userDetails);
                }
            }
        }
    }

    private void resetPasswordCountByUsername(String username) {
        try {
            this.getUserDetailsService().resetPasswordCountByUsername(username);
        } catch (AuthenticationException e) {
            logger.error("resetPasswordCountByUsername(username = " + username
                    + ") - Catch AuthenticationException", e);
        }
    }

    private void addPasswordCountByUsername(String username) {
        try {
            this.getUserDetailsService().addPasswordCountByUsername(username);
        } catch (AuthenticationException e) {
            logger.error("addPasswordCountByUsername(username = " + username
                    + ") - Catch AuthenticationException", e);
        }
    }

    private void lockUserByUsername(String username) {
        try {
            this.getUserDetailsService().lockUserByUsername(username);
        } catch (AuthenticationException e) {
            logger.error("lockUserByUsername(username = " + username
                    + ") - Catch AuthenticationException", e);
        }
    }

}