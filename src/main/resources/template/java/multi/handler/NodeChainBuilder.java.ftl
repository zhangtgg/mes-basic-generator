package cn.tfinfo.microservice.${package}.biz.${moduleName}.handler;

import cn.tfinfo.microservice.${package}.biz.${moduleName}.handler.NodeChain;

public class NodeChainBuilder{

    private final NodeChain nodeChain = new NodeChain();

    private NodeChainBuilder(){};

    public static NodeChainBuilder newBuilder(){
        return new NodeChainBuilder();
    }

    public NodeChain build(){
        return nodeChain;
    }

    public NodeChainBuilder addNode(HandlerNode node){
        nodeChain.addNode(node);
        return this;
    }

}