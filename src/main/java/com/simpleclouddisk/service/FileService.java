package com.simpleclouddisk.service;

import com.simpleclouddisk.domain.entity.FileInfo;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.web.multipart.MultipartFile;

import java.io.OutputStream;
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
     * @param outputStream
     * @throws Exception
     */
    void download(String fileName, OutputStream outputStream) throws Exception;

    Map fileExist(String fileMd5);

}
