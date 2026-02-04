package cn.bugstack.domain.strategy.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyArmoryEntity;
import cn.bugstack.domain.strategy.service.armory.StrategyArmory;

import java.util.List;
import java.util.Map;

public interface IStrategyRepository {

    List<StrategyArmoryEntity> queryStrategyAwardList(Long strategyId);

    <K,V> void storeStrategyAwardSearchRateTable(Long strategyId, Integer size, Map<K, V> shuffleStrategyAwardSerachRateMap);
}
