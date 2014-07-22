package com.payeasy.core.acl.web.struts.actions;

public class OtpLoginAction extends LoginAction {

    private static final long serialVersionUID = -3379132243328460393L;

    // struts form fields
    private String token;
    private String serial;

    public String getToken() {
        return this.token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getSerial() {
        return this.serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
