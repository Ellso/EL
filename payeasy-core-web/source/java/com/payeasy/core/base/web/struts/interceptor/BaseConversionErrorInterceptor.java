package com.payeasy.core.base.web.struts.interceptor;

import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.interceptor.ConversionErrorInterceptor;
import com.opensymphony.xwork2.util.ValueStack;

public class BaseConversionErrorInterceptor extends ConversionErrorInterceptor {

    private static final long serialVersionUID = 8200521083508596520L;

    @Override
    protected Object getOverrideExpr(ActionInvocation invocation, Object value) {
        ValueStack stack = invocation.getStack();

        try {
            stack.push(value);

            return "'" + stack.findValue("top", String.class) + "'";
        } finally {
            stack.pop();
        }
    }

    /**
     * Returns <tt>false</tt> if the value is null, "", or {""} (array of size 1 with a blank element). Returns
     * <tt>true</tt> otherwise.
     *
     * @param propertyName the name of the property to check.
     * @param value        the value to error check.
     * @return <tt>false</tt>  if the value is null, "", or {""}, <tt>true</tt> otherwise.
     */
    @Override
    protected boolean shouldAddError(String propertyName, Object value) {

        if (value == null) {
            return false;
        }

        if ("".equals(value)) {
            return false;
        }

        if (value instanceof String[]) {
            String[] array = (String[]) value;

            if (array.length == 0) {
                return false;
            }

            if (array.length > 1) {
                return true;
            }

            String str = array[0];

            if ("".equals(str)) {
                return false;
            }
        }

        return true;
    }
}
