package cn.tfinfo.microservice.mes.basic.generator.service.impl;

import cn.tfinfo.microservice.mes.basic.generator.common.service.impl.BaseServiceImpl;
import cn.tfinfo.microservice.mes.basic.generator.dao.GenListDtlDao;
import cn.tfinfo.microservice.mes.basic.generator.entity.GenListDtlEntity;
import cn.tfinfo.microservice.mes.basic.generator.service.GenListDtlService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class GenListDtlServiceImpl extends BaseServiceImpl<GenListDtlDao, GenListDtlEntity> implements GenListDtlService {

}
