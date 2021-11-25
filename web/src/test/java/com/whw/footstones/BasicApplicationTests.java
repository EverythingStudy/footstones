package com.whw.footstones;


import com.whw.footstones.config.redis.RedisService;
import com.whw.footstones.core.util.Result;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = BasicApplication.class)
@Slf4j
public class BasicApplicationTests {

    @Autowired
    RedisService redisService;

    @Test
    public void TestRedis() {
        redisService.set("test", "key");
        Result.success("suc");
    }

}
