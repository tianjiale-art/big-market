package cn.tianjiale.infrastructure.persistent.repository;

import cn.tianjiale.domain.strategy.model.entity.StrategyAwardEntity;
import cn.tianjiale.domain.strategy.model.entity.StrategyEntity;
import cn.tianjiale.domain.strategy.model.entity.StrategyRuleEntity;
import cn.tianjiale.domain.strategy.model.valobj.*;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.tianjiale.infrastructure.persistent.dao.*;
import cn.tianjiale.infrastructure.persistent.po.*;
import cn.tianjiale.infrastructure.persistent.redis.IRedisService;
import cn.tianjiale.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Repository
@Slf4j
public class StrategyRepository implements IStrategyRepository {
    @Resource
    private IRedisService redisService;
    @Resource
    IStrategyAwardDao strategyAwardDao;
    @Resource
    private IStrategyDao strategyDao;
    @Resource
    private IStrategyRuleDao strategyRuleDao;
    @Resource
    private IRuleTreeDao ruleTreeDao;
    @Resource
    private IRuleTreeNodeDao ruleTreeNodeDao;
    @Resource
    private IRuleTreeNodeLineDao ruleTreeNodeLineDao;

    @Override
    public List<StrategyAwardEntity> queryStrategyAwardList(Long strategyId) {
        //1.先查询存储是否存在
        String cacheKey = Constants.RedisKey.STRATEGY_AWARD_KEY + strategyId;

        List<StrategyAwardEntity> strategyAwardEntities = redisService.getValue(cacheKey);
        if (strategyAwardEntities != null && !strategyAwardEntities.isEmpty()) return strategyAwardEntities;

        //2.缓存没有使用数据库查询
       List<StrategyAward> strategyAwardList = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);

         strategyAwardEntities = new ArrayList<>(strategyAwardList.size());
        for (int i = 0; i < strategyAwardList.size(); i++) {
            StrategyAward strategyAward = strategyAwardList.get(i);
            StrategyAwardEntity strategyAwardEntity = new StrategyAwardEntity();

            strategyAwardEntity.setStrategyId(strategyAward.getStrategyId());
            strategyAwardEntity.setAwardId(strategyAward.getAwardId());
            strategyAwardEntity.setAwardCountSurplus(strategyAward.getAwardCountSurplus());
            strategyAwardEntity.setAwardCount(strategyAward.getAwardCount());
            strategyAwardEntity.setAwardRate(strategyAward.getAwardRate());
            strategyAwardEntities.add(strategyAwardEntity);

        }
        //3.查询到的数据进行缓存
        redisService.setValue(cacheKey,strategyAwardEntities);

        //4.返回数据
        return strategyAwardEntities;
    }

    @Override
    public void storeStrategyAwardSearchRateTable(String key, int rateRange, Map<Integer, Integer> awardMap) {
        //存储奖品范围
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key,rateRange);

        //存储奖品table
        RMap<Object, Object> map = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key);
        map.putAll(awardMap);

    }

    @Override
    public Integer getStrategyAwardAssemble(String key, Integer rateKey) {
        return redisService.getFromMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + key,rateKey);
    }

    @Override
    public int getRateRange(Long strategyId) {
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId);
    }
    public int getRateRange(String key){
        return redisService.getValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + key);
    }

    @Override
    public StrategyEntity queryStrategyEntityByStrategyId(Long strategyId) {
        //有限从缓冲中获取

        String cacheKey = Constants.RedisKey.STRATEGY_KEY + strategyId;
        StrategyEntity strategyEntity = redisService.getValue(cacheKey);
        if (null != strategyEntity) return strategyEntity;
        Strategy strategy = strategyDao.queryStrategyByStrategyId(strategyId);
        strategyEntity = StrategyEntity.builder()
                .strategyId(strategy.getStrategyId())
                .strategyDesc(strategy.getStrategyDesc())
                .ruleModels(strategy.getRuleModels())
                .build();
        redisService.setValue(cacheKey, strategyEntity);
        return strategyEntity;

    }

    @Override
    public StrategyRuleEntity queryStrategyRule(Long strategyId, String ruleWeight) {
        StrategyRule strategyRule1 = new StrategyRule();
        strategyRule1.setStrategyId(strategyId);

        strategyRule1.setRuleModel(ruleWeight);

        StrategyRule strategyRule = strategyRuleDao.queryStrategyRule(strategyRule1);
       return StrategyRuleEntity.builder()
               .strategyId(strategyId.intValue())
               .awardId(strategyRule.getAwardId())
               .ruleModel(strategyRule.getRuleModel())
               .ruleType(strategyRule.getRuleType())
               .ruleValue(strategyRule.getRuleValue())
               .ruleDesc(strategyRule.getRuleDesc())
               .build();
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, Integer awardId, String ruleModel) {

        StrategyRule strategyRule = new StrategyRule();
        strategyRule.setAwardId(awardId);
        strategyRule.setStrategyId(strategyId);
        strategyRule.setRuleModel(ruleModel);
       return strategyRuleDao.queryStrategyRuleValue(strategyRule);
    }

    @Override
    public String queryStrategyRuleValue(Long strategyId, String ruleModel) {
        return queryStrategyRuleValue(strategyId,null,ruleModel);
    }

    @Override
    public StrategyAwardRuleModelVO queryStrategyAwardRuleModelVO(Long strategyId, Integer awardId) {
        StrategyAward strategyAward = new StrategyAward();
        strategyAward.setStrategyId(strategyId);
        strategyAward.setAwardId(awardId);
       String ruleModel = strategyAwardDao.queryStrategyAwardRuleModel(strategyAward);
       if(ruleModel == null) return null;
        return new StrategyAwardRuleModelVO(ruleModel);
    }

    @Override
    public RuleTreeVO queryRuleTreeVOByTreeId(String treeId) {
        String key = Constants.RedisKey.RULE_TREE_VO_KEY + treeId;
        //redis中查询
        RuleTreeVO ruleTreeVO = redisService.getValue(key);
        if (ruleTreeVO != null) return ruleTreeVO;

        //根据treeId查询ruleTree ,ruleTreeNodelist,ruleTreeNodeLinelist
        RuleTree ruleTree = ruleTreeDao.queryRuleTreeByTreeId(treeId);
        List<RuleTreeNode> ruleTreeNodes = ruleTreeNodeDao.queryRuleNodeListByTreeId(treeId);
        List<RuleTreeNodeLine> ruleTreeNodeLines = ruleTreeNodeLineDao.queryRuleNodeLineListByTreeId(treeId);

        //ruleTreeNodeList ->map<String,ruleTreeNOdeVOList>
        Map<String,List<RuleTreeNodeLineVO>> ruleTreeNodeLineMap = new TreeMap<>();
        for (RuleTreeNodeLine ruleTreeNodeLine : ruleTreeNodeLines) {
            RuleTreeNodeLineVO ruleTreeNodeLineVO = RuleTreeNodeLineVO.builder()
                    .treeId(treeId)
                    .ruleNodeFrom(ruleTreeNodeLine.getRuleNodeFrom())
                    .ruleNodeTo(ruleTreeNodeLine.getRuleNodeTo())
                    .ruleLimitType(RuleLimitTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitType()))
                    .ruleLimitValue(RuleLogicCheckTypeVO.valueOf(ruleTreeNodeLine.getRuleLimitValue()))
                    .build();
            List<RuleTreeNodeLineVO> ruleTreeNodeLineVOS = ruleTreeNodeLineMap.computeIfAbsent(ruleTreeNodeLine.getRuleNodeFrom(), k -> new ArrayList<>());
            ruleTreeNodeLineVOS.add(ruleTreeNodeLineVO);
        }

        //ruleTreeNodeMap<String,ruleTreeNodeVO>
        Map<String,RuleTreeNodeVO> ruleTreeNodeVOMap = new TreeMap<>();
        for (RuleTreeNode ruleTreeNode : ruleTreeNodes) {
            RuleTreeNodeVO ruleTreeNodeVO = RuleTreeNodeVO.builder()
                    .treeId(treeId)
                    .ruleDesc(ruleTreeNode.getRuleDesc())
                    .ruleKey(ruleTreeNode.getRuleKey())
                    .ruleValue(ruleTreeNode.getRuleValue())
                    .treeNodeLineVOList(ruleTreeNodeLineMap.get(ruleTreeNode.getRuleKey()))
                    .build();
            ruleTreeNodeVOMap.put(ruleTreeNode.getRuleKey(),ruleTreeNodeVO);
        }
        ruleTreeVO = RuleTreeVO.builder()
                .treeId(treeId)
                .treeDesc(ruleTree.getTreeDesc())
                .treeName(ruleTree.getTreeName())
                .treeRootRuleNode(ruleTree.getTreeNodeRuleKey())
                .treeNodeMap(ruleTreeNodeVOMap)
                .build();

        redisService.setValue(key,ruleTreeVO);

        //return ruleTreeVO
        return ruleTreeVO;

    }
}
