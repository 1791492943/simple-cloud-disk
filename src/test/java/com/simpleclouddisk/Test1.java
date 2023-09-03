package com.simpleclouddisk;

import cn.dev33.satoken.secure.BCrypt;
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
        File file = new File("E:\\视频\\游戏\\Desktop\\23.mp4");
        InputStream inputStream = new FileInputStream(file);
        String s = DigestUtils.md5Hex(inputStream);
        System.out.println(s);
    }

    @Test
    void password() throws IOException {
        String hashpw = BCrypt.hashpw("123456");
        System.out.println(hashpw);
        boolean bool = BCrypt.checkpw("123456", "$2a$10$DdnrBCKBl2Pjz8snpU8LNOmOOovHV95rm6oXIX7LhMDa7hwug9IFK");
        System.out.println(bool);
    }

}
