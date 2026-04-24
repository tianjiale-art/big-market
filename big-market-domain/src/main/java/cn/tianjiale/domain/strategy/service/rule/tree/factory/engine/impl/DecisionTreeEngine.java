package cn.tianjiale.domain.strategy.service.rule.tree.factory.engine.impl;

import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import cn.tianjiale.domain.strategy.model.valobj.RuleTreeNodeVO;
import cn.tianjiale.domain.strategy.model.valobj.RuleTreeVO;
import cn.tianjiale.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.tianjiale.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.tianjiale.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;


@Slf4j
public class DecisionTreeEngine implements IDecisionTreeEngine {
    private Map<String, ILogicTreeNode> logicTreeNodeMap;
    private RuleTreeVO ruleTreeVO;

    public DecisionTreeEngine(Map<String, ILogicTreeNode> logicTreeNodeMap, RuleTreeVO ruleTreeVO) {
        this.logicTreeNodeMap = logicTreeNodeMap;
        this.ruleTreeVO = ruleTreeVO;
    }

    @Override
    public DefaultTreeFactory.StrategyAwardData process(String userId, Long strategyId, Integer awardId) {
        //1.获取根节点key
        String treeRootRuleNode = ruleTreeVO.getTreeRootRuleNode();

        //2.获取规则树节点集合treeNodeMap
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        //3.根据根节点key获取起始节点ruleTreeNodeVO
        RuleTreeNodeVO ruleTreeNodeVO = treeNodeMap.get(treeRootRuleNode);



        //4.从起始节点开始执行
        DefaultTreeFactory.StrategyAwardData  strategyAwardData = null;
        while (ruleTreeNodeVO != null){
            //4.1 获取决策节点——>真正用来处理业务的
            String ruleKey = ruleTreeNodeVO.getRuleKey();
            ILogicTreeNode logicTreeNode = logicTreeNodeMap.get(ruleKey);
            DefaultTreeFactory.TreeActionEntity logic = logicTreeNode.logic(userId, strategyId, awardId);

            //4.2 赋值结果
            strategyAwardData = logic.getStrategyAwardData();

            //4.3 找到连线ruleNodeTreeLineVO
            List<RuleTreeNodeLineVO> treeNodeLineVOList = ruleTreeNodeVO.getTreeNodeLineVOList();
            RuleLogicCheckTypeVO ruleLogicCheckTypeVO = logic.getRuleLogicCheckTypeVO();
            log.info("决策树引擎:{},treeNode:{},code:{}",ruleTreeVO.getTreeName(),ruleTreeNodeVO.getTreeId(),ruleLogicCheckTypeVO.getCode());

            //4.4 获取下一个决策节点
           String nextNode = nextNode(ruleLogicCheckTypeVO.getCode(),treeNodeLineVOList);
           ruleTreeNodeVO = treeNodeMap.get(nextNode);

        }
        //4.返回结果
        return strategyAwardData;
    }

    private String nextNode(String code, List<RuleTreeNodeLineVO> treeNodeLineVOList) {

        //参数校验
        if (treeNodeLineVOList == null || treeNodeLineVOList.isEmpty()) return null;
        for (RuleTreeNodeLineVO ruleTreeNodeLineVO : treeNodeLineVOList) {
            if (decisionLogic(code,ruleTreeNodeLineVO)){
                return ruleTreeNodeLineVO.getRuleNodeTo();
            }
        }
        throw new RuntimeException("决策树引擎计算失败，未找到可执行节点");
    }
    private boolean decisionLogic(String code,RuleTreeNodeLineVO ruleTreeNodeLineVO){
        switch (ruleTreeNodeLineVO.getRuleLimitType()){
            case EQUAL :
                return code.equals(ruleTreeNodeLineVO.getRuleLimitValue().getCode());
            default:
                return false;
        }
    }
}
