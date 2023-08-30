package com.simpleclouddisk.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simpleclouddisk.domain.entity.FileShard;
import com.simpleclouddisk.service.FileShardService;
import com.simpleclouddisk.mapper.FileShardMapper;
import org.springframework.stereotype.Service;

/**
* @author Administrator
* @description 针对表【file_shard】的数据库操作Service实现
* @createDate 2023-08-09 11:47:54
*/
@Service
public class FileShardServiceImpl extends ServiceImpl<FileShardMapper, FileShard>
    implements FileShardService{

}




