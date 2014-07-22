package com.payeasy.core.base.web.struts.validator;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

public class CharStringLengthFieldValidator extends FieldValidatorSupport {

    private boolean doTrim = true;
    private int maxLength = -1;
    private int minLength = -1;
    private int charLength = 3;

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getMaxLength() {
        return this.maxLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMinLength() {
        return this.minLength;
    }

    public void setTrim(boolean trim) {
        this.doTrim = trim;
    }

    public boolean getTrim() {
        return this.doTrim;
    }

    public int getCharLength() {
        return this.charLength;
    }

    public void setCharLength(int charLength) {
        this.charLength = charLength;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = this.getFieldName();
        String fieldValue = (String) this.getFieldValue(fieldName, object);

        if (fieldValue == null || fieldValue.length() <= 0) {
            // use a required validator for these
            return;
        }

        if (this.doTrim) {
            fieldValue = fieldValue.trim();
            if (fieldValue.length() <= 0) {
                // use a required validator
                return;
            }
        }

        int length = this.countLength(fieldValue);

        if ((this.minLength > -1) && (length < this.minLength)) {
            this.addFieldError(fieldName, object);
        } else if ((this.maxLength > -1) && (length > this.maxLength)) {
            this.addFieldError(fieldName, object);
        }
    }

    private int countLength(String value) {
        if (value == null) {
            return 0;
        }

        int length = 0;

        for (int i = 0; i < value.length(); i++) {
            int charLength = value.substring(i, i + 1).getBytes().length;
            length += (charLength == 1) ? 1 : this.charLength;
        }

        return length;
    }
}
