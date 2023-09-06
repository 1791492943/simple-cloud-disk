package com.simpleclouddisk.utils;

import com.simpleclouddisk.config.MinioConfig;
import io.minio.*;
import io.minio.errors.*;
import io.minio.http.Method;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.*;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Component
public class MinioUtil {


    public static MinioClient minioClient;

    @Autowired
    public void setMinioClient(MinioClient minioClient) {
        MinioUtil.minioClient = minioClient;
    }

    /**
     * 上传
     *
     * @param file
     * @return 上传后的文件名
     */
    public static String upload(File file, String suffix) throws IOException, ServerException, InsufficientDataException, ErrorResponseException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file));
        long size = file.length();

        String uuid = UUID.randomUUID() + suffix;
        minioClient.putObject(PutObjectArgs.builder()
                .stream(inputStream, size, MinioConfig.SIZE) // 文件输入流 文件大小 分片大小(-1 不分片)
                .bucket(MinioConfig.BUCKET) // 桶
                .object(uuid) // 文件名
                .build());
        inputStream.close();
        return uuid;

    }

    /**
     * 下载
     *
     * @param fileName     文件名
     * @param outputStream 输出流
     */
    public static void download(String fileName, OutputStream outputStream) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        GetObjectResponse object = minioClient.getObject(GetObjectArgs.builder()
                .bucket(MinioConfig.BUCKET) // 桶
                .object(fileName) // 文件名
                .build());
        IOUtils.copy(object, outputStream);
    }

    /**
     * 获取文件输入流
     *
     * @param fileName
     * @return
     */
    public static InputStream getFile(String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getObject(
                GetObjectArgs.builder()
                        .bucket(MinioConfig.BUCKET) // 桶
                        .object(fileName) // 文件名
                        .build()
        );
    }

    /**
     * 预览
     *
     * @param fileName 文件名
     * @return 预览网址
     */
    public static String preview(String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs
                .builder()
                .bucket(MinioConfig.BUCKET) // 桶
                .object(fileName) // 文件名
                .method(Method.GET) // 请求的方式
                .build());
    }

    /**
     * 删除
     *
     * @param fileName 文件名
     */
    public static void delete(String fileName) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException {
        minioClient.removeObject(RemoveObjectArgs
                .builder()
                .bucket(MinioConfig.BUCKET) // 桶
                .object(fileName) // 文件名
                .build());
    }

    /**
     * 获取文件大小
     */
    public static Long size(String fileName) throws Exception {
        StatObjectResponse statObjectResponse = minioClient.statObject(StatObjectArgs
                .builder()
                .bucket(MinioConfig.BUCKET)
                .object(fileName)
                .build());
        return statObjectResponse.size();
    }

}
