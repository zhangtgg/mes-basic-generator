package cn.tfinfo.microservice.mes.basic.generator.service;

import cn.tfinfo.microservice.mes.basic.generator.common.page.PageResult;
import cn.tfinfo.microservice.mes.basic.generator.common.query.Query;
import cn.tfinfo.microservice.mes.basic.generator.common.service.BaseService;
import cn.tfinfo.microservice.mes.basic.generator.entity.GenListEntity;

public interface GenListService extends BaseService<GenListEntity> {

    PageResult<GenListEntity> page(Query query);

    GenListEntity getById(Long id);

    boolean save(GenListEntity genListEntity);

    void deleteById(Long id);

}
