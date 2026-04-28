package cn.tianjiale.domain.strategy.service.rule.chain.factory;


import cn.tianjiale.domain.strategy.model.entity.StrategyEntity;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.domain.strategy.service.armory.IStrategyDispatch;
import cn.tianjiale.domain.strategy.service.rule.chain.ILogicLink;
import cn.tianjiale.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.*;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 责任链工厂
 */
@Service
public class DefaultChainFactory {
 private final Map<String, ILogicLink<String, Long,DefaultChainFactory.StrategyAwardVO>> logicLinkMap;
 protected IStrategyRepository repository;

 //自动注入
 public DefaultChainFactory(Map<String,ILogicLink<String,Long,DefaultChainFactory.StrategyAwardVO>> logicLinkMap,IStrategyRepository repository){
     this.logicLinkMap = logicLinkMap;
     this.repository = repository;
 }
 public ILogicLink<String,Long,DefaultChainFactory.StrategyAwardVO> openLogicChain(Long strategyId){
     //查询策略的ruleModels
     StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
     String[] ruleModels = strategyEntity.ruleModel();
     //如果没有配置规则返回默认规则
     if (ruleModels == null || ruleModels.length == 0){
         return logicLinkMap.get(logicModel.RULE_DEFAULT.getCode());
     }
     //开始装载规则
     ILogicLink<String, Long, DefaultChainFactory.StrategyAwardVO> logicLink = logicLinkMap.get(ruleModels[0]);
     ILogicLink<String, Long, DefaultChainFactory.StrategyAwardVO> newLogicLink = logicLink;
     for (String ruleModel : ruleModels) {
            newLogicLink.appendNext(logicLinkMap.get(ruleModel));
            newLogicLink = newLogicLink.next();
     }
     //责任链最后装载默认责任链
     newLogicLink.appendNext(logicLinkMap.get(logicModel.RULE_DEFAULT.getCode()));
     return logicLink;
 }

 @AllArgsConstructor
 @Data
 @NoArgsConstructor
 @Builder
 public static class StrategyAwardVO{
     Integer awardId;
     String logicModel;

 }

 @AllArgsConstructor
 @Getter
 public enum logicModel{
     RULE_BLACKLIST("rule_blacklist","黑名单"),
     RULE_WEIGHT("rule_weight","权重抽奖策略"),
     RULE_DEFAULT("rule_default","默认抽奖策略"),
     ;

     private String code;
     private String info;
 }


}
