package cn.tianjiale.domain.strategy.service.rule.tree.impl;

import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.tianjiale.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


/**
 * 规则树节点->次数锁
 */
@Slf4j
@Component("rule_lock")
public class RuleLockLogicTreeNode implements ILogicTreeNode {
    public static  Long userRaffleCount = 3l;
    @Resource
    private IStrategyRepository repository;
    @Override
    public DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId) {
        //2.查询规则 ruleValue
        String ruleValue = repository.queryStrategyRuleValue(strategyId, awardId,"rule_lock");
        if (StringUtils.isBlank(ruleValue)){
            throw new IllegalArgumentException("valid input format");
        }
        int awardLockCount = Integer.parseInt(ruleValue);

        //3.次数对比
        if (userRaffleCount >= awardLockCount){
            return DefaultTreeFactory.TreeActionEntity.builder()
                    .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.ALLOW)
                    .strategyAwardData(DefaultTreeFactory.StrategyAwardData.builder()
                            .awardId(awardId)
                            .awardRuleValue(ruleValue)
                            .build())
                    .build();
        }
        return DefaultTreeFactory.TreeActionEntity.builder()
                .ruleLogicCheckTypeVO(RuleLogicCheckTypeVO.TAKE_OVER)
                .build();
    }
}
