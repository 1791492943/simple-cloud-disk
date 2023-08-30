package com.simpleclouddisk.mapper;

import com.simpleclouddisk.domain.entity.UserFile;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
* @author Administrator
* @description 针对表【user_file】的数据库操作Mapper
* @createDate 2023-08-27 09:39:28
* @Entity com.simpleclouddisk.domain.entity.UserFile
*/
@Mapper
public interface UserFileMapper extends BaseMapper<UserFile> {

}




