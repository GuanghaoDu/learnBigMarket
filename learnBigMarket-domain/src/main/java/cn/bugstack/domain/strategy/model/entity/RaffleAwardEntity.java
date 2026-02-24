package cn.bugstack.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author duguanghao
 * @title RaffleAwardEntity
 * @date 2026/2/24 21:03
 * @description 抽奖奖品实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleAwardEntity {

    private Long strategyId;

    private Integer awardId;

    private String awardKey;

    private String awardConfig;

    private String awardDesc;

}
