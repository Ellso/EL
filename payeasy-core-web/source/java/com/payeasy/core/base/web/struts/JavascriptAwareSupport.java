package com.payeasy.core.base.web.struts;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

public class JavascriptAwareSupport  implements JavascriptAware, Serializable {

    private static final long serialVersionUID = 2648508550057959119L;
    
    private Collection<String> javascripts;

    public synchronized void setJavascripts(Collection<String> javascripts) {
        this.javascripts = javascripts;
    }

    public synchronized Collection<String> getJavascripts() {
        return new ArrayList<String>(this.internalGetJavascripts());
    }

    public void addJavascript(String javascript) {
        this.internalGetJavascripts().add(javascript);
    }

    public boolean hasJavascripts() {
        return (this.javascripts != null) && !this.javascripts.isEmpty();
    }

    private Collection<String> internalGetJavascripts() {
        if (this.javascripts == null) {
            this.javascripts = new ArrayList<String>();
        }

        return this.javascripts;
    }

}