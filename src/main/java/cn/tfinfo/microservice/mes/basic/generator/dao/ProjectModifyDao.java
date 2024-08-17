package cn.tfinfo.microservice.mes.basic.generator.dao;

import cn.tfinfo.microservice.mes.basic.generator.common.dao.BaseDao;
import cn.tfinfo.microservice.mes.basic.generator.entity.ProjectModifyEntity;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import org.apache.ibatis.annotations.Mapper;

/**
 * 项目名变更
 */
@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface ProjectModifyDao extends BaseDao<ProjectModifyEntity> {

}