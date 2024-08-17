package cn.tfinfo.microservice.${package}.biz.${moduleName}.handler;

import cn.tfinfo.microservice.${package}.common.entity.${moduleName}.dto.${ClassName};
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class HandlerContext {

    public static String ADD = "add";

    public static String UPDATE = "update";

    private ${ClassName}Dto ${className}Dto;

}