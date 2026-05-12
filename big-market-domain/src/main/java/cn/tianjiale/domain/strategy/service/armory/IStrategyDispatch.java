package cn.tianjiale.domain.strategy.service.armory;

import cn.tianjiale.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

public interface IStrategyDispatch {
    /**
     * 获取抽奖策略装配的随机结果
     *
     * @param strategyId 策略ID
     * @return 抽奖结果
     */
    Integer getRandomAwardId(Long strategyId);
    Integer getRandomAwardId(Long strategyId,String ruleWeight);

    Boolean subtractionAwardStock(Long strategyId,Integer awardId);


}
