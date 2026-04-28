package cn.tianjiale.domain.strategy.service.raffle;

import cn.tianjiale.domain.strategy.model.entity.*;
import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.model.valobj.RuleTreeVO;
import cn.tianjiale.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.domain.strategy.service.IRaffleStrategy;
import cn.tianjiale.domain.strategy.service.armory.IStrategyDispatch;
import cn.tianjiale.domain.strategy.service.rule.chain.ILogicLink;
import cn.tianjiale.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.tianjiale.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.tianjiale.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.tianjiale.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import cn.tianjiale.types.enums.ResponseCode;
import cn.tianjiale.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    protected IStrategyRepository repository;

    protected IStrategyDispatch dispatch;
    protected final DefaultChainFactory defaultChainFactory;

    protected  final DefaultTreeFactory defaultTreeFactory;
    public AbstractRaffleStrategy(IStrategyRepository repository,IStrategyDispatch dispatch,DefaultChainFactory defaultChainFactory,DefaultTreeFactory defaultTreeFactory){
        this.repository = repository;
        this.dispatch = dispatch;
        this.defaultChainFactory = defaultChainFactory;
        this.defaultTreeFactory = defaultTreeFactory;
    }
    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) throws Exception {
        log.info("执行------------抽奖strategyId:{},userId:{}",raffleFactorEntity.getStrategyId(),raffleFactorEntity.getUserId());
        //1.参数校验
        String userId = raffleFactorEntity.getUserId();
        Long strategyId = raffleFactorEntity.getStrategyId();

        if (strategyId == null || StringUtils.isBlank(userId)){
            throw new AppException(ResponseCode.ILLEGAL_PARAMETER.getCode(),ResponseCode.ILLEGAL_PARAMETER.getInfo());
        }
        //抽奖责任链计算【初步拿到奖品ID】
        DefaultChainFactory.StrategyAwardVO chain = raffleLogicChain(userId,strategyId);
        log.info("抽奖策略计算-责任链{}{}{}{}",userId,strategyId,chain.getAwardId(),chain.getLogicModel());
        if (!chain.getLogicModel().equals(DefaultChainFactory.logicModel.RULE_DEFAULT.getCode())){
            return RaffleAwardEntity.builder()
                    .awardId(chain.getAwardId())
                    .build();
        }

        //3,规则树抽奖过滤【】
        DefaultTreeFactory.StrategyAwardVO tree = raffleLogicTree(userId,strategyId,chain.getAwardId());
        log.info("抽奖策略计算-规则树 {} {} {} {}", userId, strategyId, tree.getAwardId(), tree.getAwardRuleValue());

        return RaffleAwardEntity.builder()
                .awardId(tree.getAwardId())
                .build();

    }

    protected abstract DefaultChainFactory.StrategyAwardVO raffleLogicChain(String userId, Long strategyId) throws Exception;

    protected abstract DefaultTreeFactory.StrategyAwardVO raffleLogicTree(String userId, Long strategyId, Integer awardId);


    //  protected abstract RaffleActionEntity<RaffleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics);
  //  protected abstract RaffleActionEntity<RaffleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity,String... logic);
}
