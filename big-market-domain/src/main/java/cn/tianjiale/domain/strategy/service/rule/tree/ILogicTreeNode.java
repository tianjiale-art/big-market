package cn.tianjiale.domain.strategy.service.rule.tree;


import cn.tianjiale.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

/**
 * 规则树接口->node
 */
public interface ILogicTreeNode {
    DefaultTreeFactory.TreeActionEntity logic(String userId, Long strategyId, Integer awardId);
}
