package cn.tianjiale.domain.strategy.service.rule.tree.factory.engine.impl;

import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.model.valobj.RuleTreeNodeLineVO;
import cn.tianjiale.domain.strategy.model.valobj.RuleTreeNodeVO;
import cn.tianjiale.domain.strategy.model.valobj.RuleTreeVO;
import cn.tianjiale.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.tianjiale.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;
import cn.tianjiale.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

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
    public DefaultTreeFactory.StrategyAwardVO process(String userId, Long strategyId, Integer awardId) {
        //1.获取根节点key
        String nextNode = ruleTreeVO.getTreeRootRuleNode();

        //2.获取规则树节点集合treeNodeMap
        Map<String, RuleTreeNodeVO> treeNodeMap = ruleTreeVO.getTreeNodeMap();

        DefaultTreeFactory.StrategyAwardVO  strategyAwardData = null;
        //遍历核心以nextNode为循环条件
        while (nextNode != null){
            //获取当前节点
            RuleTreeNodeVO ruleTreeNodeVO = treeNodeMap.get(nextNode);
            //获取决策节点
            ILogicTreeNode logicTreeNode = logicTreeNodeMap.get(ruleTreeNodeVO.getRuleKey());
            DefaultTreeFactory.TreeActionEntity logic = logicTreeNode.logic(userId, strategyId, awardId);

            //4.2 赋值结果
            strategyAwardData = logic.getStrategyAwardVO();
            RuleLogicCheckTypeVO ruleLogicCheckTypeVO = logic.getRuleLogicCheckTypeVO();


            log.info("决策树引擎:{},treeNode:{},code:{}",ruleTreeVO.getTreeName(),ruleTreeNodeVO.getTreeId(),ruleLogicCheckTypeVO.getCode());

            //获取下一个节点
            nextNode = nextNode(ruleLogicCheckTypeVO.getCode(),ruleTreeNodeVO.getTreeNodeLineVOList());
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
