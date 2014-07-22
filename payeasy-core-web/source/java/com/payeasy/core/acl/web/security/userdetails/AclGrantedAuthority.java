package com.payeasy.core.acl.web.security.userdetails;

import org.apache.commons.lang.builder.CompareToBuilder;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.springframework.security.GrantedAuthority;
import org.springframework.util.Assert;

public class AclGrantedAuthority implements GrantedAuthority {

    private static final long serialVersionUID = -5646057285110540843L;

    private final String authority;

    public AclGrantedAuthority(String authority) {
        Assert.hasText(authority, "Cannot pass a blank text [authority] to constructor");
        this.authority = authority;
    }

    public String getAuthority() {
        return this.authority;
    }

    public int compareTo(final Object other) {
        AclGrantedAuthority castOther = (AclGrantedAuthority) other;
        return new CompareToBuilder()
                .append(this.authority, castOther.authority)
                .toComparison();
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof AclGrantedAuthority)) {
            return false;
        }

        AclGrantedAuthority castOther = (AclGrantedAuthority) other;

        return new EqualsBuilder()
                .append(this.authority, castOther.authority)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.authority)
                .toHashCode();
    }

    @Override
    public String toString(){
        return this.getAuthority();
    }

}