package com.payeasy.core.base.web.struts.converter;

import java.math.BigDecimal;
import java.util.Map;

import org.apache.struts2.util.StrutsTypeConverter;

@SuppressWarnings("unchecked")
public class BigDecimalTypeConveter extends StrutsTypeConverter {

    public BigDecimalTypeConveter() {
    }

    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (values[0] == null || values[0].trim().equals("")) {
            return null;
        }

        return new BigDecimal(values[0]);
    }

    public String convertToString(Map context, Object object) {
        return object.toString();
    }
    
}