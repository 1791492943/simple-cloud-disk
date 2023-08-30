package com.simpleclouddisk.config;

import io.minio.MinioClient;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "minio")
@Data
public class MinioConfig {

    /**
     * 桶
     */
    public static final String BUCKET = "file";
    /**
     * 分片大小
     * -1 不分片
     */
    public static final Integer SIZE = -1;

    public static String bucket;
    public static String url;
    public static String accessKey;
    public static String secretKey;
    public static String shardPath;
    public static String thumbnail;

    public void setBucket(String bucket) {
        MinioConfig.bucket = bucket;
    }
    public void setUrl(String url) {
        MinioConfig.url = url;
    }
    public void setAccessKey(String accessKey) {
        MinioConfig.accessKey = accessKey;
    }
    public void setShardPath(String shardPath) {
        MinioConfig.shardPath = shardPath;
    }
    public void setSecretKey(String secretKey) {
        MinioConfig.secretKey = secretKey;
    }
    public void setThumbnail(String thumbnail) {
        MinioConfig.thumbnail = thumbnail;
    }

    @Bean
    public MinioClient minioClient() {
        return MinioClient
                .builder()
                .endpoint(url)
                .credentials(accessKey, secretKey)
                .build();
    }

}
