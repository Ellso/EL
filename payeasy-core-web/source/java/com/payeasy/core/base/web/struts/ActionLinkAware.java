package com.payeasy.core.base.web.struts;

import java.util.Collection;

public interface ActionLinkAware {
    
    /**
     * Set the Collection of Action-level link.
     *
     * @param actionLinks
     */
    void setActionLinks(Collection<ActionLink> actionLinks);

    /**
     * Get the Collection of Action-level links for this action. Action links should not
     * be added directly here, as implementations are free to return a new Collection or an
     * Unmodifiable Collection.
     *
     * @return Collection of String action links
     */
    Collection<ActionLink> getActionLinks();
    
    /**
     * Add an Action-level link to this Action.
     */
    void addActionLink(ActionLink actionLink);
    
    /**
     * Checks whether there are any Action-level action links.
     *
     * @return true if any Action-level action links have been registered
     */
    boolean hasActionLinks();
    
}
