package cn.tianjiale.domain.strategy.service.rule.chain.impl;

import cn.tianjiale.domain.strategy.model.entity.RaffleActionEntity;
import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.domain.strategy.service.armory.IStrategyDispatch;
import cn.tianjiale.domain.strategy.service.rule.chain.AbstractLogicLink;
import cn.tianjiale.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.tianjiale.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.tianjiale.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component("rule_weight")
public class RuleWeightLogicChain extends AbstractLogicLink<String,Long,DefaultChainFactory.StrategyAwardVO> {
    @Resource
    private IStrategyRepository repository;
    @Resource
    private IStrategyDispatch dispatch;
    public Long userScore = 0L;
    @Override
    protected String ruleModel() {
        return DefaultChainFactory.logicModel.RULE_WEIGHT.getCode();
    }

    @Override
    public DefaultChainFactory.StrategyAwardVO apply(String userId, Long strategyId) throws Exception {
        log.info("抽奖责任链——权重开始userId:{},strategyId:{},ruleModel:{}",userId,strategyId,ruleModel());
        String ruleValue = repository.queryStrategyRuleValue(strategyId,ruleModel());

        //1.根据用户的strategyId，ruleModel查询出ruleValue，然后再进行解析
        Map<Long,String> analyticalValueGroups = getAnalyticalValue(ruleValue);
        if (analyticalValueGroups != null && !analyticalValueGroups.isEmpty()){
            //2.解析出来的值4000：4000：102，103  5000:5000,102,103拿到key进行排序比较
            ArrayList<Long> keys = new ArrayList<>(analyticalValueGroups.keySet());
            //3.进行排序
            Collections.sort(keys);
            //4.查询用户的值
            //5.根据用户的积分值进行比较找到第一个符合的值
            Long valueKey = keys.stream()
                    .filter(key -> key <= userScore)
                    .max(Long::compareTo)
                    .orElse(null);

            //6.如果valueKey不等于null就可以走权重抽奖了
            if (valueKey != null){
                Integer awardId = dispatch.getRandomAwardId(strategyId, analyticalValueGroups.get(valueKey));

                log.info("抽奖责任链——权重接管userId:{},strategyId:{},ruleModel:{},awardId:{}",userId,strategyId,ruleModel(),awardId);
                return DefaultChainFactory.StrategyAwardVO.builder()
                        .awardId(awardId)
                        .logicModel(ruleModel())
                        .build();
            }
        }
        log.info("抽奖责任链——权重放行userId:{},strategyId:{},ruleModel:{}",userId,strategyId,ruleModel());
      return   next().apply(userId,strategyId);
    }

    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        String[] ruleValueKeys = ruleValue.split(Constants.SPACE);
        Map<Long,String> analyticalValueGroups = new HashMap<>();
        for (String ruleValueKey : ruleValueKeys) {
            if (ruleValueKey == null || ruleValueKey.isEmpty()){
                return analyticalValueGroups;
            }
            String[] mapKey = ruleValueKey.split(Constants.COLON);
            if (mapKey.length != 2){
                throw new IllegalArgumentException("rule_value valid input format ruleValue");
            }
            analyticalValueGroups.put(Long.valueOf(mapKey[0]),ruleValueKey);
        }
        return analyticalValueGroups;
    }
}
