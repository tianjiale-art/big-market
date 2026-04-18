package cn.tianjiale.domain.strategy.service.armory;


import cn.tianjiale.domain.strategy.model.entity.StrategyAwardEntity;
import cn.tianjiale.domain.strategy.model.entity.StrategyEntity;
import cn.tianjiale.domain.strategy.model.entity.StrategyRuleEntity;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.types.enums.ResponseCode;
import cn.tianjiale.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
public class StrategyArmoryDispatch implements IStrategyArmory,IStrategyDispatch{
    @Resource
    private IStrategyRepository strategyRepository;
    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        //1.调用仓储层进行查询数据
        List<StrategyAwardEntity> strategyAwardEntities = strategyRepository.queryStrategyAwardList(strategyId);
        assembleStrategy(String.valueOf(strategyId),strategyAwardEntities);

        //2.根据strategyId查询rulemodels
        StrategyEntity strategyEntity = strategyRepository.queryStrategyEntityByStrategyId(strategyId);

        //3.校验是否需要ruleWeight
        String ruleModels = strategyEntity.getRuleModels();
        if (ruleModels == null) return true;
        String ruleWeight = strategyEntity.getRuleWeight();

        //4.ruleWeight + strategyId ->查询StrategyRule
        StrategyRuleEntity strategyRuleEntity = strategyRepository.queryStrategyRule(strategyId,ruleWeight);
        if (null == strategyRuleEntity) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(), ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }

        //进行装配ruleWeight;
        Map<String, List<Integer>> ruleWeightValues = strategyRuleEntity.getRuleWeightValues();
        Set<String> keys = ruleWeightValues.keySet();
        for (String key : keys){
            List<Integer> value = ruleWeightValues.get(key);
            //6.1排除非权重奖项->用户必中权重奖
            List<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(entity -> !value.contains(entity.getAwardId()));

            //进行装配
            assembleStrategy(strategyId+ "_" + key,strategyAwardEntitiesClone);
        }

        return true;
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        //1.查询总的抽奖范围
        Integer rateRange = strategyRepository.getRateRange(strategyId);
        //2.根据总的抽奖范围去生成随机数获取奖品ID
       return strategyRepository.getStrategyAwardAssemble(String.valueOf(strategyId),new SecureRandom().nextInt(rateRange));
    }

    @Override
    public Integer getRandomAwardId(Long strategyId, String ruleWeight) {
        String key = strategyId + "_" + ruleWeight;
        //1.查询总的抽奖范围
        Integer rateRange = strategyRepository.getRateRange(key);
        //2.根据总的抽奖范围去生成随机数获取奖品ID
        return strategyRepository.getStrategyAwardAssemble(key,new SecureRandom().nextInt(rateRange));
    }

    public void assembleStrategy(String key,List<StrategyAwardEntity> strategyAwardEntities){
        //2.获取最小概率值
        BigDecimal minRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        //3.获取总的概率值
        BigDecimal tatalRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //4.获得奖品中奖范围
        BigDecimal rangeRate = tatalRate.divide(minRate,0,RoundingMode.CEILING);

        //5.循环填充list
        List<Integer> awardList = new ArrayList<>(rangeRate.intValue());
        for(StrategyAwardEntity strategyAward : strategyAwardEntities){
            BigDecimal awardRate = strategyAward.getAwardRate();
            int count = awardRate.multiply(rangeRate).setScale(0,RoundingMode.CEILING).intValue();
            for(int i = 0;i < count;i++){
                awardList.add(strategyAward.getAwardId());
            }
        }

        //6.乱序
        Collections.shuffle(awardList);

        //7.填充到map集合中
        Map<Integer,Integer> awardMap = new HashMap<>();
        for(int i = 0;i < awardList.size();i++){
            awardMap.put(i,awardList.get(i));
        }

        //8.存储到redis包括strategyId,rangeAward,map集合
        strategyRepository.storeStrategyAwardSearchRateTable(key,awardMap.size(),awardMap);
    }
}
