package cn.tianjiale.domain.strategy.service.rule.impl;

import cn.tianjiale.domain.strategy.model.entity.RaffleActionEntity;
import cn.tianjiale.domain.strategy.model.entity.RuleMatterEntity;
import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.domain.strategy.service.annotation.LogicStrategy;
import cn.tianjiale.domain.strategy.service.rule.ILogicFilter;
import cn.tianjiale.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.tianjiale.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * 奖品抽奖次数解锁
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_LOCK)
public class RuleLockLogicFilter implements ILogicFilter<RaffleActionEntity.RaffleCenterEntity> {
    public static  Long userRaffleCount = 3l;
    @Resource
    private IStrategyRepository repository;

    @Override
    public RaffleActionEntity filter(RuleMatterEntity ruleMatterEntity) {
        log.info("抽奖中规则——奖品抽奖次数解锁strategyId:{},userId:{},awardId:{},ruleModel:{}",ruleMatterEntity.getStrategyId(),ruleMatterEntity.getUserId(),ruleMatterEntity.getAwardId(),ruleMatterEntity.getRuleModel());
        //1.参数校验
        Long strategyId = ruleMatterEntity.getStrategyId();
        Integer awardId = ruleMatterEntity.getAwardId();
        String ruleModel = ruleMatterEntity.getRuleModel();
        if(strategyId == null || awardId == null || StringUtils.isBlank(ruleModel)){
            throw new IllegalArgumentException("valid input format");
        }

        //2.查询规则 ruleValue
        String ruleValue = repository.queryStrategyRuleValue(strategyId, awardId, ruleModel);
        if (StringUtils.isBlank(ruleValue)){
            throw new IllegalArgumentException("valid input format");
        }
        int awardLockCount = Integer.parseInt(ruleValue);

        //3.次数对比
        if (userRaffleCount >= awardLockCount){
            return RaffleActionEntity.<RaffleActionEntity.RaffleCenterEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_LOCK.getCode())
                    .build();
        }
        return RaffleActionEntity.<RaffleActionEntity.RaffleCenterEntity>builder()
                .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                .ruleModel(DefaultLogicFactory.LogicModel.RULE_LOCK.getCode())
                .build();
    }
}
