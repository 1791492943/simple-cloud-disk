package com.simpleclouddisk;

import com.mysql.cj.log.Log;
import org.apache.commons.codec.digest.DigestUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@SpringBootTest
public class Test1 {

    @Autowired
    public StringRedisTemplate redisTemplate;

    @Test
    void redisTTL(){
        Long expire = redisTemplate.opsForValue().getOperations().getExpire("phone-code: 1688408149066895362");
        System.out.println(expire);
    }

    @Test
    void md5() throws IOException {
        File file = new File("E:\\视频\\游戏\\Crossfire\\Crossfire 2023.07.15 - 08.55.23.03.DVR.mp4");
        InputStream inputStream = new FileInputStream(file);
        String s = DigestUtils.md5Hex(inputStream);
        System.out.println(s);
    }


}
