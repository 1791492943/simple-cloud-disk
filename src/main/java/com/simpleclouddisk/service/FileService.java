package com.simpleclouddisk.service;

import com.simpleclouddisk.domain.dto.DownloadListDto;
import com.simpleclouddisk.domain.entity.FileInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import io.minio.errors.*;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;
import java.util.Map;

/**
 * @author Administrator
 * @description 针对表【file_info】的数据库操作Service
 * @createDate 2023-08-08 15:19:24
 */
public interface FileService extends IService<FileInfo> {
    /**
     * 上传文件
     * @param file
     * @throws Exception
     */
    void upload(MultipartFile file, String fileShard) throws Exception;

    /**
     * 在线预览
     * @throws Exception
     */
    String preview(Long fileId) throws Exception;

    /**
     * 删除文件
     * @param fileName
     * @throws Exception
     */
    void delete(String fileName) throws Exception;

    /**
     * 下载文件
     * @param fileName
     * @throws Exception
     */
    void download(String minioName, String fileName, HttpServletResponse response) throws Exception;

    Map fileExist(String fileMd5);

    boolean shardExist(String shardMd5);

    void downloadList(List<String> fileNameList, List<String> minioNameList , HttpServletResponse response) throws ServerException, InsufficientDataException, ErrorResponseException, IOException, NoSuchAlgorithmException, InvalidKeyException, InvalidResponseException, XmlParserException, InternalException;
}
