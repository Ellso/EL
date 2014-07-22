<input type="hidden"<#rt/>
 name="${parameters.name?default("")?html}"<#rt/>
<#if parameters.nameValue?exists>
 <@s.propertyHDIV value="parameters.nameValue" write="false" escape="false" /> 
 <#assign hdivValue = dataComposer.compose(parameters.name?default("")?html, "${hdivPropertyValue}", false)/>
 value="${hdivValue}"<#rt/>
</#if>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
/>
