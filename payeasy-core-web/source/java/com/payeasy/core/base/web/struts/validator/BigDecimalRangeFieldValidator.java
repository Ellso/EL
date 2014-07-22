package com.payeasy.core.base.web.struts.validator;

import java.math.BigDecimal;
import java.util.regex.Pattern;

import com.opensymphony.xwork2.validator.ValidationException;
import com.opensymphony.xwork2.validator.validators.FieldValidatorSupport;

public class BigDecimalRangeFieldValidator extends FieldValidatorSupport {

    private final static Pattern PATTERN = Pattern.compile("^[-]?[0-9]*$");

    private String minInclusive = null;
    private String maxInclusive = null;
    private String minExclusive = null;
    private String maxExclusive = null;
    private boolean allowDecimalPoint = false;

    private BigDecimal maxInclusiveValue = null;
    private BigDecimal minInclusiveValue = null;
    private BigDecimal minExclusiveValue = null;
    private BigDecimal maxExclusiveValue = null;

    public String getMinInclusive() {
        return this.minInclusive;
    }

    public void setMinInclusive(String minInclusive) {
        this.minInclusive = minInclusive;
    }

    public String getMaxInclusive() {
        return this.maxInclusive;
    }

    public void setMaxInclusive(String maxInclusive) {
        this.maxInclusive = maxInclusive;
    }

    public String getMinExclusive() {
        return this.minExclusive;
    }

    public void setMinExclusive(String minExclusive) {
        this.minExclusive = minExclusive;
    }

    public String getMaxExclusive() {
        return this.maxExclusive;
    }

    public void setMaxExclusive(String maxExclusive) {
        this.maxExclusive = maxExclusive;
    }

    public boolean isAllowDecimalPoint() {
        return this.allowDecimalPoint;
    }

    public void setAllowDecimalPoint(boolean allowDecimalPoint) {
        this.allowDecimalPoint = allowDecimalPoint;
    }

    public void validate(Object object) throws ValidationException {
        String fieldName = this.getFieldName();
        Object fieldValue = this.getFieldValue(fieldName, object);

        if (fieldValue == null) {
            return;
        }

        BigDecimal value = null;

        try {
            value = new BigDecimal(fieldValue.toString());
        } catch (NumberFormatException e) {
            return;
        }

        this.parseParameterValues();

        if ((this.maxInclusiveValue != null && value.compareTo(this.maxInclusiveValue) > 0) ||
                (this.minInclusiveValue != null && value.compareTo(this.minInclusiveValue) < 0) ||
                (this.maxExclusiveValue != null && value.compareTo(this.maxExclusiveValue) >= 0) ||
                (this.minExclusiveValue != null && value.compareTo(this.minExclusiveValue) <= 0)) {
            this.addFieldError(fieldName, object);
        } else if (this.allowDecimalPoint == false
                && PATTERN.matcher(value.toString()).matches() == false) {
            this.addFieldError(fieldName, object);
        }
    }

    private void parseParameterValues() {
        this.minInclusiveValue = this.parseBigDecimal(this.minInclusive);
        this.maxInclusiveValue = this.parseBigDecimal(this.maxInclusive);
        this.minExclusiveValue = this.parseBigDecimal(this.minExclusive);
        this.maxExclusiveValue = this.parseBigDecimal(this.maxExclusive);
    }

    private BigDecimal parseBigDecimal(String value) {
        if (value == null) {
            return null;
        }

        try {
            return new BigDecimal(value);
        } catch (NumberFormatException e) {
            if (this.log.isWarnEnabled()) {
                this.log.warn("BigDecimalRangeFieldValidator - [parseBigDecimal]: Unable to parse given BigDecimal parameter " + value);
            }
        }

        return null;
    }

}