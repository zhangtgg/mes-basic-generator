
import lombok.Data;
import com.baomidou.mybatisplus.annotation.*;
import cn.tfinfo.microservice.common.logs.service.common.entity.DataEntity;

@Data
@TableName("${tableName}")
public class ${ClassName}Entity<#if baseClass??> extends ${baseClass.code}</#if> {
<#list fieldList as field>
<#if !field.baseField && field.fieldName != "ID">
	<#if field.fieldComment!?length gt 0>
	/**
	* ${field.fieldComment}
	*/
	</#if>
	@TableField("${field.fieldName}")
	private ${field.attrType} ${field.attrName};
</#if>
</#list>
}