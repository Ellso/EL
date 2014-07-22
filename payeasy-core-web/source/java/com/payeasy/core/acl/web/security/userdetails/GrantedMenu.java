package com.payeasy.core.acl.web.security.userdetails;

import java.io.Serializable;

public interface GrantedMenu extends Serializable {

    public long getMenuNum();

    public long getMenuParent();

    public String getMenuName();

    public boolean getMenuLeaf();

    public String getMenuUrl();

}
