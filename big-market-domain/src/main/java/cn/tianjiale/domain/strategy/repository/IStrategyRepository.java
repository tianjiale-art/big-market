package cn.tianjiale.domain.strategy.repository;


import cn.tianjiale.domain.strategy.model.entity.StrategyAwardEntity;
import cn.tianjiale.domain.strategy.model.entity.StrategyEntity;
import cn.tianjiale.domain.strategy.model.entity.StrategyRuleEntity;
import cn.tianjiale.domain.strategy.model.valobj.StrategyAwardRuleModelVO;

import java.util.List;
import java.util.Map;

/**
 * 策略服务仓储接口
 */
public interface IStrategyRepository {
    List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId);

    void storeStrategyAwardSearchRateTable(String key, int rateRange, Map<Integer, Integer> awardMap);
    Integer getStrategyAwardAssemble(String key, Integer rateKey);

    int getRateRange(String key);
    int getRateRange(Long strategyId);

    StrategyEntity queryStrategyEntityByStrategyId(Long strategyId);

    StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleWeight);

    String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel);
    String queryStrategyRuleValue(Long strategyId, String ruleModel);

    StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId);
}
