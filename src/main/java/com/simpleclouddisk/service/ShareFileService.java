package com.simpleclouddisk.service;

import com.simpleclouddisk.domain.dto.ShareFileDto;
import com.simpleclouddisk.domain.entity.ShareFile;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpServletResponse;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
* @author Administrator
* @description 针对表【share_file】的数据库操作Service
* @createDate 2023-09-12 09:40:35
*/
public interface ShareFileService extends IService<ShareFile> {

    String share(List<Long> fileList, Integer time);

    List<ShareFileDto> parseUrl(String url);

    void image(Long fileId, HttpServletResponse response) throws IOException;
}
