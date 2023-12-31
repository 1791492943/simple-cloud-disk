package com.simpleclouddisk.mapper;

import com.simpleclouddisk.domain.dto.UploadRecordsDto;
import com.simpleclouddisk.domain.entity.FileShard;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
* @author Administrator
* @description 针对表【file_shard】的数据库操作Mapper
* @createDate 2023-08-09 11:47:54
* @Entity com.simpleclouddisk.domain.entity.FileShard
*/
@Mapper
public interface FileShardMapper extends BaseMapper<FileShard> {

    List<UploadRecordsDto> uploadProgress(Long userId);

    Long shardCount(Long userId);
}




