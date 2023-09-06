package com.simpleclouddisk.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import cn.dev33.satoken.stp.StpUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.simpleclouddisk.code.FileCode;
import com.simpleclouddisk.config.CodeConfig;
import com.simpleclouddisk.config.PasswordConfig;
import com.simpleclouddisk.domain.dto.FilePageDto;
import com.simpleclouddisk.domain.dto.UploadRecordsDto;
import com.simpleclouddisk.domain.dto.UserFileDto;
import com.simpleclouddisk.domain.dto.UserLoginDto;
import com.simpleclouddisk.domain.entity.FileInfo;
import com.simpleclouddisk.domain.entity.FileShard;
import com.simpleclouddisk.domain.entity.User;
import com.simpleclouddisk.domain.entity.UserFile;
import com.simpleclouddisk.exception.ServiceException;
import com.simpleclouddisk.exception.service.PasswordException;
import com.simpleclouddisk.mapper.FileMapper;
import com.simpleclouddisk.mapper.FileShardMapper;
import com.simpleclouddisk.mapper.UserFileMapper;
import com.simpleclouddisk.service.UserService;
import com.simpleclouddisk.mapper.UserMapper;
import com.simpleclouddisk.utils.CaptchaGenerator;
import com.simpleclouddisk.utils.RedisUtil;
import com.simpleclouddisk.utils.UserUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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

    @Autowired
    private UserFileMapper userFileMapper;

    @Autowired
    private FileMapper fileMapper;

    @Autowired
    private FileShardMapper fileShardMapper;

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
     * @param userLoginDto
     */
    @Override
    public String login(UserLoginDto userLoginDto) throws ServiceException {
        User user = new User();
        user.setPhone(userLoginDto.getPhone());
        user.setPassword(userLoginDto.getPassword());

        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, user.getPhone());
        User userInfo = userMapper.selectOne(queryWrapper);

        // 账号不存在,直接注册
        if (Objects.isNull(userInfo)) {
            if (userLoginDto.getCode() == null || "".equals(userLoginDto.getCode())) {
                throw new ServiceException("账号未注册");
            }

            String redisPhone = RedisUtil.redisPhoneCode(userLoginDto.getPhone());
            String code = redisTemplate.opsForValue().get(redisPhone);
            if (!userLoginDto.getCode().equals(code)) {
                throw new ServiceException("验证码错误");
            }

            User userRegister = new User();
            userRegister.setPhone(userLoginDto.getPhone());

            userMapper.insert(userRegister);
            StpUtil.login(userRegister.getUserId());
            String token = StpUtil.getTokenValue();

            redisTemplate.opsForValue().getOperations().delete(redisPhone);
            return token;
        }

        if (userLoginDto.getCode() != null && !"".equals(userLoginDto.getCode())) {
            // 验证码登录
            String redisPhone = RedisUtil.redisPhoneCode(userInfo.getPhone());
            String code = redisTemplate.opsForValue().get(redisPhone);
            if (userLoginDto.getCode() != null && userLoginDto.getCode().equals(code)) {
                StpUtil.login(userInfo.getUserId());
                String token = StpUtil.getTokenValue();

                // 设置最后登录时间
                userInfo.setLastTime(new Timestamp(System.currentTimeMillis()));
                this.updateById(userInfo);

                redisTemplate.opsForValue().getOperations().delete(redisPhone);
                return token;
            }
        } else if (userLoginDto.getPassword() != null && !"".equals(userLoginDto.getPassword())) {
            // 密码登录
            // 密码错误
            System.out.println(BCrypt.checkpw(user.getPassword(), userInfo.getPassword()));
            if (!BCrypt.checkpw(user.getPassword(), userInfo.getPassword())) {
                // 查询 redis 密码错误次数
                String redisUserId = RedisUtil.redisPasswordCount(userInfo.getUserId());
                String count = redisTemplate.opsForValue().get(redisUserId);

                if ("-1".equals(count)) {
                    throw new ServiceException("账号已被锁定,请等待 " + redisTemplate.opsForValue().getOperations().getExpire(redisUserId) + " 秒后再次尝试");
                }

                // 第一次密码错误
                if (Objects.isNull(count)) {
                    // 密码错误累加
                    redisTemplate.opsForValue().set(redisUserId, "1", PasswordConfig.TIME, TimeUnit.MINUTES);
                    throw new PasswordException("密码错误,你还有 " + (PasswordConfig.COUNT - 1) + " 次机会");
                }

                // 多次密码错误
                int countAdd = Integer.parseInt(count) + 1;
                // 密码错误次数达到上限 锁定10分钟 -1 表示上限
                if (countAdd == PasswordConfig.COUNT) {
                    redisTemplate.opsForValue().set(redisUserId, "-1", PasswordConfig.TIME, TimeUnit.MINUTES);
                    throw new PasswordException("账号已被锁定 " + PasswordConfig.TIME + " 分钟");
                }
                // 密码错误没有达到上限 次数加 1
                redisTemplate.opsForValue().set(redisUserId, String.valueOf(countAdd), PasswordConfig.TIME, TimeUnit.MINUTES);
                throw new PasswordException("密码错误,你还有 " + (PasswordConfig.COUNT - countAdd) + " 次机会");

            }

            // 登录成功
            StpUtil.login(userInfo.getUserId());
            String token = StpUtil.getTokenValue();

            // 清空密码累计次数
            String redisUserId = RedisUtil.redisPasswordCount(userInfo.getUserId());
            redisTemplate.opsForValue().set(redisUserId, "0");

            // 设置最后登录时间
            userInfo.setLastTime(new Timestamp(System.currentTimeMillis()));
            this.updateById(userInfo);

            return token;
        }
        return null;
    }

    /**
     * 修改密码
     *
     * @param user
     */
    @Override
    public void updatePassword(User user) throws ServiceException {
        long userId = StpUtil.getLoginIdAsLong();

        // 只有在密码为空 (没有设置过密码) 时,才能直接设置密码
        User user1 = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUserId, userId));
        if (Objects.nonNull(user1.getPassword())) {
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

    @Override
    public Map<String, Long> getSpace() {
        Long loginId = StpUtil.getLoginIdAsLong();
        User user = userMapper.selectById(loginId);
        Map<String, Long> map = new HashMap<>();
        map.put("use", user.getUseSpace());
        map.put("total", user.getTotalSpace());
        return map;
    }

    @Override
    public Page page(FilePageDto filePageDto) {
        Page<UserFile> page = new Page<>(filePageDto.getPageNum(), filePageDto.getPageSize());
        Page<UserFile> userFilePage = userFileMapper.selectPage(page, new LambdaQueryWrapper<UserFile>()
                .eq(UserFile::getUserId, StpUtil.getLoginIdAsLong())
                .eq(UserFile::getFilePid, filePageDto.getPid())
                .eq(UserFile::getDelFlag,filePageDto.getDel())
                .eq(filePageDto.getCategory() != 0, UserFile::getFileCategory,filePageDto.getCategory()));

        Page<UserFileDto> userFileDtoPage = new Page<>();
        BeanUtils.copyProperties(userFilePage,userFileDtoPage,"records");

        List<UserFileDto> collect = userFilePage.getRecords()
                .stream()
                .map(item -> new UserFileDto(item))
                .collect(Collectors.toList());

        userFileDtoPage.setRecords(collect);
        return userFileDtoPage;
    }

    @Override
    public void deleteFileById(Long[] fileIds) {
        long userId = StpUtil.getLoginIdAsLong();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        UserFile userFile = new UserFile();
        userFile.setRecoveryTime(timestamp);
        userFile.setDelFlag(FileCode.DEL_YES);
        userFileMapper.update(userFile,new LambdaQueryWrapper<UserFile>().eq(UserFile::getUserId, userId).in(UserFile::getId, fileIds));
    }

    @Override
    public void rename(Long id, String fileName) {
        // 当前操作人,判断是否是文件主人
        Long userId = UserUtil.getUserId();

        // 设置属性
        UserFile userFile = new UserFile();
        userFile.setId(id);
        userFile.setFileName(fileName);

        userFileMapper.update(userFile,new LambdaUpdateWrapper<UserFile>()
                .set(UserFile::getFileName,fileName)
                .eq(UserFile::getId,id)
                .eq(UserFile::getUserId,userId));
    }

    @Override
    public void deleteFileInfoById(List<Long> list) {
        long userId = StpUtil.getLoginIdAsLong();

        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<UserFile>().eq(UserFile::getUserId, userId).in(UserFile::getId, list);

        // 查询文件信息
        List<UserFile> userFiles = userFileMapper.selectList(queryWrapper);

        // 计算空间
        long space = 0;
        for (UserFile fileInfo : userFiles) {
            space += fileInfo.getFileSize();
        }

        // 删除信息
        userFileMapper.delete(queryWrapper);

        // 释放空间
        userMapper.setSpace(userId,space * -1);
    }

    @Override
    public List<UploadRecordsDto> uploadInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        List<UploadRecordsDto> list = fileShardMapper.uploadProgress(userId);
        return list;
    }
}




