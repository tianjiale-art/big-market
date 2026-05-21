package cn.tianjiale.domain.strategy.model.entity;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleAwardEntity {


    /**
     * 抽奖奖品ID - 内部流转使用
     */
    private Integer awardId;


    /**
     * 奖品配置信息
     */
    private String awardConfig;

    /**
     * 奖品顺序号
     */
    private String sort;
}
