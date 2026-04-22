package cn.tianjiale.domain.strategy.service.rule;

import cn.tianjiale.domain.strategy.model.entity.RaffleActionEntity;
import cn.tianjiale.domain.strategy.model.entity.RuleMatterEntity;

public interface ILogicFilter<T extends RaffleActionEntity.RaffleEntity> {

    RaffleActionEntity<T> filter(RuleMatterEntity ruleMatterEntity);

}
