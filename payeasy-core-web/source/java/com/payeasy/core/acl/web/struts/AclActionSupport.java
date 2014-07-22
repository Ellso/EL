package com.payeasy.core.acl.web.struts;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.security.Authentication;
import org.springframework.security.AuthenticationTrustResolver;
import org.springframework.security.AuthenticationTrustResolverImpl;
import org.springframework.security.GrantedAuthority;
import org.springframework.security.ui.switchuser.SwitchUserProcessingFilter;

import com.payeasy.core.acl.web.security.event.authentication.AuthenticationAuditEvent;
import com.payeasy.core.acl.web.security.userdetails.AclUserDetails;
import com.payeasy.core.base.web.struts.BaseActionSupport;

public class AclActionSupport extends BaseActionSupport {

    private static final long serialVersionUID = 6880873208342778278L;

    private static final List<String> EMPTY_ACL_PERMISSIONS = new ArrayList<String>();

    private AuthenticationTrustResolver authenticationTrustResolver = new AuthenticationTrustResolverImpl();

    public void setAuthenticationTrustResolver(AuthenticationTrustResolver authenticationTrustResolver) {
        this.authenticationTrustResolver = authenticationTrustResolver;
    }

    public Authentication getAuthentication() {
        return (Authentication) this.getRequest().getUserPrincipal();
    }

    public AclUserDetails getAclUserDetails() {
        Authentication authentication = this.getAuthentication();
        return (AclUserDetails) authentication.getPrincipal();
    }

    public List<String> getAclPermissions() {
        Authentication authentication = this.getAuthentication();

        if (authentication == null) {
            return EMPTY_ACL_PERMISSIONS;
        }

        GrantedAuthority[] authorities = authentication.getAuthorities();

        if (ArrayUtils.isEmpty(authorities)) {
            return EMPTY_ACL_PERMISSIONS;
        }

        List<String> aclPermissions = new ArrayList<String>();

        for (GrantedAuthority authoritie : authorities) {
            aclPermissions.add(authoritie.toString());
        }

        return aclPermissions;
    }

    public boolean isAnonymousUser() {
        return this.authenticationTrustResolver.isAnonymous(this.getAuthentication());
    }

    public boolean isRememberMeUser() {
        return this.authenticationTrustResolver.isRememberMe(this.getAuthentication());
    }

    public boolean isSwitchUser() {
        return this.getAclPermissions().contains(SwitchUserProcessingFilter.ROLE_PREVIOUS_ADMINISTRATOR);
    }

    public void publishAuthenticationAuditEvent(String message) {
        super.publishEvent(new AuthenticationAuditEvent(this.getAuthentication(), message));
    }

}