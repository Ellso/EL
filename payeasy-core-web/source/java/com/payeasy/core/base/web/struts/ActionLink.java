package com.payeasy.core.base.web.struts;

import java.io.Serializable;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

public class ActionLink implements Serializable {

    private static final long serialVersionUID = -7806812694582451334L;

    private boolean includeContext;
    private String linkLabel;
    private String linkHref;

    public ActionLink() {
    }

    public ActionLink(boolean includeContext, String linkLabel, String linkHref) {
        this.includeContext = includeContext;
        this.linkLabel = linkLabel;
        this.linkHref = linkHref;
    }

    public boolean isIncludeContext() {
        return this.includeContext;
    }

    public void setIncludeContext(boolean includeContext) {
        this.includeContext = includeContext;
    }

    public String getLinkLabel() {
        return this.linkLabel;
    }

    public void setLinkLabel(String linkLabel) {
        this.linkLabel = linkLabel;
    }

    public String getLinkHref() {
        return this.linkHref;
    }

    public void setLinkHref(String linkHref) {
        this.linkHref = linkHref;
    }

    @Override
    public String toString() {
        return this.toString(ToStringStyle.MULTI_LINE_STYLE);
    }

    /**
     * you can use the following styles:
     * <li>ToStringStyle.DEFAULT_STYLE</li>
     * <li>ToStringStyle.MULTI_LINE_STYLE</li>
     * <li>ToStringStyle.NO_FIELD_NAMES_STYLE</li>
     * <li>ToStringStyle.SHORT_PREFIX_STYLE</li>
     * <li>ToStringStyle.SIMPLE_STYLE</li>
     * @param style ToStringStyle
     * @return String
     */
    public String toString(ToStringStyle style) {
        return new ToStringBuilder(this, style)
                .append("includeContext", this.isIncludeContext())
                .append("linkLabel", this.getLinkLabel())
                .append("linkHref", this.getLinkHref())
                .toString();
    }
}
