package cn.tianjiale.domain.strategy.model.entity;


import cn.tianjiale.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyRuleEntity {
    /**
     * 抽奖策略ID
     */
    private Integer strategyId;
    /** 抽奖奖品ID【规则类型为策略，则不需要奖品ID】 */
    private Integer awardId;
    /**
     * 抽象规则类型；1-策略规则、2-奖品规则
     */
    private Integer ruleType;

    /**
     * 抽奖规则类型【rule_random - 随机值计算、rule_lock - 抽奖几次后解锁、rule_luck_award - 幸运奖(兜底奖品)】
     */
    private String ruleModel;

    /**
     * 抽奖规则比值
     */
    private String ruleValue;
    /** 抽奖规则描述 */
    private String ruleDesc;

    //4000:101,102 5000,101,102 6000:101,102
    public Map<String, List<Integer>> getRuleWeightValues(){
        //参数校验
        if(!"rule_weight".equals(ruleModel)) return null;
        Map<String, List<Integer>> ruleWeightValues = new HashMap<>();
        String[] split = ruleValue.split(Constants.SPACE);
        for (String key : split) {
            //检查输入是否为空
            if (key == null || key.isEmpty()) {
                return ruleWeightValues;
            }
            //分割字符串以获取键和值
            String[] split1 = key.split(Constants.COLON);
            if (split1.length != 2) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + key);
            }

            String[] groups = split1[1].split(Constants.SPLIT);
            List<Integer> values = new ArrayList<>(groups.length);
            for (String group : groups) {
                values.add(Integer.parseInt(group));
            }
            ruleWeightValues.put(key,values);
        }
        return ruleWeightValues;
    }
}
