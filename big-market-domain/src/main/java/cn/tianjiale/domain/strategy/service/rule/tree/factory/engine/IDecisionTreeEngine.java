package cn.tianjiale.domain.strategy.service.rule.tree.factory.engine;

import cn.tianjiale.domain.strategy.service.rule.tree.factory.DefaultTreeFactory;

public interface IDecisionTreeEngine {
  DefaultTreeFactory.StrategyAwardVO process(String userId,Long strategy,Integer awardId);
}
