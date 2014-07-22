package com.payeasy.core.acl.web.security.providers;

import org.springframework.dao.DataAccessException;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationException;
import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.providers.UsernamePasswordAuthenticationToken;
import org.springframework.security.providers.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.payeasy.core.acl.web.security.userdetails.AclUserDetailsManager;

public class BaseAuthenticationProvider extends AbstractUserDetailsAuthenticationProvider {

    private AclUserDetailsManager userDetailsService;

    public AclUserDetailsManager getUserDetailsService() {
        return this.userDetailsService;
    }

    public void setUserDetailsService(AclUserDetailsManager userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    protected void doAfterPropertiesSet() throws Exception {
        super.doAfterPropertiesSet();
        Assert.notNull(this.userDetailsService, "userDetailsService is required");
    }

    protected final UserDetails retrieveUser(String username,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        UserDetails loadedUser;

        try {
            loadedUser = this.getUserDetailsService().loadUserByUsername(username);
        } catch (DataAccessException repositoryProblem) {
            throw new AuthenticationServiceException(repositoryProblem.getMessage(), repositoryProblem);
        }

        if (loadedUser == null) {
            throw new AuthenticationServiceException(
                    "UserDetailsService returned null, which is an interface contract violation");
        }
        return loadedUser;
    }

    protected final void additionalAuthenticationChecks(UserDetails userDetails,
            UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        if (authentication.getCredentials() == null) {
            throw new BadCredentialsException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
        }

        String presentedPassword = authentication.getCredentials().toString();

        if (!this.getUserDetailsService().isPasswordValid(userDetails, presentedPassword)) {
            throw new BadCredentialsException(this.messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
        }
    }

    public final Authentication authenticate(Authentication authentication) {
        Authentication authResult = null;

        try {
            this.preAuthentication(authentication);
            authResult = super.authenticate(authentication);
            this.postAuthentication(authentication);
        } catch (AuthenticationException failed) {
            this.unsuccessfulAuthentication(failed);
            throw failed;
        }

        this.successfulAuthentication(authentication);

        return authResult;
    }

    protected void preAuthentication(Authentication authentication) {
    }

    protected void postAuthentication(Authentication authentication) {
    }

    protected void successfulAuthentication(Authentication authentication) {
    }

    protected void unsuccessfulAuthentication(AuthenticationException failed) {
    }

}