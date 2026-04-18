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

}
