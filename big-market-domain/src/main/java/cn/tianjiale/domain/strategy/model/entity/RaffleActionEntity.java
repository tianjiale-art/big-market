package cn.tianjiale.domain.strategy.model.entity;


import cn.tianjiale.domain.strategy.model.valobj.RuleLogicCheckTypeVO;
import lombok.*;

/**
 * 规则动作实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleActionEntity<T extends RaffleActionEntity.RaffleEntity> {
    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();
    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();;
    private String ruleModel;
    private T data;

    /**
     * data只能存放抽奖阶段实体对象，不能随便塞其他对象
     * 保证类型安全，避免数据乱传
     */
    static public class RaffleEntity{

    }
    // 抽奖之前
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class RaffleBeforeEntity extends RaffleEntity{
        private Long strategyId;
        private String ruleWeightValueKey;
        private Integer awardId;
    }

    static public class RaffleCenterEntity extends RaffleEntity{

    }
    static public class RaffleAfterEntity extends RaffleEntity{

    }

}
