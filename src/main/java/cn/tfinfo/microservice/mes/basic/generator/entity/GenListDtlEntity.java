package cn.tfinfo.microservice.mes.basic.generator.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("gen_list_dtl")
public class GenListDtlEntity {

	@TableId("id")
	private Long id;

	@TableField("parent_id")
	private Long parentId;

	@TableField("table_id")
	private Long tableId;

	@TableField("table_name")
	private String tableName;

	@TableField("main_table_flag")
	private Integer mainTableFlag;

	@TableField(exist = false)
	private String deleteFlag;
}