package cn.tianjiale.domain.strategy.service.rule.chain.factory;


import cn.tianjiale.domain.strategy.model.entity.StrategyEntity;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.domain.strategy.service.armory.IStrategyDispatch;
import cn.tianjiale.domain.strategy.service.rule.chain.ILogicLink;
import cn.tianjiale.domain.strategy.service.rule.factory.DefaultLogicFactory;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 责任链工厂
 */
@Service
public class DefaultChainFactory {
 private final Map<String, ILogicLink<String, Long,Integer>> logicLinkMap;
 protected IStrategyRepository repository;

 //自动注入
 public DefaultChainFactory(Map<String,ILogicLink<String,Long,Integer>> logicLinkMap,IStrategyRepository repository){
     this.logicLinkMap = logicLinkMap;
     this.repository = repository;
 }
 public ILogicLink<String,Long,Integer> openLogicChain(Long strategyId){
     //查询策略的ruleModels
     StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
     String[] ruleModels = strategyEntity.ruleModel();
     //如果没有配置规则返回默认规则
     if (ruleModels == null || ruleModels.length == 0){
         return logicLinkMap.get("default");
     }
     //开始装载规则
     ILogicLink<String, Long, Integer> logicLink = logicLinkMap.get(ruleModels[0]);
     ILogicLink<String, Long, Integer> newLogicLink = logicLink;
     for (String ruleModel : ruleModels) {
            newLogicLink.appendNext(logicLinkMap.get(ruleModel));
            newLogicLink = newLogicLink.next();
     }
     //责任链最后装载默认责任链
     newLogicLink.appendNext(logicLinkMap.get("default"));
     return logicLink;
 }


}
