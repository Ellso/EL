package com.payeasy.core.acl.web.struts.actions;

public class CaptchaLoginAction extends LoginAction {

    private static final long serialVersionUID = 415890472919888102L;

    private String captcha;

    public String getCaptcha() {
        return this.captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }
}
