package cn.tianjiale.domain.strategy.service;

import cn.tianjiale.domain.strategy.model.valobj.StrategyAwardStockKeyVO;

public interface IRaffleStock {
    /**
     * 获取奖品消耗队列
     * @return
     * @throws InterruptedException
     */
    StrategyAwardStockKeyVO takeQueueValue() throws InterruptedException;

    /**
     * 更新奖品库存消耗记录
     * @param strategyId
     * @param awardId
     */
    void updateStrategyAwardStock(Long strategyId,Integer awardId);
}
