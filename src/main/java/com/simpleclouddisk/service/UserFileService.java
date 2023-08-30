package com.simpleclouddisk.service;

import com.simpleclouddisk.domain.dto.FileSecondsPassDto;
import com.simpleclouddisk.domain.entity.UserFile;
import com.baomidou.mybatisplus.extension.service.IService;
import com.simpleclouddisk.exception.service.SpaceException;

/**
* @author Administrator
* @description 针对表【user_file】的数据库操作Service
* @createDate 2023-08-27 09:39:28
*/
public interface UserFileService extends IService<UserFile> {

    void secondsPass(FileSecondsPassDto fileSecondsPassDto) throws SpaceException;
}
