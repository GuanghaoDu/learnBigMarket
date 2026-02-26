package cn.bugstack.domain.strategy.model.entity;

import cn.bugstack.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import lombok.*;

/**
 * @author duguanghao
 * @title RuleActionEntity
 * @date 2026/2/24 21:15
 * @description 规则动作实体
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RuleActionEntity <T extends RuleActionEntity.RaffleEntity>{

    private String code = RuleLogicCheckTypeVO.ALLOW.getCode();

    private String info = RuleLogicCheckTypeVO.ALLOW.getInfo();

    private String ruleModel;

    private T data;

    //抽奖返回过去的结果要继承这个类 当作父类
    static public class RaffleEntity{


    }

    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    //抽奖前的结果要继承这个类
    static public class RaffleBeforeEntity extends RaffleEntity{

        private Long strategyId;
        private String ruleWeightValueKey;
        private Integer awardId;
    }

    //抽奖中的结果要继承这个类
    @EqualsAndHashCode(callSuper = true)
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    static public class RaffleCenterEntity extends RaffleEntity{
        private Long strategyId;
        private Integer awardId;
        private String ruleLockValueKey;
        private String ruleDesc;
    }

    //抽奖后的结果要继承这个类
    static public class RaffleAfterEntity extends RaffleEntity{


    }
}
