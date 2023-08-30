package com.simpleclouddisk.jobhandler;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.simpleclouddisk.domain.entity.User;
import com.simpleclouddisk.domain.entity.UserFile;
import com.simpleclouddisk.mapper.FileMapper;
import com.simpleclouddisk.mapper.UserFileMapper;
import com.simpleclouddisk.mapper.UserMapper;
import com.xxl.job.core.context.XxlJobHelper;
import com.xxl.job.core.handler.annotation.XxlJob;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import static net.sf.jsqlparser.parser.feature.Feature.delete;

@Component
@Slf4j
public class Job {

    @Autowired
    private UserFileMapper userFileMapper;

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FileMapper fileMapper;

    /**
     * 清空回收站
     */
    @XxlJob("emptyTheRecycleBin")
    public void emptyTheRecycleBin(){
        List<UserFile> userFiles = userFileMapper.selectList(new LambdaQueryWrapper<UserFile>().le(UserFile::getRecoveryTime, Timestamp.valueOf(LocalDateTime.now().minusDays(14))));
        for (UserFile userFile : userFiles) {
            // 删除信息
            userFileMapper.deleteById(userFile);
            // 恢复大小
            Long fileSize = fileMapper.selectById(userFile.getFileId()).getFileSize();
            userMapper.setSpace(userFile.getUserId(),fileSize * -1);
        }
        log.info("本次清除 {} 个回收站的文件",userFiles.size());
        XxlJobHelper.log("本次清除 {} 个回收站的文件",userFiles.size());
    }
}
