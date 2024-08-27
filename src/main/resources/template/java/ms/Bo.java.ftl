package cn.tfinfo.microservice.${packageName}.biz.${moduleName};

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.tfinfo.microservice.baseconstruct.response.BaseResponse;
import cn.tfinfo.microservice.baseconstruct.utils.CollectionUtils;
import cn.tfinfo.microservice.baseconstruct.utils.StringUtils;
import cn.tfinfo.microservice.mes.basic.biz.dict.DictBo;
import cn.tfinfo.microservice.mes.basic.biz.serial.McComFormTypeBo;
import cn.tfinfo.microservice.mes.basic.common.annotation.DictEscape;
import cn.tfinfo.microservice.mes.basic.common.annotation.PageList;
import cn.tfinfo.microservice.mes.basic.common.constants.DictType;
import cn.tfinfo.microservice.mes.basic.common.enums.SysActiveFlag;
import cn.tfinfo.microservice.mes.basic.common.enums.SysDelFlag;
import cn.tfinfo.microservice.mes.basic.common.local.DbCsLocal;
import cn.tfinfo.microservice.mes.basic.common.annotation.PageList;
import cn.tfinfo.microservice.mes.basic.common.enums.BaseSysCode;
import cn.tfinfo.microservice.${packageName}.common.entity.${moduleName}.${ClassName}Entity;
import cn.tfinfo.microservice.${packageName}.common.entity.${moduleName}.${ClassName2}Entity;
import cn.tfinfo.microservice.${package}.persistance.dao.${moduleName}.${ClassName}Dao;
import cn.tfinfo.microservice.${package}.persistance.dao.${moduleName}.${ClassName2}Dao;
import cn.tfinfo.microservice.usercenter.shared.facade.UserFacade;

import java.util.*;

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

    @Autowired
    private ${ClassName2}Dao ${className2}Dao;

    @Autowired
    private McComFormTypeBo mcComFormTypeBo;

    @Autowired
    private UserFacade userFacade;

    @PageList
    public List<${ClassName}Entity> queryList(${ClassName}Entity entity) {

        List<${ClassName}Entity> ${ClassName}List = ${className}Dao.queryList(entity);
        if (CollectionUtils.isNotEmpty(${ClassName}List)) {
            // 字典转义
            translatePageList(${ClassName}List);
        }
        return ${ClassName}List;
    }

    /**
    * 字典转换
    */
    public List<${ClassName}Entity> translatePageList(List<${ClassName}Entity> ${className}List) {
        Map<String, String> dict = DictBo.getDictMap("");
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

        // 处理子表
        handleSubEntities(${className}Entity);

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


    private String generateSerialNo() {
        BaseReturn<String> result = mcComFormTypeBo.generateCode("");
        if (result.hasError()) {
            throw new CommonException(result.getMsg());
        }
        return result.getT();
    }

    public void handleSubEntities(${ClassName}Entity ${className}Entity) {
        List<${ClassName2}Entity> ${className2}List = ${className}Entity.get${ClassName2}List();
        if (CollectionUtils.isEmpty(${className2}List)) {
            return; // 如果子表列表为空，直接返回
        }

        List<${ClassName2}Entity> insertList = new ArrayList<>();
        List<${ClassName2}Entity> updateList = new ArrayList<>();
        List<String> deleteList = new ArrayList<>();
        ${className2}List.forEach(entity -> {
            if (SysDelFlag.DELETE.getFlag().equals(entity.getDelFlag())) {
                deleteList.add(entity.getId());
            } else {
                if (StringUtils.isEmpty(entity.getId())) {
                    entity.setParentId(${className}Entity.getId());
                    entity.preInsert();
                    insertList.add(entity);
                } else {
                    entity.preUpdate();
                    updateList.add(entity);
                }
            }
        });

        if (CollectionUtils.isNotEmpty(deleteList)) {
            ${className2}Dao.deleteByIds(deleteList, DbCsLocal.getUserId());
        }
        if (CollectionUtils.isNotEmpty(insertList)) {
            ${className2}Dao.saveBatch(insertList);
        }
        if (CollectionUtils.isNotEmpty(updateList)) {
            ${className2}Dao.updateBatch(updateList);
        }
    }

    public void updateStatus(List<String> ids, String flag) {
        ${className}Dao.updateStatus(flag, DbCsLocal.getUserId(), ids);
    }

    @Transactional(rollbackFor = {Exception.class}, timeout = 5)
    public void deleteByIds(List<String> ids) {
        ${className}Dao.deleteByIds(ids, DbCsLocal.getUserId());
        ${className2}Dao.deleteByParentIds(ids, DbCsLocal.getUserId());
    }

    public ${ClassName}Entity queryDetail(String id) {
        ${ClassName}Entity entity = ${className}Dao.queryDetail(id);
        if(entity == null) {
            throw new CommonException("参数异常");
        }
        List<${ClassName2}Entity> ${className2}List = ${className2}Dao.queryListByParentId(id);
        entity.set${ClassName2}List(translateDetailList(${className2}List));
        return entity;
    }

    public List<${ClassName2}Entity> translateDetailList(List<${ClassName2}Entity> ${className2}List) {
        Set<String> idList = ${className2}List
                .stream()
                .map(${ClassName2}Entity::getCreateBy)
                .collect(Collectors.toSet());
        UserMapDTO userMapDTO = userFacade.getNameMapByIds(idList);
        Map<String, String> userNameMap = userMapDTO.getUserNameMap();
        for (${ClassName2}Entity entity : ${className2}List) {

        }
        return ${className2}List;
    }

}