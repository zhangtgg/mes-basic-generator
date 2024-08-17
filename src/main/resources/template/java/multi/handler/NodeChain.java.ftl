package cn.tfinfo.microservice.${package}.biz.${moduleName}.handler;

import cn.tfinfo.microservice.${package}.biz.${moduleName}.handler.HandlerNode;
import java.util.ArrayList;
import java.util.List;

public class NodeChain implements HandlerNode{

    private final List<HandlerNode> nodeList = new ArrayList<>();

    public void addNode(HandlerNode handlerNode){
        nodeList.add(handlerNode);
    }

    public void removeNode(HandlerNode handlerNode){
        nodeList.removeIf(n -> n.equals(handlerNode));
    }

    @Override
    public void dealWith(HandlerContext handlerContext)  {
        for (HandlerNode node : nodeList) {
            node.dealWith(handlerContext);
        }
    }
}