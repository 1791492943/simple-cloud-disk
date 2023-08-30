package com.simpleclouddisk.mapper;

import com.simpleclouddisk.domain.entity.FileInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 * @description 针对表【file_info】的数据库操作Mapper
 * @createDate 2023-08-08 15:19:24
 * @Entity com.simpleclouddisk.domain.entity.FileInfo
 */
@Mapper
public interface FileMapper extends BaseMapper<FileInfo> {

}




