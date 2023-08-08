package com.simpleclouddisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simpleclouddisk.domain.FileInfo;
import com.simpleclouddisk.service.FileInfoService;
import com.simpleclouddisk.mapper.FileInfoMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【file_info】的数据库操作Service实现
* @createDate 2023-08-08 15:19:24
*/
@Service
public class FileInfoServiceImpl extends ServiceImpl<FileInfoMapper, FileInfo>
    implements FileInfoService{

}




