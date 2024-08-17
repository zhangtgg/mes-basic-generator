package cn.tfinfo.microservice.${package}.biz.${moduleName}.handler;

import cn.tfinfo.microservice.${package}.biz.${moduleName}.handler.NodeChain;
import cn.tfinfo.microservice.${package}.biz.${moduleName}.handler.impl.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ${ClassName}NodeFactory {

<#list handlerList as handler>
    @Autowired
    private ${handler.attrType} ${handler.attrName};
</#list>

    /**
    * 创建handler链条
    */
    public NodeChain createStepChain() {
        return NodeChainBuilder.newBuilder()
                               <#list handlerList as handler>
                               .addNode(${handler.attrName})
                               </#list>
                               .build();
    }
}
