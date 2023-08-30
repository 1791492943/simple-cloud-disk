package com.simpleclouddisk.start;

import com.simpleclouddisk.config.MinioConfig;
import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.io.File;

@Slf4j
@Component
public class MinioStart implements ApplicationRunner {

    @Autowired
    public MinioClient minioClient;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        boolean fileBucket = minioClient.bucketExists(BucketExistsArgs.builder().bucket(MinioConfig.BUCKET).build());
        if(!fileBucket){
            log.warn("文件桶不存在,正在创建");
            minioClient.makeBucket(MakeBucketArgs.builder().bucket(MinioConfig.BUCKET).build());
            log.info("文件桶创建完成");
        }

        if(!new File(MinioConfig.shardPath).exists()){
            log.warn("分片临时目录不存在,正在创建");
            new File(MinioConfig.shardPath).mkdirs();
            log.info("分片临时目录创建完成");
        }

        if(!new File(MinioConfig.thumbnail).exists()){
            log.warn("缩略图目录不存在,正在创建");
            new File(MinioConfig.thumbnail).mkdirs();
            log.info("缩略图目录创建完成");
        }
    }
}
