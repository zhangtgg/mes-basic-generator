package cn.tfinfo.microservice.${packageName}.client.webcontroller.${moduleName};

import cn.hutool.core.bean.BeanUtil;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import cn.tfinfo.microservice.baseconstruct.enums.BaseErrorCode;
import cn.tfinfo.microservice.baseconstruct.param.annotation.RequestController;
import cn.tfinfo.microservice.baseconstruct.param.annotation.UserTicket;
import cn.tfinfo.microservice.baseconstruct.request.IdOrclRequest;
import cn.tfinfo.microservice.baseconstruct.request.IdsRequest;
import cn.tfinfo.microservice.mes.basic.client.request.base.IdsFlagRequest;
import cn.tfinfo.microservice.baseconstruct.response.BaseResponse;
import cn.tfinfo.microservice.baseconstruct.response.EntityResponse;
import cn.tfinfo.microservice.baseconstruct.response.ListResponse;
import cn.tfinfo.microservice.baseconstruct.utils.PageUtils;
import cn.tfinfo.microservice.${package}.biz.${moduleName}.${ClassName}Bo;
import cn.tfinfo.microservice.${package}.client.request.${moduleName}.${ClassName}Request;
import cn.tfinfo.microservice.${package}.client.request.${moduleName}.${ClassName}PageRequest;
import cn.tfinfo.microservice.${package}.common.entity.${moduleName}.${ClassName}Entity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


import java.util.List;

import static cn.tfinfo.microservice.baseconstruct.enums.BaseErrorCode.SUCCESS;
import static java.lang.Boolean.TRUE;

/**
* ${"<p> 控制层 </p>"}
*
* @author MES
* @description
* @date ${.now?string("yyyy/MM/dd")}
*/
@Slf4j
@RequestController
@RequestMapping("/${className}")
public class ${ClassName}Controller {

    @Autowired
    private ${ClassName}Bo ${className}Bo;

    @UserTicket
    @GetMapping("/findList")
    public BaseResponse findList(${ClassName}PageRequest request){
        ListResponse<${ClassName}Entity> response = new ListResponse<>(Boolean.TRUE, SUCCESS);
        ${ClassName}Entity entity = BeanUtil.copyProperties(request, ${ClassName}Entity.class);
        Page<${ClassName}Entity> result = PageHelper.startPage(request.getPageNo(), request.getPageSize());
        List<${ClassName}Entity> list = ${className}Bo.queryList(entity);
        response.setRows(list);
        PageUtils.getPageInfo(response, request.getPageNo(), request.getPageSize(), (int) result.getTotal());
        return response;
    }

    @UserTicket
    @GetMapping("/queryDetail")
    public BaseResponse queryDetail(IdOrclRequest request) {
        EntityResponse<${ClassName}Entity> response = new EntityResponse<>(TRUE, SUCCESS);
        response.setData(${className}Bo.queryDetail(request.getId()));
        return response;
    }

    @UserTicket
    @PostMapping("/save")
    public BaseResponse save(${ClassName}Request request) {
        BaseResponse response = new BaseResponse(Boolean.TRUE, BaseErrorCode.SUCCESS);
        ${ClassName}Entity entity = new ${ClassName}Entity();
        BeanUtils.copyProperties(request, entity);
        ${className}Bo.saveOrUpdate(entity);
        return response;
    }

    @UserTicket
    @PostMapping("/deleteByIds")
    public BaseResponse deleteByIds(IdsRequest request) {
        BaseResponse response = new BaseResponse(Boolean.TRUE, BaseErrorCode.SUCCESS);
        ${className}Bo.deleteByIds(request.getIds());
        return response;
    }

    @UserTicket
    @PostMapping("/updateStatus")
    public BaseResponse updateStatus(IdsFlagRequest request) {
        BaseResponse response = new BaseResponse(Boolean.TRUE, BaseErrorCode.SUCCESS);
        ${className}Bo.updateStatus(request.getIds(), request.getFlag());
        return response;
    }
}