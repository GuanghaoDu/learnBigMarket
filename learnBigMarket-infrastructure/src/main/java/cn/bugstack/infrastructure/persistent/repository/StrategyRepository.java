package cn.bugstack.infrastructure.persistent.repository;

import cn.bugstack.domain.strategy.model.entity.StrategyArmoryEntity;
import cn.bugstack.domain.strategy.repository.IStrategyRepository;
import cn.bugstack.infrastructure.persistent.dao.IStrategyAwardDao;
import cn.bugstack.infrastructure.persistent.po.StrategyAward;
import cn.bugstack.infrastructure.redis.IRedisService;
import cn.bugstack.types.common.Constants;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RMap;
import org.springframework.stereotype.Repository;


import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @desc
 * @author duguanghao
 * @date 2026-02-03 19:21
 **/
@Slf4j
@Repository
public class StrategyRepository implements IStrategyRepository {

    @Resource
    private IRedisService redisService;

    @Resource
    private IStrategyAwardDao strategyAwardDao;

    /**
     * @desc 查询策略奖品列表
     * @author duguanghao
     * @date 2026-02-04 19:40
     * @param strategyId
     * @return java.util.List<cn.bugstack.domain.strategy.model.entity.StrategyArmoryEntity>
    **/
    @Override
    public List<StrategyArmoryEntity> queryStrategyAwardList(Long strategyId) {

        String cache = Constants.RedisKey.STRATEGY_AWARD_LIST_KEY + strategyId;
        List<StrategyArmoryEntity> strategyArmoryEntities = redisService.getValue(cache);
        if (null != strategyArmoryEntities) {
            return strategyArmoryEntities;
        }
        List<StrategyAward> strategyAwards = strategyAwardDao.queryStrategyAwardListByStrategyId(strategyId);
        strategyArmoryEntities = new ArrayList<>();
        for (StrategyAward strategyAward : strategyAwards) {
            StrategyArmoryEntity strategyArmoryEntity = StrategyArmoryEntity.builder()
                    .strategyId(strategyAward.getStrategyId())
                    .awardId(strategyAward.getAwardId())
                    .awardTitle(strategyAward.getAwardTitle())
                    .awardSubtitle(strategyAward.getAwardSubtitle())
                    .awardCount(strategyAward.getAwardCount())
                    .awardCountSurplus(strategyAward.getAwardCountSurplus())
                    .awardRate(strategyAward.getAwardRate())
                    .sort(strategyAward.getSort())
                    .ruleModels(strategyAward.getRuleModels())
                    .build();
            strategyArmoryEntities.add(strategyArmoryEntity);
        }
        redisService.setValue(cache, strategyArmoryEntities);
        return strategyArmoryEntities;
    }

    /**
     * @desc 存储策略奖品搜索率表
     * @author duguanghao
     * @date 2026-02-04 19:40
     * @param strategyId
     * @param size
     * @param shuffleStrategyAwardSerachRateMap
     *
     **/
    /**
     * 在 Redisson 中，当你调用 getMap 方法时，如果指定的 key 不存在，Redisson 并不会立即在 Redis 数据库中创建这个 key。相反，它会返回一个 RMap 对象的实例，这个实例是一个本地的 Java 对象，它代表了 Redis 中的一个哈希（hash）。
     * <p>
     * 当你开始使用这个 RMap 实例进行操作，比如添加键值对，那么 Redisson 会在 Redis 数据库中创建相应的 key，并将数据存储在这个 key 对应的哈希中。如果你只是获取了 RMap 实例而没有进行任何操作，那么在 Redis 数据库中是不会有任何变化的。
     * <p>
     * 简单来说，getMap 方法返回的 RMap 对象是懒加载的，只有在你实际进行操作时，Redis 数据库中的数据结构才会被创建或修改。
     */
    @Override
    public <K,V> void storeStrategyAwardSearchRateTable(Long strategyId, Integer rateRange, Map<K, V> shuffleStrategyAwardSerachRateMap) {
        redisService.setValue(Constants.RedisKey.STRATEGY_RATE_RANGE_KEY + strategyId,rateRange);
        // 2. 存储概率查找表 - 存在则删除重新装配
        String tableCacheKey = Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId;
        if (redisService.isExists(tableCacheKey)) {
            redisService.remove(tableCacheKey);
        }
        Map<K, V> cacheRateTable  = redisService.getMap(Constants.RedisKey.STRATEGY_RATE_TABLE_KEY + strategyId);
        cacheRateTable.putAll(shuffleStrategyAwardSerachRateMap);
    }
}
