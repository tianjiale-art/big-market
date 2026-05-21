package cn.tianjiale.trigger.api.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 抽奖奖品列表，应答对象
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleAwardListResponseDTO {
    private Integer awardId;
    private String awardTitle;
    //奖品副标题【抽奖1次后解锁】
    private String awardSubtitle;
    //排序编号
    private Integer sort;
}
