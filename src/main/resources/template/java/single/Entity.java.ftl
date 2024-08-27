package cn.tfinfo.microservice.${packageName}.common.entity.${moduleName};

import lombok.Data;
import com.baomidou.mybatisplus.annotation.*;
import cn.tfinfo.microservice.common.logs.service.common.entity.DataEntity;

import java.util.*;

@Data
@TableName("${tableName}")
public class ${ClassName}Entity extends DataEntity {
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