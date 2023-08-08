package com.simpleclouddisk.service;

import com.simpleclouddisk.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;
import com.simpleclouddisk.exception.ServiceException;
import com.simpleclouddisk.exception.service.LoginException;
import com.simpleclouddisk.exception.service.PasswordException;
import com.simpleclouddisk.exception.service.RegisterException;

/**
* @author Administrator
* @description 针对表【user(用户信息)】的数据库操作Service
* @createDate 2023-08-07 08:52:13
*/
public interface UserService extends IService<User> {

    /**
     * 生成短信验证码
     * @param phone
     */
    void code(String phone);

    /**
     * 登录
     * @param user
     */
    String login(User user) throws ServiceException;

    /**
     * 注册
     * @param user
     */
    void register(User user) throws RegisterException;

    /**
     * 修改密码
     * @param user
     */
    void updatePassword(User user) throws ServiceException;
}
