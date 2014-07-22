package com.payeasy.core.acl.web.security.userdetails;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.util.Assert;

public class AclUserDetails implements UserDetails {

    private static final long serialVersionUID = 4613383697559207122L;

    private final long usernum;
    private final String name;
    private final String username;
    private final String password;
    private final String userregno;
    private final String usertel;
    private final String userfax;
    private final String useremail;
    private final String usermtel;
    private final long passwordCount;
    private final boolean enabled;
    private final boolean accountNonLocked;
    private final boolean accountNonExpired;
    private final boolean credentialsNonExpired;

    private final AclGrantedAuthority[] authorities;
    private final AclGrantedMenu[] menus;

    public AclUserDetails(long usernum, String name, String username,
            String password, String userregno, String usertel, String userfax,
            String useremail, String usermtel, long passwordCount, boolean enabled,
            boolean accountNonLocked, boolean accountNonExpired, boolean credentialsNonExpired,
            AclGrantedAuthority[] authorities, AclGrantedMenu[] menus) throws IllegalArgumentException {

        Assert.notNull(usernum, "Cannot pass a null [usernum] to constructor");
        Assert.hasText(name, "Cannot pass a blank text [name] to constructor");
        Assert.hasText(username, "Cannot pass a blank text [username] to constructor");
        Assert.hasText(password, "Cannot pass a blank text [password] to constructor");
        Assert.notNull(authorities, "Cannot pass a null AclGrantedAuthority array to constructor");
        Assert.notNull(menus, "Cannot pass a null menus array to constructor");

        this.usernum = usernum;
        this.name = name;
        this.username = username;
        this.password = password;
        this.userregno = userregno;
        this.usertel = usertel;
        this.userfax = userfax;
        this.useremail = useremail;
        this.usermtel = usermtel;
        this.passwordCount = passwordCount;
        this.enabled = enabled;
        this.accountNonLocked = accountNonLocked;
        this.accountNonExpired = accountNonExpired;
        this.credentialsNonExpired = credentialsNonExpired;
        this.authorities = authorities;
        this.menus = menus;
    }

    public long getUsernum() {
        return this.usernum;
    }

    public String getName() {
        return this.name;
    }

    public String getUsername() {
        return this.username;
    }

    public String getPassword() {
        return this.password;
    }

    public String getUserregno() {
        return this.userregno;
    }

    public String getUsertel() {
        return this.usertel;
    }

    public String getUserfax() {
        return this.userfax;
    }

    public String getUseremail() {
        return this.useremail;
    }

    public String getUsermtel() {
        return this.usermtel;
    }

    public long getPasswordCount() {
        return this.passwordCount;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public boolean isAccountNonLocked() {
        return this.accountNonLocked;
    }

    public boolean isAccountNonExpired() {
        return this.accountNonExpired;
    }

    public boolean isCredentialsNonExpired() {
        return this.credentialsNonExpired;
    }

    public AclGrantedAuthority[] getAuthorities() {
        return this.authorities;
    }

    public AclGrantedMenu[] getMenus() {
        return this.menus;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof AclUserDetails)) {
            return false;
        }

        AclUserDetails castOther = (AclUserDetails) other;

        return new EqualsBuilder()
                .append(this.usernum, castOther.usernum)
                .append(this.name, castOther.name)
                .append(this.username, castOther.username)
                .append(this.password, castOther.password)
                .append(this.userregno, castOther.userregno)
                .append(this.usertel, castOther.usertel)
                .append(this.userfax, castOther.userfax)
                .append(this.useremail, castOther.useremail)
                .append(this.usermtel, castOther.usermtel)
                .append(this.passwordCount, castOther.passwordCount)
                .append(this.enabled, castOther.enabled)
                .append(this.accountNonLocked, castOther.accountNonLocked)
                .append(this.accountNonExpired, castOther.accountNonExpired)
                .append(this.credentialsNonExpired, castOther.credentialsNonExpired)
                .append(this.authorities, castOther.authorities)
                .append(this.menus, castOther.menus)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.usernum)
                .append(this.name)
                .append(this.username)
                .append(this.password)
                .append(this.userregno)
                .append(this.usertel)
                .append(this.userfax)
                .append(this.useremail)
                .append(this.usermtel)
                .append(this.passwordCount)
                .append(this.enabled)
                .append(this.accountNonLocked)
                .append(this.accountNonExpired)
                .append(this.credentialsNonExpired)
                .append(this.authorities)
                .append(this.menus)
                .toHashCode();
    }

    @Override
    public String toString() {
        return this.toString(ToStringStyle.MULTI_LINE_STYLE);
    }

    public String toString(ToStringStyle style) {

        StringBuffer authorityBuffer = new StringBuffer();

        if (this.authorities != null) {
            authorityBuffer.append("[");
            for (int i = 0; i < this.authorities.length; i++) {
                if (i > 0) {
                    authorityBuffer.append(", ");
                }
                authorityBuffer.append("\n    " + this.authorities[i].toString());
            }
            authorityBuffer.append("\n  ]");
        } else {
            authorityBuffer.append("Not granted any authorities");
        }

        StringBuffer menuBuffer = new StringBuffer();

        if (this.menus != null) {
            for (int i = 0; i < this.menus.length; i++) {
                if (i > 0) {
                    menuBuffer.append(", ");
                }

                menuBuffer.append("\n" + this.menus[i].toString());
            }

        } else {
            authorityBuffer.append("Not granted any menus");
        }

        return new ToStringBuilder(this, style)
                .append("Usernum", this.usernum)
                .append("Name", this.name)
                .append("Username", this.username)
                .append("Password", "[PROTECTED]")
                .append("Regno", this.userregno)
                .append("Tel", this.usertel)
                .append("Fax", this.userfax)
                .append("Email", this.useremail)
                .append("Mtel", this.usermtel)
                .append("PasswordCount", this.passwordCount)
                .append("Enabled", this.enabled)
                .append("AccountNonLocked", this.accountNonLocked)
                .append("AccountNonExpired", this.accountNonExpired)
                .append("credentialsNonExpired", this.credentialsNonExpired)
                .append("Granted Authorities", authorityBuffer.toString())
                .append("Granted Menus", menuBuffer.toString())
                .toString();
    }
}