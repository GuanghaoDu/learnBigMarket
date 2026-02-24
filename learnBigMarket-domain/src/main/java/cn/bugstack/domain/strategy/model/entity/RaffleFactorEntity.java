package cn.bugstack.domain.strategy.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author duguanghao
 * @title RaffleFactorEntity
 * @date 2026/2/24 21:00
 * @description 抽奖因素实体类
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RaffleFactorEntity {

        private String userId;
        private Long strategyId;
}
