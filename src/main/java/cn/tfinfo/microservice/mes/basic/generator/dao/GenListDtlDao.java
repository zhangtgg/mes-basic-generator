package cn.tfinfo.microservice.mes.basic.generator.dao;

import cn.tfinfo.microservice.mes.basic.generator.entity.GenListDtlEntity;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
@InterceptorIgnore(tenantLine = "true")
public interface GenListDtlDao extends BaseMapper<GenListDtlEntity> {

}
