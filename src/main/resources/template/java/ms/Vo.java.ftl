
import lombok.Data;
import com.baomidou.mybatisplus.annotation.*;
import cn.tfinfo.microservice.common.logs.service.common.entity.DataEntity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ${ClassName}Vo {
<#list fieldList as field>
        <#if field.fieldComment!?length gt 0>
            /**
            * ${field.fieldComment}
            */
        </#if>
        private ${field.attrType} ${field.attrName};
</#list>
}