
import cn.hutool.core.bean.BeanUtil;
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
import org.springframework.web.bind.annotation.*;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import static cn.tfinfo.microservice.baseconstruct.enums.BaseErrorCode.SUCCESS;
import static java.lang.Boolean.TRUE;

@Slf4j
@RequestController
@RequestMapping("/${className}")
public class ${ClassName}Controller {

    @Autowired
    private ${ClassName}Bo ${className}Bo;

    /**
     * 分页查询
     */
    @UserTicket
    @GetMapping("/findList")
    public BaseResponse findList(${ClassName}PageRequest request) {
        ListResponse<${ClassName}Vo> response = new ListResponse<>(Boolean.TRUE, SUCCESS);
        ${ClassName}Entity entity = BeanUtil.copyProperties(request, ${ClassName}Entity.class);
        Page<${ClassName}Entity> result = PageHelper.startPage(request.getPageNo(), request.getPageSize());
        List<${ClassName}Vo> list = ${className}Bo.queryList(entity);
        response.setRows(list);
        PageUtils.getPageInfo(response, request.getPageNo(), request.getPageSize(), (int) result.getTotal());
        return response;
    }

    /**
     * 查询详情
     */
    @UserTicket
    @GetMapping("/queryDetail")
    public BaseResponse queryDetail(IdOrclRequest request) {
        EntityResponse<${ClassName}Vo> response = new EntityResponse<>(TRUE, SUCCESS);
        response.setData(${className}Bo.queryDetail(request.getId()));
        return response;
    }

    /**
     * 保存或更新
     */
    @UserTicket
    @PostMapping("/save")
    public BaseResponse save(${ClassName}Request request) {
        BaseResponse response = new BaseResponse(Boolean.TRUE, BaseErrorCode.SUCCESS);
        ${className}Bo.save(request.get${ClassName}Dto());
        return response;
    }

    /**
     * 批量删除
     */
    @UserTicket
    @PostMapping("/deleteByIds")
    public BaseResponse deleteByIds(IdsRequest request) {
        BaseResponse response = new BaseResponse(Boolean.TRUE, BaseErrorCode.SUCCESS);
        ${className}Bo.deleteByIds(request.getIds());
        return response;
    }

    /**
     * 批量启用/禁用
     */
    @UserTicket
    @PostMapping("/active")
    public BaseResponse activePlan(IdsFlagRequest request) {
        BaseResponse response = new BaseResponse(Boolean.TRUE, BaseErrorCode.SUCCESS);
        ${className}Bo.updateStatus(request.getIds(), request.getFlag());
        return response;
    }
}