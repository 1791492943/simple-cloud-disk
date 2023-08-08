package com.simpleclouddisk;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

@SpringBootTest
public class Test1 {

    @Autowired
    public StringRedisTemplate redisTemplate;

    @Test
    void redisTTL(){
        Long expire = redisTemplate.opsForValue().getOperations().getExpire("phone-code: 1688408149066895362");
        System.out.println(expire);
    }

}
