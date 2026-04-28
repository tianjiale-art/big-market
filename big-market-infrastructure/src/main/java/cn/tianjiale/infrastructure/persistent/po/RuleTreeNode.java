package cn.tianjiale.infrastructure.persistent.po;


import java.util.Date;
import lombok.Data;

/**
 * 规则表-树节点
 * @TableName rule_tree_node
 */

@Data
public class RuleTreeNode {
    /**
     * 自增ID
     */

    private Long id;

    /**
     * 规则树ID
     */
    private String treeId;

    /**
     * 规则Key
     */
    private String ruleKey;

    /**
     * 规则描述
     */
    private String ruleDesc;

    /**
     * 规则比值
     */
    private String ruleValue;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}