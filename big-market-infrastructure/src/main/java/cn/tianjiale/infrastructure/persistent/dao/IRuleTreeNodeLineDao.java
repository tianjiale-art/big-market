package cn.tianjiale.infrastructure.persistent.dao;


import cn.tianjiale.infrastructure.persistent.po.RuleTreeNodeLine;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IRuleTreeNodeLineDao {
    List<RuleTreeNodeLine> queryRuleNodeLineListByTreeId(String treeId);
}
