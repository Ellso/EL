package com.payeasy.core.acl.web.security.userdetails;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.util.Assert;

public class AclGrantedMenu implements GrantedMenu {

    private static final long serialVersionUID = -8849519709866044877L;

    private final long menuNum;
    private final long menuParent;
    private final boolean menuLeaf;
    private final String menuName;
    private final String menuUrl;

    public AclGrantedMenu(long menuNum, long menuParent, boolean menuLeaf,
            String menuName, String menuUrl) {

        Assert.notNull(menuNum, "Cannot pass a null [menuNum] to constructor");
        Assert.notNull(menuLeaf, "Cannot pass a null [menuLeaf] to constructor");
        Assert.hasText(menuName, "Cannot pass a blank text [menuName] to constructor");

        this.menuNum = menuNum;
        this.menuParent = menuParent;
        this.menuLeaf = menuLeaf;
        this.menuName = menuName;
        this.menuUrl = menuUrl;
    }

    public long getMenuNum() {
        return this.menuNum;
    }

    public long getMenuParent() {
        return this.menuParent;
    }

    public boolean getMenuLeaf() {
        return this.menuLeaf;
    }

    public String getMenuName() {
        return this.menuName;
    }

    public String getMenuUrl() {
        return this.menuUrl;
    }

    @Override
    public boolean equals(final Object other) {
        if (!(other instanceof AclGrantedMenu)) {
            return false;
        }

        AclGrantedMenu castOther = (AclGrantedMenu) other;
        return new EqualsBuilder()
                .append(this.menuNum, castOther.menuNum)
                .append(this.menuParent, castOther.menuParent)
                .append(this.menuLeaf, castOther.menuLeaf)
                .append(this.menuName, castOther.menuName)
                .append(this.menuUrl, castOther.menuUrl)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(this.menuNum)
                .append(this.menuParent)
                .append(this.menuLeaf)
                .append(this.menuName)
                .append(this.menuUrl)
                .toHashCode();
    }

    @Override
    public String toString() {
        return this.toString(ToStringStyle.MULTI_LINE_STYLE);
    }

    public String toString(ToStringStyle style) {
        return new ToStringBuilder(this, style)
                .append("menuNum", this.menuNum)
                .append("menuParent", this.menuParent)
                .append("menuLeaf", this.menuLeaf)
                .append("menuName", this.menuName)
                .append("menuUrl", this.menuUrl)
                .toString();
    }
}
