package cn.bugstack.domain.strategy.service.armory;


import cn.bugstack.domain.strategy.model.entity.StrategyAwardEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyEntity;
import cn.bugstack.domain.strategy.model.entity.StrategyRuleEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.types.enums.ResponseCode;
import cn.bugstack.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.util.*;

/**
 * @desc
 * @author duguanghao
 * @date 2026-02-03 19:20
 **/
@Slf4j
@Service
public class StrategyArmory implements IStrategyArmory {

    @Resource
    private IStrategyRepository repository;


    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {

        List<StrategyAwardEntity> strategyAwardEntities = repository.queryStrategyAwardList(strategyId);
        assembleLotteryStrategy(String.valueOf(strategyId), strategyAwardEntities);
        //创建策略权重配置 适用于rule_weight 权重规则配置
        StrategyEntity strategyEntity = repository.queryStrategyEntityByStrategyId(strategyId);
        String ruleWeight = strategyEntity.getRuleWeight();
        if (null == ruleWeight) {
            return true;
        }
        StrategyRuleEntity strategyRuleEntity = repository.queryStrategyRule(strategyId, ruleWeight);
        if (null == strategyRuleEntity) {
            throw new AppException(ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getCode(),
                    ResponseCode.STRATEGY_RULE_WEIGHT_IS_NULL.getInfo());
        }
        Map<String, List<Integer>> ruleWeightValueMap = strategyRuleEntity.getRuleWeightValues();
        for (String key : ruleWeightValueMap.keySet()) {
            List<Integer> ruleWeightValueList = ruleWeightValueMap.get(key);
            ArrayList<StrategyAwardEntity> strategyAwardEntitiesClone = new ArrayList<>(strategyAwardEntities);
            strategyAwardEntitiesClone.removeIf(entity -> !ruleWeightValueList.contains(entity.getAwardId()));
            assembleLotteryStrategy(String.valueOf(strategyId).concat("_").concat(key),strategyAwardEntitiesClone);
        }
        return true;
    }

    private void assembleLotteryStrategy(String strategyId, List<StrategyAwardEntity> strategyAwardEntities) {
        //获取最小概率
        BigDecimal minAwardRate = strategyAwardEntities.stream()
                .map(strategyArmoryEntity -> strategyArmoryEntity.getAwardRate())
                .min(BigDecimal::compareTo).orElse(null);

        log.info("最小概率为：{}", minAwardRate);

        //获取概率值的总和
        BigDecimal totalAwardRate = strategyAwardEntities.stream()
                .map(strategyArmoryEntity -> strategyArmoryEntity.getAwardRate())
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        log.info("概率总和为：{}", totalAwardRate);

        //获取概率的范围
        BigDecimal rangeAwardRate = totalAwardRate.divide(minAwardRate, BigDecimal.ROUND_CEILING);
        log.info("概率范围为：{}", rangeAwardRate);

        //创建概率表
        List<Integer> strategyAwardSerachRateTable = new ArrayList<>();
        //构造概率表格
        for (StrategyAwardEntity strategyAwardEntitie : strategyAwardEntities) {

            for (int i = 0; i < rangeAwardRate.multiply(strategyAwardEntitie.getAwardRate()).setScale(BigDecimal.ROUND_CEILING).intValue(); i++) {
                //将奖品Id放在概率表中，如果概率表中该奖品Id出现的次数越多，则表示该奖品的概率越大
                strategyAwardSerachRateTable.add(strategyAwardEntitie.getAwardId());
            }
            log.info("奖品Id:{}，在概率表中出现次数为：{}", strategyAwardEntitie.getStrategyId(), strategyAwardSerachRateTable.size());
        }
        Collections.shuffle(strategyAwardSerachRateTable);

        //构造键值对 通过概率获取奖品Id
        Map<Integer, Integer> shuffleStrategyAwardSerachRateMap = new LinkedHashMap<>();
        for (int i = 0; i < strategyAwardSerachRateTable.size(); i++) {
            shuffleStrategyAwardSerachRateMap.put(i, strategyAwardSerachRateTable.get(i));
        }

        repository.storeStrategyAwardSearchRateTable(strategyId, shuffleStrategyAwardSerachRateMap.size(), shuffleStrategyAwardSerachRateMap);
    }


}
