package cn.tianjiale.domain.strategy.service;

import cn.tianjiale.domain.strategy.model.entity.RaffleAwardEntity;
import cn.tianjiale.domain.strategy.model.entity.RaffleFactorEntity;

public interface IRaffleStrategy {

    /**
     * 抽奖因子入参 计算抽奖 返回奖品信息
     * @param raffleFactorEntity
     * @return
     */
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity);
}
