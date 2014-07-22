package com.payeasy.core.base.web.displaytag.export;

import org.displaytag.export.CsvView;

public class Big5CsvView extends CsvView {

    @Override
    protected String getRowEnd() {
        return "\r\n";
    }

    @Override
    public String getMimeType() {
        return "text/csv; charset=Big5";
    }
}
