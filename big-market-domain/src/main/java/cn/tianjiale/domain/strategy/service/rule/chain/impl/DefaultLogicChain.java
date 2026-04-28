package cn.tianjiale.domain.strategy.service.rule.chain.impl;


import cn.tianjiale.domain.strategy.service.armory.IStrategyDispatch;
import cn.tianjiale.domain.strategy.service.rule.chain.AbstractLogicLink;
import cn.tianjiale.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Slf4j
@Component("rule_default")
public class DefaultLogicChain extends AbstractLogicLink<String,Long,DefaultChainFactory.StrategyAwardVO> {
    @Resource
    private IStrategyDispatch dispatch;

    @Override
    protected String ruleModel() {
        return DefaultChainFactory.logicModel.RULE_DEFAULT.getCode();
    }

    @Override
    public DefaultChainFactory.StrategyAwardVO apply(String userId, Long strategyId) throws Exception {
        log.info("抽奖责任链——默认抽奖模式userId:{},strategyId:{},ruleModel:{}",userId,strategyId,ruleModel());
        Integer awardId = dispatch.getRandomAwardId(strategyId);
        return DefaultChainFactory.StrategyAwardVO.builder()
                .awardId(awardId)
                .logicModel(ruleModel())
                .build();
    }
}
