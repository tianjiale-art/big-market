package cn.tianjiale.domain.strategy.service.rule.chain.impl;

import cn.tianjiale.domain.strategy.model.entity.RaffleActionEntity;
import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.domain.strategy.service.rule.chain.AbstractLogicLink;
import cn.tianjiale.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.tianjiale.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.tianjiale.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;


@Slf4j
@Component("rule_blacklist")
public class BlackListLogicChain extends AbstractLogicLink<String ,Long,DefaultChainFactory.StrategyAwardVO> {
    @Resource
    private IStrategyRepository repository;
    @Override
    public DefaultChainFactory.StrategyAwardVO apply(String userId, Long strategyId) throws Exception {
        log.info("抽奖责任链——黑名单开始userId:{},strategyId:{},ruleModel:{}",userId,strategyId,ruleModel());
        //查询规则值配置
        //1.根据strategy + ruleModel + wardId查询ruleValue
        String ruleValue = repository.queryStrategyRuleValue(strategyId, ruleModel());

        //2.解析ruleValue:   101:user001,user002,user003
        String[] analyticalValue = ruleValue.split(Constants.COLON);
        Integer awardId = Integer.valueOf(analyticalValue[0]);
        String[] blackList = analyticalValue[1].split(Constants.SPLIT);
        //黑名单判断
        for (String blackName : blackList) {
            if (blackName.equals(userId)){
        log.info("抽奖责任链——黑名单接管userId:{},strategyId:{},ruleModel:{},awardId:{}",userId,strategyId,ruleModel(),awardId);
                return DefaultChainFactory.StrategyAwardVO.builder()
                        .logicModel(ruleModel())
                        .awardId(awardId)
                        .build();
            }
        }
        log.info("抽奖责任链——黑名单放行userId:{},strategyId:{},ruleModel:{}",userId,strategyId,ruleModel());
        return next().apply(userId,strategyId);
    }

    @Override
    protected String ruleModel() {
        return DefaultChainFactory.logicModel.RULE_BLACKLIST.getCode();
    }
}
