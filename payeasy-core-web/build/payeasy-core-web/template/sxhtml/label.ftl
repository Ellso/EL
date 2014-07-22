<#if parameters.required?default(false) && parameters.requiredposition?default("left") == 'left'>
<span class="required">*</span><#t/>
</#if>
<label<#rt/>
<#if parameters.id?exists>
 id="${parameters.id?html}"<#rt/>
</#if>
<#if parameters.cssClass?exists>
 class="${parameters.cssClass?html}"<#rt/>
<#else>
 class="label"<#rt/>
</#if>
<#if parameters.cssStyle?exists>
 style="${parameters.cssStyle?html}"<#rt/>
</#if>
<#if parameters.title?exists>
 title="${parameters.title?html}"<#rt/>
</#if>
<#if parameters.for?exists>
 for="${parameters.for?html}"<#rt/>
</#if>
<#include "/${parameters.templateDir}/simple/common-attributes.ftl" />
><#rt/>
<#if parameters.nameValue?exists>
<@s.property value="parameters.nameValue" escape="false"/><#t/>
</#if>
</label>

<#if parameters.required?default(false) && parameters.requiredposition?default("left") != 'left'>
<span class="required">*</span><#t/>
</#if>
