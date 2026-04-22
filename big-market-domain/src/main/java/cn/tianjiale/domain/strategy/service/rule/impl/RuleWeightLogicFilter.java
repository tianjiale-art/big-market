package cn.tianjiale.domain.strategy.service.rule.impl;

import cn.tianjiale.domain.strategy.model.entity.RaffleActionEntity;
import cn.tianjiale.domain.strategy.model.entity.RuleMatterEntity;
import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.domain.strategy.service.annotation.LogicStrategy;
import cn.tianjiale.domain.strategy.service.rule.ILogicFilter;
import cn.tianjiale.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.tianjiale.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 * 黑名单用户过滤
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WEIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RaffleActionEntity.RaffleBeforeEntity> {
    @Resource
    private IStrategyRepository repository;
    public static long userScore = 4500l;
    @Override
    public RaffleActionEntity<RaffleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-权重范围；userId:{},strategyId:{},ruleModel:{}",ruleMatterEntity.getUserId(),ruleMatterEntity.getStrategyId(),ruleMatterEntity.getRuleModel());
        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();
        String ruleValue = repository.queryStrategyRuleValue(strategyId,ruleMatterEntity.getAwardId(),ruleMatterEntity.getRuleModel());

        //1.根据用户的strategyId，ruleModel查询出ruleValue，然后再进行解析
        Map<Long,String> analyticalValueGroups = getAnalyticalValue(ruleValue);
        if (analyticalValueGroups == null || analyticalValueGroups.isEmpty()){
            return RaffleActionEntity.<RaffleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }
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
            return RaffleActionEntity.<RaffleActionEntity.RaffleBeforeEntity>builder()
                    .data(RaffleActionEntity.RaffleBeforeEntity.builder()
                            .ruleWeightValueKey(analyticalValueGroups.get(valueKey))
                            .strategyId(strategyId)
                            .build())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WEIGHT.getCode())
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .build();
        }
        return RaffleActionEntity.<RaffleActionEntity.RaffleBeforeEntity>builder()
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .build();

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
