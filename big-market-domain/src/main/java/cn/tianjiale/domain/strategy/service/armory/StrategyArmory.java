package cn.tianjiale.domain.strategy.service.armory;


import cn.tianjiale.domain.strategy.model.entity.StrategyAwardEntity;
import cn.tianjiale.domain.strategy.repository.IStrategyRepository;
import cn.tianjiale.types.exception.AppException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.SecureRandom;
import java.util.*;

@Slf4j
@Service
public class StrategyArmory implements IStrategyArmory{
    @Resource
    private IStrategyRepository strategyRepository;
    @Override
    public boolean assembleLotteryStrategy(Long strategyId) {
        //1.调用仓储层进行查询数据
        List<StrategyAwardEntity> strategyAwardEntities = strategyRepository.queryStrategyAwardList(strategyId);

        //2.获取最小概率值
        BigDecimal minRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);

        //3.获取总的概率值
        BigDecimal tatalRate = strategyAwardEntities.stream()
                .map(StrategyAwardEntity::getAwardRate)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        //4.获得奖品中奖范围
        BigDecimal rangeRate = tatalRate.divide(minRate,0,RoundingMode.CEILING);

        //5.循环填充list
        List<Integer> awardList = new ArrayList<>(rangeRate.intValue());
        for(StrategyAwardEntity strategyAward : strategyAwardEntities){
            BigDecimal awardRate = strategyAward.getAwardRate();
            int count = awardRate.multiply(rangeRate).setScale(0,RoundingMode.CEILING).intValue();
            for(int i = 0;i < count;i++){
                awardList.add(strategyAward.getAwardId());
            }
        }

        //6.乱序
        Collections.shuffle(awardList);

        //7.填充到map集合中
        Map<Integer,Integer> awardMap = new HashMap<>();
        for(int i = 0;i < awardList.size();i++){
            awardMap.put(i,awardList.get(i));
        }

        //8.存储到redis包括strategyId,rangeAward,map集合
        strategyRepository.storeStrategyAwardSearchRateTable(strategyId,awardMap.size(),awardMap);
        return true;
    }

    @Override
    public Integer getRandomAwardId(Long strategyId) {
        //1.查询总的抽奖范围
        Integer rateRange = strategyRepository.getRateRange(strategyId);
        //2.根据总的抽奖范围去生成随机数获取奖品ID
       return strategyRepository.getStrategyAwardAssemble(strategyId,new SecureRandom().nextInt(rateRange));
    }
}
