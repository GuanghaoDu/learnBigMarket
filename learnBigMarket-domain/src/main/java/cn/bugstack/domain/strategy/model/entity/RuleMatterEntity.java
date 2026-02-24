package cn.bugstack.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author duguanghao
 * @title RuleMatterEntity
 * @date 2026/2/24 21:11
 * @description 规则物料实体对象，用于过滤规则的必要参数
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleMatterEntity {

    private String userId;

    private Long strategyId;

    private Integer awardId;

    private String ruleModel;
}
