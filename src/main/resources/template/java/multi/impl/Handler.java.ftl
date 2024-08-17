package cn.tfinfo.microservice.${package}.biz.${moduleName}.handler.impl;

import cn.tfinfo.microservice.baseconstruct.utils.CollectionUtils;
import cn.tfinfo.microservice.${package}.biz.${moduleName}.handler.AbstractHandlerNode;
import cn.tfinfo.microservice.${package}.biz.${moduleName}.handler.HandlerContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
* 属性登记处理器
*/
@Service
public class ${ClassName}Handler extends AbstractHandlerNode {

    @Autowired
    private ${ClassName}Bo ${className}Bo;

    @Override
    protected void doBusiness(HandlerContext handlerContext) {

    }
}
