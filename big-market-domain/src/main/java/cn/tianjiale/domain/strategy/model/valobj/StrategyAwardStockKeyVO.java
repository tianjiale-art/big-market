package cn.tianjiale.domain.strategy.model.valobj;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class StrategyAwardStockKeyVO {
    private Long strategyId;
    private Integer awardId;
}
