package com.payeasy.core.acl.web.struts.interceptor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts2.ServletActionContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.AccessDeniedException;
import org.springframework.security.Authentication;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.AbstractInterceptor;
import com.payeasy.core.acl.web.security.event.authorization.StrutsAuthorizationFailureEvent;

public class SpringRolesInterceptor extends AbstractInterceptor implements ApplicationEventPublisherAware {

    private static final long serialVersionUID = 1382589425748729805L;
    private static final List<String> EMPTY_LIST = new ArrayList<String>();
    private static final String FORMAT_SECURE_OBJECT = "ActionInvocation: Name: %s; URL: %s";
    private static final String FORMAT_CONFIG_ATTRIBUTE = "AllowedRoles: %s; DisallowedRoles: %s";

    private ApplicationEventPublisher applicationEventPublisher;
    private List<String> allowedRoles = new ArrayList<String>();
    private List<String> disallowedRoles = new ArrayList<String>();

    public void setAllowedRoles(String roles) {
        this.allowedRoles = this.stringToList(roles);
    }

    public void setDisallowedRoles(String roles) {
        this.disallowedRoles = this.stringToList(roles);
    }

    public void setApplicationEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public String intercept(ActionInvocation invocation) throws Exception {
        HttpServletRequest request = ServletActionContext.getRequest();
        HttpServletResponse response = ServletActionContext.getResponse();

        String result = null;

        if (!this.isAllowed(request, invocation.getAction())) {
            result = this.handleRejection(invocation, request, response);
        } else {
            result = invocation.invoke();
        }

        return result;
    }

    /**
     * Splits a string into a List
     */
    protected List<String> stringToList(String val) {
        if (val != null) {
            String[] list = val.split("[ ]*,[ ]*");
            return Arrays.asList(list);
        } else {
            return EMPTY_LIST;
        }
    }

    /**
     * Determines if the request should be allowed for the action
     *
     * @param request The request
     * @param action The action object
     * @return True if allowed, false otherwise
     */
    protected boolean isAllowed(HttpServletRequest request, Object action) {
        if (this.allowedRoles.size() > 0) {
            boolean result = false;

            for (String role : this.allowedRoles) {
                if (request.isUserInRole(role)) {
                    result = true;
                }
            }

            return result;
        } else if (this.disallowedRoles.size() > 0) {
            for (String role : this.disallowedRoles) {
                if (request.isUserInRole(role)) {
                    return false;
                }
            }
        }

        return true;
    }

    /**
     * Handles a rejection by sending a 403 HTTP error
     *
     * @param invocation The invocation
     * @return The result code
     * @throws Exception
     */
    protected String handleRejection(ActionInvocation invocation, HttpServletRequest request,
            HttpServletResponse response) throws Exception {

        Authentication authentication = (Authentication) request.getUserPrincipal();
        String requestUrl = this.buildRequestUrl(request);
        String secureObject = String.format(FORMAT_SECURE_OBJECT, ActionContext.getContext().getName(), requestUrl);
        String configAttribute = String.format(FORMAT_CONFIG_ATTRIBUTE, this.allowedRoles, this.disallowedRoles);

        StrutsAuthorizationFailureEvent failureEvent = new StrutsAuthorizationFailureEvent(
                secureObject, configAttribute, authentication, new AccessDeniedException("Access is denied"));

        this.applicationEventPublisher.publishEvent(failureEvent);

        response.sendError(HttpServletResponse.SC_FORBIDDEN);
        return null;
    }

    private String buildRequestUrl(HttpServletRequest request) {
        String servletPath = request.getServletPath();
        String requestURI = request.getRequestURI();
        String contextPath = request.getContextPath();
        String pathInfo = request.getPathInfo();
        String queryString = request.getQueryString();

        String uri = servletPath;

        if (uri == null) {
            uri = requestURI;
            uri = uri.substring(contextPath.length());
        }

        return uri + ((pathInfo == null) ? "" : pathInfo) + ((queryString == null) ? "" : ("?" + queryString));
    }
}