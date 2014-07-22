<#assign hasFieldErrors = parameters.name?exists && fieldErrors?exists && fieldErrors[parameters.name]?exists/>
<#if hasFieldErrors>
<#list fieldErrors[parameters.name] as error>
<span errorFor="${parameters.name?html}" class="errorMessage">${error?html}</span>
</#list>
</#if>