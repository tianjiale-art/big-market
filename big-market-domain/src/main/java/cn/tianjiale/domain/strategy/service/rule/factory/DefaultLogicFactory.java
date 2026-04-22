package cn.tianjiale.domain.strategy.service.rule.factory;

import cn.tianjiale.domain.strategy.model.entity.RaffleActionEntity;
import cn.tianjiale.domain.strategy.service.annotation.LogicStrategy;
import cn.tianjiale.domain.strategy.service.rule.ILogicFilter;
import com.alibaba.fastjson2.util.AnnotationUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * 规则逻辑工厂
 */
@Service
public class DefaultLogicFactory {
    public Map<String, ILogicFilter<?>> logicFilterMap = new ConcurrentHashMap<>();

    public DefaultLogicFactory(List<ILogicFilter<?>> logicFilter){
        logicFilter.stream()
                .forEach(logic ->
                        {
                            //拿到这个类上的LogicStrategy注解
                            LogicStrategy annotation = AnnotationUtils.findAnnotation(logic.getClass(), LogicStrategy.class);
                            if (annotation != null){
                                //把规则编码->规则实现  放入map
                                logicFilterMap.put(annotation.logicMode().code,logic);
                            }});
    }
    //把map暴露出去，让业务层可以根据业务编码获取对应的实现类
    public <T extends RaffleActionEntity.RaffleEntity> Map<String,ILogicFilter<T>> openLogicFilter(){
        return (Map<String, ILogicFilter<T>>) (Map<?,?>) logicFilterMap;
    }



    @Getter
    @AllArgsConstructor
    public enum LogicModel {

        RULE_WEIGHT("rule_weight","【抽奖前规则】根据抽奖权重返回可抽奖范围KEY"),
        RULE_BLACKLIST("rule_blacklist","【抽奖前规则】黑名单规则过滤，命中黑名单则直接返回"),

        ;

        private final String code;
        private final String info;

    }
}
