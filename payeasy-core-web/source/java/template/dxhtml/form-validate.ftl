<#if parameters.validate?default(false) == true>
	<#if parameters.onsubmit?exists>
		${tag.addParameter('onsubmit', "${parameters.onsubmit}; return validateForm_${parameters.id}();")}
	<#else>
		${tag.addParameter('onsubmit', "return validateForm_${parameters.id}();")}
	</#if>
</#if>
