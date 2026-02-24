package cn.bugstack.domain.strategy.model.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author duguanghao
 * @title RuleLogicCheckTypeVO
 * @date 2026/2/24 21:28
 * @description TODO
 */
@Getter
@AllArgsConstructor
public enum RuleLogicCheckTypeVO {

    ALLOW("0000","放行：执行后续流程，不受规则影响"),
    TAKE_OVER("0001","接管：执行后续流程，受规则影响"),
    ;
    private String code;

    private String info;
}
