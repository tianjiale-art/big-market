package cn.tianjiale.test.domain;


import cn.tianjiale.domain.strategy.model.entity.RaffleAwardEntity;
import cn.tianjiale.domain.strategy.model.entity.RaffleFactorEntity;
import cn.tianjiale.domain.strategy.service.IRaffleStrategy;
import cn.tianjiale.domain.strategy.service.armory.IStrategyArmory;

import cn.tianjiale.domain.strategy.service.rule.chain.impl.RuleWeightLogicChain;

import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


import javax.annotation.Resource;
import java.util.concurrent.CountDownLatch;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyAwardTest {
    @Resource
    private IStrategyArmory strategyArmory;

    @Resource
    private IRaffleStrategy raffleStrategy;
    @Resource
    private RuleWeightLogicChain ruleWeightLogicChain;

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
        // 策略装配 100001、100002、100003
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100001L));
        log.info("测试结果：{}", strategyArmory.assembleLotteryStrategy(100006L));




    }


    @Test
    public void test_performRaffle() throws Exception {
        for (int i = 0; i < 3; i++) {
            RaffleFactorEntity raffleFactorEntity = RaffleFactorEntity.builder()
                    .userId("xiaofuge")
                    .strategyId(100006L)
                    .build();
            RaffleAwardEntity raffleAwardEntity = raffleStrategy.performRaffle(raffleFactorEntity);
            log.info("请求参数：{}", JSON.toJSONString(raffleFactorEntity));
            log.info("测试结果：{}", JSON.toJSONString(raffleAwardEntity));
        }
        // 等待 UpdateAwardStockJob 消费队列
        new CountDownLatch(1).await();
    }


}
