package cn.tianjiale.domain.strategy.service.rule.tree.factory;

import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import cn.tianjiale.domain.strategy.model.valobj.RuleTreeVO;
import cn.tianjiale.domain.strategy.service.rule.tree.ILogicTreeNode;
import cn.tianjiale.domain.strategy.service.rule.tree.factory.engine.IDecisionTreeEngine;
import cn.tianjiale.domain.strategy.service.rule.tree.factory.engine.impl.DecisionTreeEngine;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class DefaultTreeFactory {
    Map<String, ILogicTreeNode> logicTreeNodeMap;

    public DefaultTreeFactory(Map<String,ILogicTreeNode> logicTreeNodeMap){
        this.logicTreeNodeMap = logicTreeNodeMap;
    }
public IDecisionTreeEngine openLogicTree(RuleTreeVO ruleTreeVO){
        return new DecisionTreeEngine(logicTreeNodeMap,ruleTreeVO);
}

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeActionEntity{
        private RuleLogicCheckTypeVO ruleLogicCheckTypeVO;
        private StrategyAwardVO strategyAwardVO;
    }
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class StrategyAwardVO{
        private Integer awardId;
        private String awardRuleValue;
    }
}
