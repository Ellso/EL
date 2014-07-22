package com.payeasy.core.base.web.displaytag.export;

import org.displaytag.export.ExcelView;

public class Big5ExcelView extends ExcelView {

    @Override
    protected String getRowEnd() {
        return "\r\n";
    }

    @Override
    public String getMimeType() {
        return "application/vnd.ms-excel; charset=Big5"; //$NON-NLS-1$
    }
}
