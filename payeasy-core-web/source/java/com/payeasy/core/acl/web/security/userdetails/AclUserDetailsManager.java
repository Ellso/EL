package com.payeasy.core.acl.web.security.userdetails;

import java.io.Serializable;

import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;

public interface AclUserDetailsManager extends UserDetailsService, Serializable {

    public UserDetails loadUserByUsername(String username);

    public boolean isPasswordValid(UserDetails userDetails, String rawPass);

    public void changePasswordByUsername(String username, String newPassword, String oldPassword);

    public void resetPasswordCountByUsername(String username);

    public void addPasswordCountByUsername(String username);

    public void lockUserByUsername(String username);

}
