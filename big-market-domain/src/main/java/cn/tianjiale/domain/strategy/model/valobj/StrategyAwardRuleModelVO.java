package cn.tianjiale.domain.strategy.model.valobj;

import cn.tianjiale.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.tianjiale.types.common.Constants;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Data
public class StrategyAwardRuleModelVO {
    private String ruleModels;

    public String[] raffleCenterRuleModelList(){
        if (StringUtils.isBlank(ruleModels)) return null;
        ArrayList<Object> ruleModelList = new ArrayList<>();
        String[] stringRuleModels = this.ruleModels.split(Constants.SPLIT);
        for (String stringRuleModel : stringRuleModels) {
            if (DefaultLogicFactory.isCenter(stringRuleModel)){
                ruleModelList.add(stringRuleModel);
            }
        }
return ruleModelList.toArray(new String[0]);
    }
    public String[] raffleAfterRuleModelList(){
        if (StringUtils.isBlank(ruleModels)) return null;
        ArrayList<Object> ruleModelList = new ArrayList<>();
        String[] stringRuleModels = this.ruleModels.split(Constants.SPLIT);
        for (String stringRuleModel : stringRuleModels) {
            if (DefaultLogicFactory.isAfter(stringRuleModel)){
                ruleModelList.add(stringRuleModel);
            }
        }
        return ruleModelList.toArray(new String[0]);
    }
}
