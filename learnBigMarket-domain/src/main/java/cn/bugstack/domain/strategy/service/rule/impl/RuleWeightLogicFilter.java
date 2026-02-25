package cn.bugstack.domain.strategy.service.rule.impl;

import cn.bugstack.domain.strategy.model.entity.RuleActionEntity;
import cn.bugstack.domain.strategy.model.entity.RuleMatterEntity;
import cn.bugstack.domain.strategy.model.vo.RuleLogicCheckTypeVO;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.domain.strategy.service.annotation.LogicStrategy;
import cn.bugstack.domain.strategy.service.rule.ILogicFilter;
import cn.bugstack.domain.strategy.service.rule.factory.DefaultLogicFactory;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author duguanghao
 * @title RuleWeightLogicFilter
 * @date 2026/2/24 22:56
 * @description 过滤抽奖权重
 */
@Slf4j
@Component
@LogicStrategy(logicMode = DefaultLogicFactory.LogicModel.RULE_WIGHT)
public class RuleWeightLogicFilter implements ILogicFilter<RuleActionEntity.RaffleBeforeEntity> {

    @Resource
    private IStrategyRepository repository;
    // 先给个用户默认抽奖积分，后续可以通过查询数据库操作获取用户消耗积分
    public Long userScore = 4500L;
    @Override
    public RuleActionEntity<RuleActionEntity.RaffleBeforeEntity> filter(RuleMatterEntity ruleMatterEntity) {

        //获取权重模型抽奖规则比值
        String ruleValue = repository.queryStrategyRuleValue(ruleMatterEntity.getStrategyId(), ruleMatterEntity.getAwardId(), ruleMatterEntity.getRuleModel());
        Map<Long, String> analyticalValueGroup = getAnalyticalValue(ruleValue);
        if (analyticalValueGroup.isEmpty() || null == analyticalValueGroup) {
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                    .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                    .build();
        }
        // 2. 转换Keys值，并默认排序
        List<Long> analyticalSortedKeys = new ArrayList<>(analyticalValueGroup.keySet());
        Collections.sort(analyticalSortedKeys);

        Long minSorce = analyticalSortedKeys.stream().filter(sorce -> userScore >= sorce).findFirst().orElse(null);
        if (null != minSorce) {
            String ruleWeightValue = analyticalValueGroup.get(minSorce);
            log.info("命中权重规则，用户：{}", ruleMatterEntity.getUserId());
            return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                    .code(RuleLogicCheckTypeVO.TAKE_OVER.getCode())
                    .info(RuleLogicCheckTypeVO.TAKE_OVER.getInfo())
                    .data(RuleActionEntity.RaffleBeforeEntity.builder()
                            .awardId(ruleMatterEntity.getAwardId())
                            .strategyId(ruleMatterEntity.getStrategyId())
                            .ruleWeightValueKey(ruleWeightValue)
                            .build())
                    .ruleModel(DefaultLogicFactory.LogicModel.RULE_WIGHT.getCode())
                    .build();
        }
        return RuleActionEntity.<RuleActionEntity.RaffleBeforeEntity>builder()
                .code(RuleLogicCheckTypeVO.ALLOW.getCode())
                .info(RuleLogicCheckTypeVO.ALLOW.getInfo())
                .build();
    }

    /**
     * @desc 解析权重规则值，获取积分范围和对应的ruleWeightValueKey
     * @author duguanghao
     * @date 2026-02-25 15:39
     * @param ruleValue
     * @return java.util.Map<java.lang.Long, java.lang.String>
     **/
    private Map<Long, String> getAnalyticalValue(String ruleValue) {
        String[] ruleValueGroups = ruleValue.split(Constants.SPACE);
        Map<Long, String> ruleValueMap = new HashMap<>();
        for (String ruleValueKey : ruleValueGroups) {
            String[] splitRuleValue = ruleValueKey.split(Constants.COLON);
            if (2 != splitRuleValue.length) {
                throw new IllegalArgumentException("rule_weight rule_rule invalid input format" + ruleValueKey);
            }
            ruleValueMap.put(Long.parseUnsignedLong(splitRuleValue[0]), ruleValueKey);
        }
        return ruleValueMap;
    }
}
