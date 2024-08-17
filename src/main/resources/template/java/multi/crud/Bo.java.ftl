
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import cn.tfinfo.microservice.baseconstruct.response.BaseResponse;
import cn.tfinfo.microservice.baseconstruct.utils.CollectionUtils;
import cn.tfinfo.microservice.baseconstruct.utils.StringUtils;
import cn.tfinfo.microservice.${package}.biz.dict.DictBo;
import cn.tfinfo.microservice.${package}.biz.serial.RuleSerialBo;
import cn.tfinfo.microservice.${package}.common.annotation.PageList;
import cn.tfinfo.microservice.${package}.common.constants.DictType;
import cn.tfinfo.microservice.${package}.common.enums.BaseSysCode;
import cn.tfinfo.microservice.${package}.common.enums.SysActiveFlag;
import cn.tfinfo.microservice.${package}.common.enums.SysDelFlag;
import cn.tfinfo.microservice.${package}.common.local.DbCsLocal;
import cn.tfinfo.microservice.${package}.persistance.dao.${moduleName}.${ClassName}Dao;
import cn.tfinfo.microservice.${package}.persistance.dao.${moduleName}.${ClassName}DetailDao;

import java.util.List;

@Service
public class ${ClassName}Bo {

    @Autowired
    private ${ClassName}Dao ${className}Dao;

    @Autowired
    private ${ClassName}DetailDao ${className}DetailDao;

    @PageList
    public List<${ClassName}Vo> queryList(${ClassName}Entity entity) {
        List<${ClassName}Entity> ${ClassName}List = ${className}Dao.queryList(entity);
        return translatePageList(${ClassName}List);
    }

    public List<${ClassName}Vo> translatePageList(List<${ClassName}Entity> ${ClassName}List) {
        List<${ClassName}Vo> ${className}VoList = new ArrayList<>(${ClassName}List.size());
        if (CollectionUtils.isEmpty(${ClassName}List)) {
            return ${className}VoList;
        }
        // 字典转义
//      Map<String,String> ynDict = DictBo.getDictMap(DictType.MD_COMMON_ACTIVE_FLAG);
        for (${ClassName}Entity entity : ${ClassName}List) {
            ${ClassName}Vo ${className}Vo = new ${ClassName}Vo();
            ${className}VoList.add(${className}Vo);
            BeanUtils.copyProperties(entity, ${className}Vo);
        }
    }

    @Transactional(rollbackFor = Exception.class, timeout = 5)
    public BaseResponse save(${ClassName}Dto ${className}Dto) {
        NodeChain addNodeChain = mcStepNodeFactory.createStepChain();
        addNodeChain.dealWith(
                   HandlerContext.builder()
                                 .stepDTO(stepDTO)
                                 .build()
        );
    }

    public void updateStatus(List<String> ids, String flag) {
        ${className}Dao.updateStatus(flag, DbCsLocal.getUserId(), ids);
    }

    @Transactional(rollbackFor = {Exception.class}, timeout = 5)
    public void deleteByIds(List<String> ids) {
        ${className}Dao.deleteByIds(ids, DbCsLocal.getUserId());
        ${className}DetailDao.deleteByParentIds(ids, DbCsLocal.getUserId());
    }

    public ${ClassName}Vo queryDetail(String id) {
        ${ClassName}Entity entity = ${className}Dao.queryDetail(id);
        if (Objects.nonNull(entity)) {
            ${ClassName}Vo ${className}Vo = new ${ClassName}Vo();
            List<${ClassName}DetailEntity> ${className}DetailList = ${className}DetailDao.queryListByPID(id);
            entity.set${ClassName}DetailEntityList(${className}DetailList);
            BeanUtils.copyProperties(entity,${className}Vo);
        }
        return entity;
    }

}