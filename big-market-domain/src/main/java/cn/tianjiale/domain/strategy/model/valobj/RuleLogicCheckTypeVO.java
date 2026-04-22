package cn.tianjiale.domain.strategy.model.valobj;


import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum RuleLogicCheckTypeVO {
    ALLOW("0000","放行执行后续流程不受规则引擎影响"),
    TAKE_OVER("0001","接管，后续流程受规则引擎影响"),
    ;
    private final String code;
    private final String info;

}
