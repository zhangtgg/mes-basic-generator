package cn.tfinfo.microservice.mes.basic.generator.constant;

import lombok.Data;

import java.util.List;

@Data
public class TemplateConstant {

    /**
     * 仅主表生成的文件
     */
    public final static List<String> fileNameList2 = List.of(
            "AbstractHandlerNode.java",
            "HandlerContext.java",
            "HandlerNode.java",
            "NodeChain.java",
            "NodeChainBuilder.java",
            "NodeFactory.java",
            "AbstractHandlerNode.java",
            "HandlerContext.java",
            "HandlerNode.java",
            "NodeChain.java",
            "NodeChainBuilder.java",
            "NodeFactory.java",
            "${ClassName}PageRequest.java",
            "${ClassName}Request.java",
            "${ClassName}Vo.java",
            "${ClassName}Controller.java",
            "BasicHandler.java"
    );

    /**
     * 仅主表生成的文件
     */
    public final static List<String> fileNameList3 = List.of(
            "${ClassName}Bo.java",
            "CodeFreeTemplate.json"
    );

    /**
     * 主表不生成的文件
     */
    public final static List<String> fileNameList10 = List.of(
        "Handler.java"
    );

}
