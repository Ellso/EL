package com.payeasy.core.acl.web.security.event.authentication;

public enum AclAuthenticationEventActType {

    AUTHENTICATION("11"), PASSWORD("13");

    private String value;

    private AclAuthenticationEventActType(String value) {
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

}