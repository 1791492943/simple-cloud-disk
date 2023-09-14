package com.simpleclouddisk;

import cn.dev33.satoken.secure.BCrypt;
import com.alibaba.fastjson.JSON;
import com.mysql.cj.log.Log;
import com.simpleclouddisk.domain.dto.DownloadListDto;
import com.simpleclouddisk.utils.MinioUtil;
import io.minio.errors.*;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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

    @Test
    void page(){
        List<Integer> list = Arrays.asList(1,2,3,4,5,6,7,8,9,10);

        String jsonString = JSON.toJSONString(list);

        redisTemplate.opsForValue().set("1",jsonString);

        String s = redisTemplate.opsForValue().get("1");
        List list1 = JSON.parseObject(s, List.class);
        System.out.println(list1);
    }

    @Test
    void downloadFile() throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        List<DownloadListDto> fileList = new ArrayList<>();
        DownloadListDto downloadListDto = new DownloadListDto();
        downloadListDto.setMinioName("f44e2d8f-fb14-405d-92b8-cf143fc9273a.json");
        downloadListDto.setFileName("response.json");
        fileList.add(downloadListDto);

        downloadListDto = new DownloadListDto();
        downloadListDto.setMinioName("f665d8b1-b5da-459e-be01-6f8251be9c97.zip");
        downloadListDto.setFileName("icon.zip");
        fileList.add(downloadListDto);

        File file = new File("d:/test.zip");

        try (ZipOutputStream zipOutputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)));){
            for (DownloadListDto fileInfo : fileList) {
                InputStream inputStream = MinioUtil.getFile(fileInfo.getMinioName());
                ZipEntry zipEntry = new ZipEntry(fileInfo.getFileName());

                zipOutputStream.putNextEntry(zipEntry);

                IOUtils.copy(inputStream,zipOutputStream);
                zipOutputStream.closeEntry();
                inputStream.close();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String aaa = aaa();
        System.out.println(aaa);
    }

    public static String aaa(){
        System.out.println("查询");

        new Thread(() -> {
            try {
                System.out.println("redis 开始存数据");
                Thread.sleep(10000);
                System.out.println("redis 存完数据");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();

        System.out.println("查询结束");
        return "内容";
    }
}
