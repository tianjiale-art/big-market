package cn.tianjiale.infrastructure.persistent.dao;


import cn.tianjiale.infrastructure.persistent.po.Strategy;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface IStrategyDao {
    Strategy queryStrategyByStrategyId(Long strategyId);
}
