package cn.tfinfo.microservice.${packageName}.persistance.dao.${moduleName};

import cn.tfinfo.microservice.${package}.common.entity.${moduleName}.${ClassName}Entity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ${ClassName}Dao extends BaseMapper<${ClassName}Entity> {

    List<${ClassName}Entity> queryList(${ClassName}Entity entity);

    int deleteByIds(@Param("ids") List<String> ids,@Param("userId") String userId);

    void deleteByParentIds(List<String> ids, String userId);

    int updateStatus(@Param("status") String status, @Param("updateBy") String updateBy, @Param("ids") List<String> ids);

    int insert(${ClassName}Entity entity);

    int update(${ClassName}Entity entity);

    void saveBatch(@Param("list") List<${ClassName}Entity> insertList);

    void updateBatch(@Param("list") List<${ClassName}Entity> updateList);

    ${ClassName}Entity queryDetail(String id);

    List<${ClassName}Entity> queryListByParentId(String id);

}