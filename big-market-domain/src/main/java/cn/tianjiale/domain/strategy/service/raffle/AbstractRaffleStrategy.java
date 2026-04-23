package cn.tianjiale.domain.strategy.service.raffle;

import cn.tianjiale.domain.strategy.model.entity.*;
import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.model.valobj.StrategyAwardRuleModelVO;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.domain.strategy.service.IRaffleStrategy;
import cn.tianjiale.domain.strategy.service.armory.IStrategyDispatch;
import cn.tianjiale.domain.strategy.service.rule.chain.ILogicLink;
import cn.tianjiale.domain.strategy.service.rule.chain.factory.DefaultChainFactory;
import cn.tianjiale.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.tianjiale.types.enums.ResponseCode;
import cn.tianjiale.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.annotation.Resource;

@Slf4j
public abstract class AbstractRaffleStrategy implements IRaffleStrategy {

    private IStrategyRepository repository;

    private IStrategyDispatch dispatch;
    private final DefaultChainFactory defaultChainFactory;
    public AbstractRaffleStrategy(IStrategyRepository repository,IStrategyDispatch dispatch,DefaultChainFactory defaultChainFactory){
        this.repository = repository;
        this.dispatch = dispatch;
        this.defaultChainFactory = defaultChainFactory;
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
        //抽奖前置规则过滤
        ILogicLink<String, Long, Integer> logicLink = defaultChainFactory.openLogicChain(strategyId);
        Integer awardId = logicLink.apply(userId, strategyId);

        //抽奖中置规则过滤
        StrategyAwardRuleModelVO strategyAwardRuleModelVO = repository.queryStrategyAwardRuleModelVO(strategyId,awardId);
        raffleFactorEntity.setAwardId(awardId);
        RaffleActionEntity<RaffleActionEntity.RaffleCenterEntity> raffleActionEntity1 = doCheckRaffleCenterLogic(raffleFactorEntity,strategyAwardRuleModelVO.raffleCenterRuleModelList());
        if (RuleLogicCheckTypeVO.TAKE_OVER.getCode().equals(raffleActionEntity1.getCode())){
            return RaffleAwardEntity.builder()
                    .awardDesc("幸运奖品")
                    .build();
        }

        return RaffleAwardEntity.builder()
                .awardId(awardId)
                .build();

    }
  //  protected abstract RaffleActionEntity<RaffleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics);
    protected abstract RaffleActionEntity<RaffleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity,String... logic);
}
