package com.simpleclouddisk.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simpleclouddisk.config.CodeConfig;
import com.simpleclouddisk.config.PasswordConfig;
import com.simpleclouddisk.domain.User;
import com.simpleclouddisk.exception.ServiceException;
import com.simpleclouddisk.exception.service.LoginException;
import com.simpleclouddisk.exception.service.PasswordException;
import com.simpleclouddisk.exception.service.RegisterException;
import com.simpleclouddisk.service.UserService;
import com.simpleclouddisk.mapper.UserMapper;
import com.simpleclouddisk.utils.CaptchaGenerator;
import com.simpleclouddisk.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 * @description 针对表【user(用户信息)】的数据库操作Service实现
 * @createDate 2023-08-07 08:52:13
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private UserMapper userMapper;

    /**
     * 生成短信验证码
     *
     * @param phone
     */
    @Override
    public void code(String phone) {
        // 查询redis是否存在验证码
        String s = redisTemplate.opsForValue().get(RedisUtil.redisPhoneCode(phone));
        if (Objects.nonNull(s)) {
            return;
        }
        // 创建验证码
        String code = CaptchaGenerator.code();
        redisTemplate.opsForValue().set(RedisUtil.redisPhoneCode(phone), code, CodeConfig.CODE_TIME, TimeUnit.MINUTES);
    }

    /**
     * 登录
     *
     * @param user
     */
    @Override
    public String login(User user) throws ServiceException {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper
                .eq(user.getEmail() != null, User::getEmail, user.getEmail())
                .eq(user.getPhone() != null, User::getPhone, user.getPhone());
        User userInfo = userMapper.selectOne(queryWrapper);

        // 账号不存在
        if (Objects.isNull(userInfo)) {
            throw new LoginException("账号不存在!");
        }

        // 验证码登录
        String redisPhone = RedisUtil.redisPhoneCode(userInfo.getPhone());
        String code = redisTemplate.opsForValue().get(redisPhone);
        if(user.getPassword().equals(code)){
            StpUtil.login(userInfo.getUserId());
            String token = StpUtil.getTokenValue();

            // 设置最后登录时间
            userInfo.setLastTime(new Timestamp(System.currentTimeMillis()));
            this.updateById(userInfo);

            return token;
        }

        // 密码错误
        if (!BCrypt.checkpw(user.getPassword(), userInfo.getPassword())) {
            // 查询 redis 密码错误次数
            String redisUserId = RedisUtil.redisPasswordCount(userInfo.getUserId());
            String count = redisTemplate.opsForValue().get(redisUserId);

            if("-1".equals(count)){
                throw new ServiceException("账号已被锁定,请等待 " + redisTemplate.opsForValue().getOperations().getExpire(redisUserId) + " 秒后再次尝试");
            }

            // 第一次密码错误
            if (Objects.isNull(count)) {
                // 密码错误永久累加
                redisTemplate.opsForValue().set(redisUserId, "1");
                throw new PasswordException("密码错误,你还有 " + (PasswordConfig.COUNT - 1) + " 次机会");
            }

            // 多次密码错误
            int countAdd = Integer.parseInt(count) + 1;
            // 密码错误次数达到上限 锁定10分钟 -1 表示上限
            if(countAdd == PasswordConfig.COUNT){
                redisTemplate.opsForValue().set(redisUserId, "-1", PasswordConfig.TIME, TimeUnit.MINUTES);
                throw new PasswordException("账号已被锁定 " + PasswordConfig.TIME + " 分钟");
            }
            // 密码错误没有达到上限 次数加 1
            redisTemplate.opsForValue().set(redisUserId, String.valueOf(countAdd));
            throw new PasswordException("密码错误,你还有 " + (PasswordConfig.COUNT - countAdd) + " 次机会");

        }

        // 登录成功
        StpUtil.login(userInfo.getUserId());
        String token = StpUtil.getTokenValue();

        // 清空密码累计次数
        String redisUserId = RedisUtil.redisPasswordCount(userInfo.getUserId());
        redisTemplate.opsForValue().set(redisUserId,"0");

        // 设置最后登录时间
        userInfo.setLastTime(new Timestamp(System.currentTimeMillis()));
        this.updateById(userInfo);

        return token;
    }

    /**
     * 注册
     *
     * @param user
     */
    @Override
    public void register(User user) throws RegisterException {
        String code = redisTemplate.opsForValue().get(RedisUtil.redisPhoneCode(user.getPhone()));
        String password = user.getPassword();
        if(!password.equals(code)){
            throw new RegisterException("验证码错误!");
        }
        // 添加注册时间
        user.setCreateTime(new Timestamp(System.currentTimeMillis()));
        // 密码设置为空
        user.setPassword(null);
        this.save(user);
    }

    /**
     * 修改密码
     * @param user
     */
    @Override
    public void updatePassword(User user) throws ServiceException {
        long userId = StpUtil.getLoginIdAsLong();

        // 只有在密码为空 (没有设置过密码) 时,才能直接设置密码
        User user1 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        if(Objects.nonNull(user1.getPassword())){
            throw new ServiceException("密码已设置过,要修改密码请通过修改密码进入");
        }

        User userInfo = new User();
        userInfo.setUserId(userId);

        // 密码加密
        String password = user.getPassword();
        String hashpw = BCrypt.hashpw(password);

        userInfo.setPassword(hashpw);
        this.updateById(userInfo);
    }
}




