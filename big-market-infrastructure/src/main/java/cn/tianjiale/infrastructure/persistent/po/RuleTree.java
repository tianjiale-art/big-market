package cn.tianjiale.infrastructure.persistent.po;


import java.util.Date;
import lombok.Data;

/**
 * 规则表-树
 * @TableName rule_tree
 */

@Data
public class RuleTree {
    /**
     * 自增ID
     */

    private Long id;

    /**
     * 规则树ID
     */
    private String treeId;

    /**
     * 规则树名称
     */
    private String treeName;

    /**
     * 规则树描述
     */
    private String treeDesc;

    /**
     * 规则树根入口规则
     */
    private String treeNodeRuleKey;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;
}