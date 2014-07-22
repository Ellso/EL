package com.payeasy.core.base.web.struts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class ActionLinkAwareSupport implements ActionLinkAware, Serializable {

    private static final long serialVersionUID = -4184804843112271271L;

    private Collection<ActionLink> actionLinks;

    public synchronized void setActionLinks(Collection<ActionLink> actionLinks) {
        this.actionLinks = actionLinks;
    }

    public synchronized Collection<ActionLink> getActionLinks() {
        return new ArrayList<ActionLink>(this.internalGetActionLinks());
    }

    public void addActionLink(ActionLink actionLink) {
        this.internalGetActionLinks().add(actionLink);
    }

    public boolean hasActionLinks() {
        return (this.actionLinks != null) && !this.actionLinks.isEmpty();
    }

    private Collection<ActionLink> internalGetActionLinks() {
        if (this.actionLinks == null) {
            this.actionLinks = new ArrayList<ActionLink>();
        }

        return this.actionLinks;
    }
}
