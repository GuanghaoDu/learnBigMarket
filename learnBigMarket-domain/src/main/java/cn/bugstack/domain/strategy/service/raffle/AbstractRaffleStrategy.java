package cn.bugstack.domain.strategy.service.raffle;

import cn.bugstack.domain.strategy.model.entity.RaffleAwardEntity;
import cn.bugstack.domain.strategy.model.entity.RaffleFactorEntity;
import cn.bugstack.domain.strategy.service.IRaffleStrategy;

/**
 * @author duguanghao
 * @title AbstractRaffleStrategy
 * @date 2026/2/24 21:08
 * @description 抽奖策略抽象类，提供一些公共的抽奖逻辑和方法，可以被具体的抽奖策略实现类继承和使用
 */
public class AbstractRaffleStrategy implements IRaffleStrategy {


    @Override
    public RaffleAwardEntity performRaffle(RaffleFactorEntity raffleFactorEntity) {
        return null;
    }


}
