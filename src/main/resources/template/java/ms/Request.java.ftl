package cn.tfinfo.microservice.${packageName}.client.request.${moduleName};

import cn.tfinfo.microservice.baseconstruct.request.BaseRequest;
import cn.tfinfo.microservice.baseconstruct.request.PageRequest;
import cn.tfinfo.microservice.${packageName}.common.entity.${moduleName}.${ClassName}Entity;
import cn.tfinfo.microservice.${packageName}.common.entity.${moduleName}.${ClassName2}Entity;
import lombok.Data;

import java.util.*;

@Data
public class ${ClassName}Request extends PageRequest {
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
    /**
    * 备注
    */
    private String remarks;

    private List<${ClassName2}Entity> ${className2}List;
}