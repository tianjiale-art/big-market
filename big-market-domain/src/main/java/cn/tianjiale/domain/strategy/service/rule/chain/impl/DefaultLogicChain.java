package cn.tianjiale.domain.strategy.service.rule.chain.impl;


import cn.tianjiale.domain.strategy.service.armory.IStrategyDispatch;
import cn.tianjiale.domain.strategy.service.rule.chain.AbstractLogicLink;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Slf4j
@Component("default")
public class DefaultLogicChain extends AbstractLogicLink<String,Long,Integer> {
    @Resource
    private IStrategyDispatch dispatch;

    @Override
    protected String ruleModel() {
        return "default";
    }

    @Override
    public Integer apply(String userId, Long strategyId) throws Exception {
        log.info("抽奖责任链——默认抽奖模式userId:{},strategyId:{},ruleModel:{}",userId,strategyId,ruleModel());
        Integer awardId = dispatch.getRandomAwardId(strategyId);
        return awardId;
    }
}
