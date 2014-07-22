package com.payeasy.core.base.web.struts.validator;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

public class AsciiFieldValidator extends FieldValidatorSupport {

    private Integer min;
    private Integer max;

    public Integer getMin() {
        return this.min;
    }
    public void setMin(Integer min) {
        this.min = min;
    }
    public Integer getMax() {
        return this.max;
    }
    public void setMax(Integer max) {
        this.max = max;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = this.getFieldName();
        String fieldValue = (String) this.getFieldValue(fieldName, object);

        if (fieldValue == null || fieldValue.length() <= 0) {
            // use a required validator for these
            return;
        }

        for (int i = 0; i < fieldValue.length(); i++) {
            Integer ascii = new Integer(fieldValue.charAt(i));

            if ((this.min != null && ascii < this.min) || (this.max != null && ascii > this.max)) {
                this.addFieldError(fieldName, object);
            }
        }
    }
}