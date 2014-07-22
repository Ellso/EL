package com.payeasy.core.base.web.displaytag.localization;

import java.util.Collections;
import java.util.Iterator;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.Tag;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.struts2.views.jsp.TagUtils;
import org.displaytag.Messages;
import org.displaytag.localization.I18nResourceProvider;
import org.displaytag.localization.LocaleResolver;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.LocaleProvider;
import com.opensymphony.xwork2.TextProvider;
import com.opensymphony.xwork2.util.OgnlValueStack;
import com.opensymphony.xwork2.util.ValueStack;

/**
 * Struts2/Webwork2 implementation of a resource provider and locale resolver.<br/>
 * # Set this values in displaytag.properties #<br/>
 * <code>
 * locale.resolver = org.displaytag.localization.I18nWebwork2Adapter<br/>
 * locale.provider = org.displaytag.localization.I18nWebwork2Adapter<br/>
 * </code>
 *
 * @author Ronald
 * @version 1.0
 */
@SuppressWarnings("unchecked")
public class I18nWebwork2Adapter implements LocaleResolver, I18nResourceProvider {

    public static final String UNDEFINED_KEY = "???";

    private static Log log = LogFactory.getLog(I18nWebwork2Adapter.class);

    public Locale resolveLocale(HttpServletRequest request) {

       Locale result = null;
       ValueStack stack = ActionContext.getContext().getValueStack();

       Iterator iterator = stack.getRoot().iterator();

       while (iterator.hasNext()) {
           Object object = iterator.next();

           if (object instanceof LocaleProvider) {
               LocaleProvider lp = (LocaleProvider) object;
               result = lp.getLocale();
               break;
           }
       }

       if (result == null) {
           log.debug("Missing LocalProvider actions, init locale to default");
           result = Locale.getDefault();
       }

       return result;
   }

    public String getResource(String resourceKey, String defaultValue, Tag tag,
            PageContext pageContext) {

        String key = (resourceKey != null) ? resourceKey : defaultValue;

        String message = null;
        OgnlValueStack stack = (OgnlValueStack) TagUtils.getStack(pageContext);

        Iterator iterator = stack.getRoot().iterator();

        while (iterator.hasNext()) {
            Object o = iterator.next();

            if (o instanceof TextProvider) {
                TextProvider tp = (TextProvider) o;
                message = tp.getText(key, defaultValue, Collections.EMPTY_LIST, stack);
                break;
            }
        }

        if (message == null && resourceKey != null) {
            log.debug(Messages.getString("Localization.missingkey", resourceKey));
            message = UNDEFINED_KEY + resourceKey + UNDEFINED_KEY;
        }

        return message;
    }
}
