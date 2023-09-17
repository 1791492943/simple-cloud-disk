package com.simpleclouddisk.service;

import com.simpleclouddisk.domain.dto.ShareDto;
import com.simpleclouddisk.domain.dto.ShareFileDto;
import com.simpleclouddisk.domain.entity.ShareFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.simpleclouddisk.exception.ServiceException;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
* @author Administrator
* @description 针对表【share_file】的数据库操作Service
* @createDate 2023-09-12 09:40:35
*/
public interface ShareFileService extends IService<ShareFile> {

    Map<String, String> share(List<Long> fileList, Integer time);

    List<ShareFileDto> parseUrl(ShareDto share) throws ServiceException;

    void image(Long fileId, HttpServletResponse response) throws IOException;

    boolean urlExist(String url);


    List shareList();

    void unshare(List<Long> ids);

}
