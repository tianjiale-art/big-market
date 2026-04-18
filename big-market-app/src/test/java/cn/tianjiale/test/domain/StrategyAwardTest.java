package cn.tianjiale.test.domain;


import cn.tianjiale.domain.strategy.service.armory.IStrategyArmory;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class StrategyAwardTest {
    @Resource
    private IStrategyArmory strategyArmory;
@Test
    public void test_strategyArmory(){
        boolean result = strategyArmory.assembleLotteryStrategy(100001l);
        log.info("测试结果：{}",result);
    Integer randomAwardId = strategyArmory.getRandomAwardId(100001l);
    log.info("抽奖结果：{}",randomAwardId);
    }
    @Test
    public void test_getAWard(){

    }
}
