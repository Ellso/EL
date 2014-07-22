package com.payeasy.core.acl.web.hdiv.logs;

import java.security.Principal;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.logs.IUserData;
import org.springframework.security.Authentication;

public class UserData implements IUserData {

    public String getUsername(HttpServletRequest request) {
        
        Principal principal = request.getUserPrincipal();
        
        if (principal instanceof Authentication) {
            return ((Authentication) principal).getName();
        }
        
        return (principal == null) ? "NONE_PROVIDED" : principal.toString();
    }

}
