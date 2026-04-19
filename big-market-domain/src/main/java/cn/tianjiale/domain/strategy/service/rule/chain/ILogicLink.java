package cn.tianjiale.domain.strategy.service.rule.chain;

public interface ILogicLink<T,D,R> extends ILogicArmory<T,D,R>{

    R apply(T requestParameter,D dynamicContext) throws Exception;
}
