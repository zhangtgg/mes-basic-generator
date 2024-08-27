package cn.tfinfo.microservice.${packageName}.client.request.${moduleName};

import cn.tfinfo.microservice.baseconstruct.request.PageRequest;
import lombok.Data;

import java.util.*;

@Data
public class ${ClassName}PageRequest extends PageRequest {
<#list fieldList as field>
<#if !field.baseField>
    <#if field.fieldComment!?length gt 0>
    /**
     * ${field.fieldComment}
     */
    </#if>
    private ${field.attrType} ${field.attrName};
    </#if>

</#list>
}
