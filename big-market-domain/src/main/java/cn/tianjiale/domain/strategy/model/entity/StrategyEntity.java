package cn.tianjiale.domain.strategy.model.entity;


import cn.tianjiale.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyEntity {
    /**
     * 抽奖策略ID
     */
    private Long strategyId;
    /**
     * 规则模型，rule配置的模型同步到此表，便于使用
     */
    /** 抽奖策略描述 */
    private String strategyDesc;
    private String ruleModels;

    public String[] ruleModel(){
        if (StringUtils.isBlank(ruleModels))
            return null;
        return ruleModels.split(Constants.SPLIT);
    }
    public String getRuleWeight(){
        String[] RuleModels = this.ruleModel();
        if (RuleModels == null){
            return null;
        }
        for (String ruleModel : RuleModels) {
            if (ruleModel.equals("rule_weight")){
                return ruleModel;
            }
        }
        return null;
    }
}
