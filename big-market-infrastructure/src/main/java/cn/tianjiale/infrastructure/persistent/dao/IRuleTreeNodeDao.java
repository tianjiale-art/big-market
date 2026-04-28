package cn.tianjiale.infrastructure.persistent.dao;


import cn.tianjiale.infrastructure.persistent.po.RuleTreeNode;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IRuleTreeNodeDao {
    List<RuleTreeNode> queryRuleNodeListByTreeId(String treeId);
}
