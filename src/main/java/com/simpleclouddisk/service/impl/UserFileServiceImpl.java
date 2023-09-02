package com.simpleclouddisk.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simpleclouddisk.code.FileCode;
import com.simpleclouddisk.domain.dto.FileSecondsPassDto;
import com.simpleclouddisk.domain.entity.FileInfo;
import com.simpleclouddisk.domain.entity.User;
import com.simpleclouddisk.domain.entity.UserFile;
import com.simpleclouddisk.exception.service.SpaceException;
import com.simpleclouddisk.mapper.FileMapper;
import com.simpleclouddisk.mapper.UserMapper;
import com.simpleclouddisk.service.UserFileService;
import com.simpleclouddisk.mapper.UserFileMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;

/**
 * @author Administrator
 * @description 针对表【user_file】的数据库操作Service实现
 * @createDate 2023-08-27 09:39:28
 */
@Service
public class UserFileServiceImpl extends ServiceImpl<UserFileMapper, UserFile> implements UserFileService {

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private UserMapper userMapper;

    @Override
    @Transactional(rollbackFor = SpaceException.class)
    public void secondsPass(FileSecondsPassDto fileSecondsPassDto) throws SpaceException {
        FileInfo fileInfo = fileMapper.selectOne(new LambdaQueryWrapper<FileInfo>().eq(FileInfo::getFileName, fileSecondsPassDto.getMinioName()));
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        UserFile userFile = UserFile.builder()
                .userId(StpUtil.getLoginIdAsLong())
                .fileId(fileInfo.getFileId())
                .filePid(fileSecondsPassDto.getFilePid())
                .fileName(fileSecondsPassDto.getFileName())
                .minioName(fileSecondsPassDto.getMinioName())
                .folderType(FileCode.TYPE_FILE)
                .fileSize(fileInfo.getFileSize())
                .delFlag(FileCode.DEL_NO)
                .createTime(timestamp)
                .updateTime(timestamp)
                .build();
        this.save(userFile);

        userMapper.setSpace(StpUtil.getLoginIdAsLong(),fileInfo.getFileSize());
        User user = userMapper.selectById(StpUtil.getLoginIdAsLong());
        if(user.getUseSpace() > user.getTotalSpace()){
            throw new SpaceException("空间不足,请扩容!");
        }
    }
}




