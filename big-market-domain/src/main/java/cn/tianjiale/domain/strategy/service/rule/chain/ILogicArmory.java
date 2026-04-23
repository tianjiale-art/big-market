package cn.tianjiale.domain.strategy.service.rule.chain;

public interface ILogicArmory<T,D,R>  {

    ILogicLink<T,D,R> next();
    ILogicLink<T,D,R> appendNext(ILogicLink<T,D,R> next);


}
