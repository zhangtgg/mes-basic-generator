package cn.tfinfo.microservice.${package}.biz.${moduleName}.handler;

/**
* 节点处理器抽象类
*/
public abstract class AbstractHandlerNode implements HandlerNode{

    @Override
    public void dealWith(HandlerContext handlerContext){

        if(!needDealWith(handlerContext)){
            return;
        }

        //卡控处理
        validator(handlerContext);

        doBusiness(handlerContext);

    }

    /**
    * 判断当前操作是否需要处理
    */
    protected boolean needDealWith(HandlerContext handlerContext) {
        return true;
    }

    protected abstract void doBusiness(HandlerContext handlerContext);

    /**
    * 默认空实现 如果子类有自己的卡控规则 请覆写这个方法
    */
    protected void validator(HandlerContext handlerContext){};
}
