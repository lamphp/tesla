<#assign inputRoot= input.path("$")>
[
  <#list inputRoot.photos as elem>
    {
      "id": ${elem.id},
      "owner": ${elem.owner},
      "title": ${elem.title},
      "ispublic": ${elem.ispublic},
      "isfriend": ${elem.isfriend},
      "isfamily": ${elem.isfamily}
    }<#if (elem_has_next)>,</#if>  
   </#list>
]