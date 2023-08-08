package com.simpleclouddisk.mapper;

import com.simpleclouddisk.domain.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author Administrator
 * @description 针对表【user(用户信息)】的数据库操作Mapper
 * @createDate 2023-08-07 08:52:13
 * @Entity com.simpleclouddisk.domain.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}




