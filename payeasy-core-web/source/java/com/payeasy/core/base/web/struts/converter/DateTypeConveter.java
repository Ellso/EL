package com.payeasy.core.base.web.struts.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.struts2.util.StrutsTypeConverter;

@SuppressWarnings("unchecked")
public class DateTypeConveter extends StrutsTypeConverter {

    private static final Logger logger = Logger.getLogger(DateTypeConveter.class);
    private static final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

    public DateTypeConveter() {
    }

    @Override
    public Object convertFromString(Map context, String[] values, Class toClass) {
        if (values[0] == null || values[0].trim().equals("")) {
            return null;
        }

        try {
            return DateTypeConveter.format.parse(values[0]);
        } catch (ParseException e) {
            logger.warn("convertFromString(toClass = " + toClass.getName()
                    + ") - Catch ParseException", e);
        }

        return null;
    }

    @Override
    public String convertToString(Map context, Object object) {
        return DateTypeConveter.format.format(object);
    }
}
