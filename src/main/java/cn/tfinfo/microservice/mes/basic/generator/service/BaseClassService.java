package cn.tfinfo.microservice.mes.basic.generator.service;


import cn.tfinfo.microservice.mes.basic.generator.common.page.PageResult;
import cn.tfinfo.microservice.mes.basic.generator.common.query.Query;
import cn.tfinfo.microservice.mes.basic.generator.common.service.BaseService;
import cn.tfinfo.microservice.mes.basic.generator.entity.BaseClassEntity;

import java.util.List;

/**
 * 基类管理
 */
public interface BaseClassService extends BaseService<BaseClassEntity> {

    PageResult<BaseClassEntity> page(Query query);

    List<BaseClassEntity> getList();
}