package cn.tianjiale.test.domain;


import cn.tianjiale.domain.strategy.model.entity.RaffleAwardEntity;
import cn.tianjiale.domain.strategy.model.entity.RaffleFactorEntity;
import cn.tianjiale.domain.strategy.service.IRaffleStrategy;
import cn.tianjiale.domain.strategy.service.armory.IStrategyArmory;
import cn.tianjiale.domain.strategy.service.armory.IStrategyDispatch;
import cn.tianjiale.domain.strategy.service.rule.impl.RuleWeightLogicFilter;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.util.ReflectionTestUtils;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyAwardTest {
    @Resource
    private IStrategyArmory strategyArmory;
    @Resource
    private IStrategyDispatch strategyDispatch;
    @Resource
    private IRaffleStrategy raffleStrategy;
    @Resource
    private RuleWeightLogicFilter ruleWeightLogicFilter;
/*@Before
    public void test_strategyArmory(){
    boolean b = strategyArmory.assembleLotteryStrategy(100001l);
    log.info("测试结果:{}",b);
  }
  @Test
    public void test_strategyDispatch(){
      Integer randomAwardId1 = strategyDispatch.getRandomAwardId(100001l);
      log.info("普通抽奖:{}",randomAwardId1);
      Integer randomAwardId = strategyDispatch.getRandomAwardId(100001l, "4000:102,103,104,105");
      Integer randomAwardId2 = strategyDispatch.getRandomAwardId(100001l, "5000:102,103,104,105,106,107");
      Integer randomAwardId3 = strategyDispatch.getRandomAwardId(100001l, "6000:102,103,104,105,106,107,108,109");

      log.info("抽奖结果权重方式:4000:{},5000:{},6000:{}",randomAwardId,randomAwardId2,randomAwardId3);
  }*/


    @Before
    public void setUp() {
        ReflectionTestUtils.setField(ruleWeightLogicFilter, "userScore", 40500L);
    }

    @Test
    public void test_performRaffle() {
        RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                .userId("tianjaile")
                .strategyId(100001L)
                .build();
        RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
        log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
        log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
    }

}
