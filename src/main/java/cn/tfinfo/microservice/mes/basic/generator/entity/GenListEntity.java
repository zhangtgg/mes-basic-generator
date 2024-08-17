package cn.tfinfo.microservice.mes.basic.generator.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
@TableName("gen_list")
public class GenListEntity {

	@TableId
	private Long id;

	@TableField("project_name")
	private String projectName;

	@TableField("module_name")
	private String moduleName;

	@TableField("function_description")
	private String functionDescription;

	@TableField("created_at")
	private Date createdAt;

	@TableField("updated_at")
	private Date updatedAt;

	@TableField(exist = false)
	List<GenListDtlEntity> genListDtlEntityList;
}