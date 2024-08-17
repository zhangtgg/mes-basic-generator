package cn.tfinfo.microservice.${package}.biz.${moduleName}.handler;

import cn.tfinfo.microservice.${package}.biz.${moduleName}.handler.HandlerContext;

public interface HandlerNode {

    /**
    * 节点的具体处理
    */
    void dealWith(HandlerContext handlerContext);

}