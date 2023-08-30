package com.simpleclouddisk.mapper;

import com.simpleclouddisk.domain.entity.User;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author Administrator
 * @description 针对表【user(用户信息)】的数据库操作Mapper
 * @createDate 2023-08-07 08:52:13
 * @Entity com.simpleclouddisk.domain.entity.User
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    void setSpace(long userId, long size);

}




