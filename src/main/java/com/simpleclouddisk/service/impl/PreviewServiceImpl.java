package com.simpleclouddisk.service.impl;

import com.simpleclouddisk.config.MinioConfig;
import com.simpleclouddisk.domain.entity.FileInfo;
import com.simpleclouddisk.mapper.FileMapper;
import com.simpleclouddisk.service.PreviewService;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

@Service
public class PreviewServiceImpl implements PreviewService {

    @Autowired
    private FileMapper fileMapper;

    @Override
    public void image(Long fileId, HttpServletResponse response) throws IOException {
        FileInfo fileInfo = fileMapper.selectById(fileId);
        String fileCover = fileInfo.getFileCover();
        File file = new File(MinioConfig.thumbnail + "/" + fileCover);
        try (FileInputStream inputStream = new FileInputStream(file);
             ServletOutputStream outputStream = response.getOutputStream();
        ) {
            IOUtils.copy(inputStream, outputStream);
        }
    }
}
