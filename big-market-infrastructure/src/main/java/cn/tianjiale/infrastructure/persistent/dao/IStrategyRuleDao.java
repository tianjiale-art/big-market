package cn.tianjiale.infrastructure.persistent.dao;


import cn.tianjiale.infrastructure.persistent.po.StrategyRule;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IStrategyRuleDao {
    StrategyRule queryStrategyRule(StrategyRule strategyRule);

    String queryStrategyRuleValue(StrategyRule strategyRule);
}
