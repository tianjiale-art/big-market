package cn.tianjiale.domain.strategy.service.armory;

import cn.tianjiale.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

public interface IStrategyArmory {
   /**
    * 装配抽奖策略配置
    * @param strategyId
    * @return
    */
   boolean assembleLotteryStrategy(Long strategyId);
   /**
    * 获取抽奖策略装配的随机结果
    *
    * @param strategyId 策略ID
    * @return 抽奖结果
    */
   Integer getRandomAwardId(Long strategyId);
}
