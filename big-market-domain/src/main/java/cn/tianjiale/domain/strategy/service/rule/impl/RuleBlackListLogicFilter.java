package cn.tianjiale.domain.strategy.service.rule.impl;


import cn.tianjiale.domain.strategy.model.entity.RaffleActionEntity;
import cn.tianjiale.domain.strategy.model.entity.RuleMatterEntity;
import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.domain.strategy.service.annotation.LogicStrategy;
import cn.tianjiale.domain.strategy.service.rule.ILogicFilter;
import cn.tianjiale.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.tianjiale.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;

/**
 * 根据用户积分对比返回用户可抽奖奖品范围key
 */

@Slf4j
@Service
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_BLACKLIST)
public class RuleBlackListLogicFilter implements ILogicFilter<RaffleActionEntity.RaffleBeforeEntity> {
    @Resource
    private IStrategyRepository repository;
    @Override
    public RaffleActionEntity<RaffleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {
        log.info("规则过滤-黑名单strategyId:{},userId:{},ruleModel:{}",ruleMatterEntity.getStrategyId(),ruleMatterEntity.getUserId(),ruleMatterEntity.getRuleModel());
        String userId = ruleMatterEntity.getUserId();
        Long strategyId = ruleMatterEntity.getStrategyId();

        //1.根据strategy + ruleModel + wardId查询ruleValue
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());

        //2.解析ruleValue:   101:user001,user002,user003
        String[] analyticalValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.valueOf(analyticalValue[0]);
        String[] blackList = analyticalValue[1].split(Constants.SPLIT);

        //3.获得黑名单进行对比
        for (String blackName : blackList) {
            if (blackName.equals(userId)){
                return RaffleActionEntity.<RaffleActionEntity.RaffleBeforeEntity>builder()
                        .data(RaffleActionEntity.RaffleBeforeEntity.builder()
                                .strategyId(strategyId)
                                .awardId(awardId)
                                .build())
                        .ruleModel(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode())
                        .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                        .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                        .build();
            }
        }
        return RaffleActionEntity.<RaffleActionEntity.RaffleBeforeEntity>builder()
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .build();
    }
}
