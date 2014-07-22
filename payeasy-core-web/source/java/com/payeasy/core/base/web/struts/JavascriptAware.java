package com.payeasy.core.base.web.struts;

import java.util.Collection;

public interface JavascriptAware {

    /**
     * Set the Collection of Action-level javascript.
     *
     * @param javascripts
     */
    void setJavascripts(Collection<String> javascripts);

    /**
     * Get the Collection of Action-level javascript for this action. Action javascript should not
     * be added directly here, as implementations are free to return a new Collection or an
     * Unmodifiable Collection.
     *
     * @return Collection of String javascripts
     */
    Collection<String> getJavascripts();
    
    /**
     * Add an Action-level javascript to this Action.
     */
    void addJavascript(String javascript);
    
    /**
     * Checks whether there are any Action-level javascripts.
     *
     * @return true if any Action-level javascripts have been registered
     */
    boolean hasJavascripts();
    
}
