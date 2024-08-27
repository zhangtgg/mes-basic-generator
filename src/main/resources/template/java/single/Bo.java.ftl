package cn.tfinfo.microservice.${packageName}.biz.${moduleName};

import org.springframework.beans.BeanUtils;

import cn.tfinfo.microservice.baseconstruct.response.BaseResponse;
import cn.tfinfo.microservice.baseconstruct.utils.CollectionUtils;
import cn.tfinfo.microservice.baseconstruct.utils.StringUtils;
import cn.tfinfo.microservice.mes.basic.biz.dict.DictBo;
import cn.tfinfo.microservice.mes.basic.biz.serial.RuleSerialBo;
import cn.tfinfo.microservice.mes.basic.common.annotation.PageList;
import cn.tfinfo.microservice.mes.basic.common.constants.DictType;
import cn.tfinfo.microservice.mes.basic.common.enums.BaseSysCode;
import cn.tfinfo.microservice.mes.basic.common.enums.SysActiveFlag;
import cn.tfinfo.microservice.mes.basic.common.enums.SysDelFlag;
import cn.tfinfo.microservice.mes.basic.common.local.DbCsLocal;
import cn.tfinfo.microservice.mes.basic.common.exception.CommonException;
import cn.tfinfo.microservice.${packageName}.common.entity.${moduleName}.${ClassName}Entity;
import cn.tfinfo.microservice.${package}.persistance.dao.${moduleName}.${ClassName}Dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

import static cn.tfinfo.microservice.mes.basic.common.constants.CommonConstants.UNUSED_RETURN_VALUE;

/**
* ${"<p> 业务层 </p>"}
*
* @author MES
* @description
* @date ${.now?string("yyyy/MM/dd")}
*/
@Service
public class ${ClassName}Bo {

    @Autowired
    private ${ClassName}Dao ${className}Dao;

    @PageList
    public List<${ClassName}Entity> queryList(${ClassName}Entity entity) {

        List<${ClassName}Entity> ${className}List = ${className}Dao.queryList(entity);
        if (CollectionUtils.isNotEmpty(${className}List)) {
            // 字典转义
            translatePageList(${className}List);
        }
        return ${className}List;
    }

    /**
    * 字典转换
    *
    * @param ${className}List 待转换列表
    */
    @DictEscape
    @SuppressWarnings(UNUSED_RETURN_VALUE)
    public List<${ClassName}Entity> translatePageList(List<${ClassName}Entity> ${className}List) {
        for (${ClassName}Entity entity : ${className}List) {

        }
        return ${className}List;
    }

    @Transactional(rollbackFor = Exception.class, timeout = 5)
    public BaseResponse saveOrUpdate(${ClassName}Entity ${className}Entity) {
        BaseResponse response = new BaseResponse();
        response.setSuccess(true);
        // 处理主表
        handleMainEntity(${className}Entity);

        return response;
    }

    public void handleMainEntity(${ClassName}Entity ${className}Entity) {
        if (StringUtils.isEmpty(${className}Entity.getId())) {
            ${className}Entity.preInsert();
            ${className}Dao.insert(${className}Entity);
        } else {
            ${className}Entity.preUpdate();
            ${className}Dao.update(${className}Entity);
        }
    }

    public void updateStatus(List<String> ids, String flag) {
        ${className}Dao.updateStatus(flag, DbCsLocal.getUserId(), ids);
    }

    @Transactional(rollbackFor = {Exception.class}, timeout = 5)
    public void deleteByIds(List<String> ids) {
        ${className}Dao.deleteByIds(ids, DbCsLocal.getUserId());
    }

    public ${ClassName}Entity queryDetail(String id) {
        ${ClassName}Entity entity = ${className}Dao.queryDetail(id);
        if(entity == null) {
            throw new CommonException("参数异常");
        }
        return entity;
    }

}