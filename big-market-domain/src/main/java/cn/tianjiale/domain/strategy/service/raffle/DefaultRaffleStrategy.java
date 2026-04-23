package cn.tianjiale.domain.strategy.service.raffle;

import cn.tianjiale.domain.strategy.model.entity.RaffleActionEntity;
import cn.tianjiale.domain.strategy.model.entity.RaffleFactorEntity;
import cn.tianjiale.domain.strategy.model.entity.RuleMatterEntity;
import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.service.rule.ILogicFilter;
import cn.tianjiale.domain.strategy.service.rule.factory.DefaultLogicFactory;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


/**
 * 默认抽奖过滤实现
 */
@Service
@Slf4j
public class DefaultRaffleStrategy extends AbstractRaffleStrategy{

    @Resource
    private DefaultLogicFactory logicFactory;
    @Override
    protected RaffleActionEntity<RaffleActionEntity.RaffleBeforeEntity> doCheckRaffleBeforeLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {
        if (logics == null||logics.length == 0 ){
            return RaffleActionEntity.<RaffleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }
        //1.先过滤黑名单
        String blackLists = Arrays.stream(logics)
                .filter(str -> str.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .findFirst()
                .orElse(null);
        Map<String, ILogicFilter<RaffleActionEntity.RaffleBeforeEntity>> logicFilterMap = logicFactory.openLogicFilter();
        if (StringUtils.isNotBlank(blackLists)){
            ILogicFilter<RaffleActionEntity.RaffleBeforeEntity> raffleEntityILogicFilter = logicFilterMap.get(blackLists);
            RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
            ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
            ruleMatterEntity.setRuleModel(blackLists);
            ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
            RaffleActionEntity<RaffleActionEntity.RaffleBeforeEntity> filter = raffleEntityILogicFilter.filter(ruleMatterEntity);

            if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(filter.getCode())) return filter;
        }

        //2.然后再顺序过滤其他规则

        //2.1logics过滤掉黑名单
        List<String> collect = Arrays.stream(logics)
                .filter(str -> !str.equals(DefaultLogicFactory.LogicModel.RULE_BLACKLIST.getCode()))
                .collect(Collectors.toList());

        //2.2依次过滤其他规则
        RaffleActionEntity<RaffleActionEntity.RaffleBeforeEntity> raffleActionEntity = null;
        for (String rule : collect) {
            if (StringUtils.isNotBlank(rule)) {
                ILogicFilter<RaffleActionEntity.RaffleBeforeEntity> logicFilter = logicFilterMap.get(rule);
                RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
                ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
                ruleMatterEntity.setRuleModel(rule);
                ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
                raffleActionEntity = logicFilter.filter(ruleMatterEntity);
                if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(raffleActionEntity.getCode())) return raffleActionEntity;
            }

        }
        return raffleActionEntity;

    }

    @Override
    protected RaffleActionEntity<RaffleActionEntity.RaffleCenterEntity> doCheckRaffleCenterLogic(RaffleFactorEntity raffleFactorEntity, String... logics) {
        if (logics.length == 0 || logics == null){
            return RaffleActionEntity.<RaffleActionEntity.RaffleCenterEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }
        //2.2依次过滤其他规则
        RaffleActionEntity<RaffleActionEntity.RaffleCenterEntity> raffleActionEntity = null;
        Map<String, ILogicFilter<RaffleActionEntity.RaffleCenterEntity>> logicFilterMap = logicFactory.openLogicFilter();
        for (String rule : logics) {
            if (StringUtils.isNotBlank(rule)) {
                ILogicFilter<RaffleActionEntity.RaffleCenterEntity> logicFilter = logicFilterMap.get(rule);
                RuleMatterEntity ruleMatterEntity = new RuleMatterEntity();
                ruleMatterEntity.setStrategyId(raffleFactorEntity.getStrategyId());
                ruleMatterEntity.setRuleModel(rule);
                ruleMatterEntity.setUserId(raffleFactorEntity.getUserId());
                ruleMatterEntity.setAwardId(raffleFactorEntity.getAwardId());

                raffleActionEntity = logicFilter.filter(ruleMatterEntity);
                if (!RuleLogicCheckTypeVO.ALLOW.getCode().equals(raffleActionEntity.getCode())) return raffleActionEntity;
            }

        }
        return raffleActionEntity;

    }


}
