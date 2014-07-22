package com.payeasy.core.acl.web.security.providers;

import org.apache.commons.lang.StringUtils;
import org.springframework.security.Authentication;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

@SuppressWarnings("unchecked")
public class PasswordAuthenticationProvider extends BaseAuthenticationProvider {

    public boolean supports(Class authentication) {
        return (PasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    protected void doAfterPropertiesSet() throws Exception {
        super.doAfterPropertiesSet();
    }

    protected void preAuthentication(Authentication authentication) {
        Assert.isInstanceOf(PasswordAuthenticationToken.class, authentication,
                this.messages.getMessage("AbstractUserDetailsAuthenticationProvider.onlySupports",
                        "Only PasswordAuthenticationToken is supported"));

        this.changePassword((PasswordAuthenticationToken) authentication);
        this.getUserCache().removeUserFromCache(authentication.getName());
    }

    private void changePassword(PasswordAuthenticationToken authentication) {

        String username = authentication.getName();
        String newPassword = (String) authentication.getCredentials();
        String oldPassword = authentication.getOldPassword();

        if (StringUtils.isBlank(username)) {
            throw new UsernameNotFoundException("Username is blank");
        }

        if (StringUtils.isBlank(newPassword)) {
            throw new BadCredentialsException("New password is blank");
        }

        if (StringUtils.isBlank(oldPassword)) {
            throw new BadCredentialsException("Old password is blank");
        }

        this.getUserDetailsService().changePasswordByUsername(username, newPassword, oldPassword);
    }

}