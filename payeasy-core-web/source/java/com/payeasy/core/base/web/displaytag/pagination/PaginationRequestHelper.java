package com.payeasy.core.base.web.displaytag.pagination;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.displaytag.tags.TableTagParameters;
import org.displaytag.util.ParamEncoder;

public abstract class PaginationRequestHelper {

    public static int getPageIndex(HttpServletRequest request, String tableId) {
        String parameterName = new ParamEncoder(tableId).encodeParameterName(TableTagParameters.PARAMETER_PAGE);
        String parameterValue = request.getParameter(parameterName);

        return StringUtils.isBlank(parameterValue) ? 1 : Integer.parseInt(parameterValue);
    }

    public static boolean isExport(HttpServletRequest request) {
        String parameterName = TableTagParameters.PARAMETER_EXPORTING;
        String parameterValue = request.getParameter(parameterName);

        return StringUtils.isNotBlank(parameterValue);
    }

}