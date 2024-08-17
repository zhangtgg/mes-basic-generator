
import cn.tfinfo.microservice.baseconstruct.request.BaseRequest;
import lombok.Data;

import java.util.List;

@Data
public class ${ClassName}Request extends BaseRequest {
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
}