package com.payeasy.core.acl.web.struts.actions;

import java.util.Properties;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.util.Assert;

import com.payeasy.core.acl.web.struts.AclActionSupport;

public class AbstractErrorMessageAction extends AclActionSupport implements InitializingBean {

    private static final long serialVersionUID = -877337677190147870L;

    private boolean useI18n = true;
    private String defaultExceptionMessage;
    private Properties exceptionMessageMappings;

    public void setUseI18n(boolean useI18n) {
        this.useI18n = useI18n;
    }

    public void setDefaultExceptionMessage(String defaultExceptionMessage) {
        this.defaultExceptionMessage = defaultExceptionMessage;
    }

    public void setExceptionMessageMappings(Properties exceptionMessageMappings) {
        this.exceptionMessageMappings = exceptionMessageMappings;
    }

    public void afterPropertiesSet() throws Exception {
        Assert.hasText(this.defaultExceptionMessage, "defaultExceptionMessage must have text");
        Assert.notEmpty(this.exceptionMessageMappings, "exceptionMessageMappings must not be empty");
    }

    protected void handleErrorMessage(String exceptionClassName) {
        this.handleActionMessage(this.exceptionMessageMappings.getProperty(
                exceptionClassName, this.defaultExceptionMessage));
    }

    protected void handleActionMessage(String message) {
        if (this.useI18n) {
            this.addActionMessage(this.getText(message));
        } else {
            this.addActionMessage(message);
        }
    }
}
