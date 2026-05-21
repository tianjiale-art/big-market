package cn.tianjiale.domain.strategy.service;


import cn.tianjiale.domain.strategy.model.entity.StrategyAwardEntity;

import java.util.List;

/**
 * 策略奖品接口
 */
public interface IRaffleAward {
    List<StrategyAwardEntity> queryRaffleStrategyAwardList(Long strategyId);
}
