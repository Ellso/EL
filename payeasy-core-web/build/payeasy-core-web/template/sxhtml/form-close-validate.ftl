<#if ((parameters.validate?default(false) == true) && (parameters.performValidation?default(false) == true))>
<script type="text/javascript">
    function validateForm_${parameters.id}() {
        form = document.getElementById("${parameters.id}");
        clearErrorMessages(form);

        var errors = false;
        var lastFieldName = "";
        var continueValidation = true;
    <#list parameters.tagNames as tagName>
        <#list tag.getValidators("${tagName}") as validator>
        
        if (lastFieldName != "${validator.fieldName}") {
            lastFieldName = "${validator.fieldName}";
            continueValidation = true;
        }
        
        // field name: ${validator.fieldName}
        // validator name: ${validator.validatorType}
        if (form.elements['${validator.fieldName}']) {
            field = form.elements['${validator.fieldName}'];
            var error = "${validator.getMessage(action)?js_string}";
            <#if validator.validatorType = "required">
            if (field.value == "") {
                addError(field, error);
                errors = true;
                <#if validator.shortCircuit>continueValidation = false;</#if>
            }
            <#elseif validator.validatorType = "requiredstring">
            if (continueValidation && field.value != null && (field.value == "" || field.value.replace(/^\s+|\s+$/g,"").length == 0)) {
                addError(field, error);
                errors = true;
                <#if validator.shortCircuit>continueValidation = false;</#if>
            }
            <#elseif validator.validatorType = "stringlength">
            if (continueValidation && field.value != null) {
                var value = field.value;
                <#if validator.trim>
                //trim field value
                while (value.substring(0,1) == ' ')
                    value = value.substring(1, value.length);
                while (value.substring(value.length-1, value.length) == ' ')
                    value = value.substring(0, value.length-1);
                </#if>
                if ((${validator.minLength?string("0")} > -1 && value.length < ${validator.minLength?string("0")}) ||
                    (${validator.maxLength?string("0")} > -1 && value.length > ${validator.maxLength?string("0")})) {
                    addError(field, error);
                    errors = true;
                    <#if validator.shortCircuit>continueValidation = false;</#if>
                }
            }
            <#elseif validator.validatorType = "charstringlength">
            if (continueValidation && field.value != null) {
                var value = field.value;
                <#if validator.trim>
                //trim field value
                while (value.substring(0,1) == ' ')
                    value = value.substring(1, value.length);
                while (value.substring(value.length-1, value.length) == ' ')
                    value = value.substring(0, value.length-1);
                </#if>
                var length = 0;
                for (var i = 0; i < value.length; i++) {
                    var escapeLength = escape(value.charAt(i)).length;
                    length += (escapeLength < 4) ? 1 : ${validator.charLength?string("0")};
                }
                if ((${validator.minLength?string("0")} > -1 && length < ${validator.minLength?string("0")}) ||
                    (${validator.maxLength?string("0")} > -1 && length > ${validator.maxLength?string("0")})) {
                    addError(field, error);
                    errors = true;
                    <#if validator.shortCircuit>continueValidation = false;</#if>
                }
            }
            <#elseif validator.validatorType = "regex">
            if (continueValidation && field.value != null) {
                var value = field.value;
                <#if validator.trimed>
                //trim field value
                while (value.substring(0,1) == ' ')
                    value = value.substring(1, value.length);
                while (value.substring(value.length-1, value.length) == ' ')
                    value = value.substring(0, value.length-1);
                </#if>
                if (!value.match(/${validator.expression}/<#if validator.caseSensitive == false>i</#if>)) {
                    addError(field, error);
                    errors = true;
                    <#if validator.shortCircuit>continueValidation = false;</#if>
                }
            }
            <#elseif validator.validatorType = "email">
            if (continueValidation && field.value != null && field.value.length > 0 && field.value.match(/\b(^[_A-Za-z0-9-]+(\.[_A-Za-z0-9-]+)*@([A-Za-z0-9-])+(\.[A-Za-z0-9-]+)*((\.[A-Za-z0-9]{2,})|(\.[A-Za-z0-9]{2,}\.[A-Za-z0-9]{2,}))$)\b/gi)==null) {
                addError(field, error);
                errors = true;
                <#if validator.shortCircuit>continueValidation = false;</#if>
            }
            <#elseif validator.validatorType = "url">
            if (continueValidation && field.value != null && field.value.length > 0 && field.value.match(/(^(ftp|http|https):\/\/(\.[_A-Za-z0-9-]+)*(@?([A-Za-z0-9-])+)?(\.[A-Za-z0-9-]+)*((\.[A-Za-z0-9]{2,})|(\.[A-Za-z0-9]{2,}\.[A-Za-z0-9]{2,}))(:[0-9]+)?([/A-Za-z0-9?#_-]*)?$)/gi)==null) { 
                addError(field, error);
                errors = true;
                <#if validator.shortCircuit>continueValidation = false;</#if>
            }
            <#elseif validator.validatorType = "int">
            if (continueValidation && field.value != null && field.value.length > 0) {
                if (!isNaN(parseInt(field.value, 10)) && field.value.match("^[-]?[\\d]+$")) {
                    var value = parseInt(field.value, 10);
                    if (<#if validator.min?exists>value < ${validator.min?string("0")}<#else>false</#if> ||
                        <#if validator.max?exists>value > ${validator.max?string("0")}<#else>false</#if>) {
                        addError(field, error);
                        errors = true;
                        <#if validator.shortCircuit>continueValidation = false;</#if>
                    }
                } else {
                    addError(field, error);
                    errors = true;
                    <#if validator.shortCircuit>continueValidation = false;</#if>
                }
            }
            <#elseif validator.validatorType = "long">
            if (continueValidation && field.value != null && field.value.length > 0) {
                if (!isNaN(parseInt(field.value, 10)) && field.value.match("^[-]?[\\d]+$")) {
                    var value = parseInt(field.value, 10);
                    if (<#if validator.min?exists>value < ${validator.min?string("0")}<#else>false</#if> ||
                        <#if validator.max?exists>value > ${validator.max?string("0")}<#else>false</#if>) {
                        addError(field, error);
                        errors = true;
                        <#if validator.shortCircuit>continueValidation = false;</#if>
                    }
                } else {
                    addError(field, error);
                    errors = true;
                    <#if validator.shortCircuit>continueValidation = false;</#if>
                }
            }
            <#elseif validator.validatorType = "short">
            if (continueValidation && field.value != null && field.value.length > 0) {
                if (!isNaN(parseInt(field.value, 10)) && field.value.match("^[-]?[\\d]+$")) {
                    var value = parseInt(field.value, 10);
                    if (<#if validator.min?exists>value < ${validator.min?string("0")}<#else>false</#if> ||
                        <#if validator.max?exists>value > ${validator.max?string("0")}<#else>false</#if>) {
                        addError(field, error);
                        errors = true;
                        <#if validator.shortCircuit>continueValidation = false;</#if>
                    }
                } else {
                    addError(field, error);
                    errors = true;
                    <#if validator.shortCircuit>continueValidation = false;</#if>
                }
            }
            <#elseif validator.validatorType = "double">
            if (continueValidation && field.value != null && field.value.length > 0) {
                if (!isNaN(parseFloat(field.value, 10)) && field.value.match("^[-]?[\\d]+[\\.]?[\\d]*$")) {
                    var value = parseFloat(field.value, 10);
                    if (<#if validator.minInclusive?exists>value < ${validator.minInclusive?string}<#else>false</#if> ||
                            <#if validator.maxInclusive?exists>value > ${validator.maxInclusive?string}<#else>false</#if> ||
                            <#if validator.minExclusive?exists>value <= ${validator.minExclusive?string}<#else>false</#if> ||
                            <#if validator.maxExclusive?exists>value >= ${validator.maxExclusive?string}<#else>false</#if>) {
                        addError(field, error);
                        errors = true;
                        <#if validator.shortCircuit>continueValidation = false;</#if>
                    }
                } else {
                    addError(field, error);
                    errors = true;
                    <#if validator.shortCircuit>continueValidation = false;</#if>
                }
            }
            <#elseif validator.validatorType = "bigdecimal">
            if (continueValidation && field.value != null && field.value.length > 0) {
                <#if validator.allowDecimalPoint>
                if (!isNaN(parseFloat(field.value, 10)) && field.value.match("^[-]?[\\d]+[\\.]?[\\d]*$")) {
                    var value = parseFloat(field.value, 10);
                    if (<#if validator.minInclusive?exists>value < ${validator.minInclusive?string}<#else>false</#if> ||
                            <#if validator.maxInclusive?exists>value > ${validator.maxInclusive?string}<#else>false</#if> ||
                            <#if validator.minExclusive?exists>value <= ${validator.minExclusive?string}<#else>false</#if> ||
                            <#if validator.maxExclusive?exists>value >= ${validator.maxExclusive?string}<#else>false</#if>) {
                        addError(field, error);
                        errors = true;
                        <#if validator.shortCircuit>continueValidation = false;</#if>
                    }
                } else {
                    addError(field, error);
                    errors = true;
                    <#if validator.shortCircuit>continueValidation = false;</#if>
                }
                <#else>
                if (!isNaN(parseInt(field.value, 10)) && field.value.match("^[-]?[\\d]+$")) {
                    var value = parseInt(field.value, 10);
                    if (<#if validator.minInclusive?exists>value < ${validator.minInclusive?string}<#else>false</#if> ||
                            <#if validator.maxInclusive?exists>value > ${validator.maxInclusive?string}<#else>false</#if> ||
                            <#if validator.minExclusive?exists>value <= ${validator.minExclusive?string}<#else>false</#if> ||
                            <#if validator.maxExclusive?exists>value >= ${validator.maxExclusive?string}<#else>false</#if>) {
                        addError(field, error);
                        errors = true;
                        <#if validator.shortCircuit>continueValidation = false;</#if>
                    }
                } else {
                    addError(field, error);
                    errors = true;
                    <#if validator.shortCircuit>continueValidation = false;</#if>
                }
                </#if>
            }
            <#elseif validator.validatorType = "ascii">
            if (continueValidation && field.value != null && field.value.length > 0) {
                var value = field.value;
                
                for (var i = 0; i < value.length ; i++) {
            	      var ascii = value.charCodeAt(i);
            	      
                    if (<#if validator.min?exists>ascii < ${validator.min?string("0")}<#else>false</#if> ||
                        <#if validator.max?exists>ascii > ${validator.max?string("0")}<#else>false</#if>) {
                        addError(field, error);
                        errors = true;
                        <#if validator.shortCircuit>continueValidation = false;</#if>
                        break;
                    }
                }
            }
            </#if>
        }
        </#list>
    </#list>
    
        if (typeof(addAdditionalValidation) == 'function') {
            if (addAdditionalValidation(form)) {
                errors = true;
            }
        }
        
        return !errors;
    }
</script>
</#if>
