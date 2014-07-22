package com.payeasy.core.base.web.struts;

import java.text.MessageFormat;
import java.util.Collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionSupport;

public class BaseActionSupport extends ActionSupport implements ActionLinkAware, JavascriptAware, ApplicationEventPublisherAware {

    private static final long serialVersionUID = -3841209768866084475L;

    public static final String MESSAGE = "message";
    public static final String REDIRECT = "redirect";
    public static final String ACCESS_DENIED = "accessDenied";

    private final ActionLinkAwareSupport actionLinkAware = new ActionLinkAwareSupport();
    private final JavascriptAwareSupport javascriptAwareSupport = new JavascriptAwareSupport();

    private ApplicationEventPublisher applicationEventPublisher;

    public void setActionLinks(Collection<ActionLink> actionLinks) {
        this.actionLinkAware.setActionLinks(actionLinks);
    }

    public Collection<ActionLink> getActionLinks() {
        return this.actionLinkAware.getActionLinks();
    }

    public void addPreviousActionLink() {
        this.addActionLink(false, "¦^¤W¤@­¶", "javascript:history.go(-1);");
    }

    public void addActionLink(boolean includeContext, String linkLabel, String linkHref) {
        this.addActionLink(new ActionLink(includeContext, linkLabel, linkHref));
    }

    public void addActionLink(ActionLink actionLink) {
        this.actionLinkAware.addActionLink(actionLink);
    }

    public boolean hasActionLinks() {
        return this.actionLinkAware.hasActionLinks();
    }

    public void setJavascripts(Collection<String> javascripts) {
        this.javascriptAwareSupport.setJavascripts(javascripts);
    }

    public Collection<String> getJavascripts() {
        return this.javascriptAwareSupport.getJavascripts();
    }

    public void addJavascript(String javascript) {
        this.javascriptAwareSupport.addJavascript(javascript);
    }
    
    public boolean hasJavascripts() {
        return this.javascriptAwareSupport.hasJavascripts();
    }
    
    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public String getFormatValue(String expression, String pattern) {
        // why use value statck to get value, see conversion error interceptor.
        // when conversion error occur, interceptor put use input value to fake.
        Object expressionValue = ActionContext.getContext().getValueStack().findValue(expression);

        if (expressionValue != null) {
            if (expressionValue instanceof String) {
                return (String) expressionValue;
            } else {
                return MessageFormat.format(pattern, expressionValue);
            }
        }

        return null;
    }

    public HttpServletRequest getRequest() {
        return ServletActionContext.getRequest();
    }

    public HttpServletResponse getResponse() {
        return ServletActionContext.getResponse();
    }

    public HttpSession getSession() {
        return this.getRequest().getSession();
    }

    public HttpSession getSession(boolean create) {
        return this.getRequest().getSession(create);
    }

    public void publishEvent(ApplicationEvent event) {
        if (this.applicationEventPublisher != null) {
            this.applicationEventPublisher.publishEvent(event);
        }
    }

}