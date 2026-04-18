package cn.tianjiale.test;

import cn.tianjiale.infrastructure.persistent.redis.IRedisService;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.redisson.api.RMap;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
public class ApiTest {
    @Resource
    public IRedisService redisService;


    @Test
    public void test() {
        RMap<Object, Object> map = redisService.getMap("strategy_id_10001");
        map.put(1,"11111");
        map.put(2,"22222");
        log.info("测试结果：{}",redisService.getFromMap("strategy_id_10001",1).toString());
        log.info("测试完成");
    }

}
