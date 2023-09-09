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
import com.simpleclouddisk.config.ProjectConfig;
import com.simpleclouddisk.domain.dto.FilePageDto;
import com.simpleclouddisk.domain.dto.UploadRecordsDto;
import com.simpleclouddisk.domain.dto.UserFileDto;
import com.simpleclouddisk.domain.dto.UserLoginDto;
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
import org.springframework.transaction.annotation.Transactional;

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
            userRegister.setTotalSpace(ProjectConfig.space);

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
        Page<UserFile> page = new Page<>(filePageDto.getPageNum(), -1);
        userFileMapper.selectPage(page, new LambdaQueryWrapper<UserFile>()
                .eq(UserFile::getUserId, StpUtil.getLoginIdAsLong())
                .eq((filePageDto.getDel() != FileCode.DEL_YES && filePageDto.getCategory() == 0), UserFile::getFilePid, filePageDto.getPid())
                .eq(UserFile::getDelFlag, filePageDto.getDel())
                .eq(filePageDto.getCategory() != 0, UserFile::getFileCategory, filePageDto.getCategory())
                .orderByDesc(UserFile::getCreateTime));

        Page<UserFileDto> userFileDtoPage = new Page<>();
        BeanUtils.copyProperties(page, userFileDtoPage, "records");

        List<UserFileDto> collect = page.getRecords()
                .stream()
                .map(UserFileDto::new)
//                .skip((filePageDto.getPageNum() - 1) * filePageDto.getPageSize())
//                .limit(filePageDto.getPageSize())
                .collect(Collectors.toList());

        userFileDtoPage.setRecords(collect);
        return userFileDtoPage;
    }

    @Override
    @Transactional
    public void deleteFileById(Long[] fileIds) {
        long userId = StpUtil.getLoginIdAsLong();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        UserFile userFile = new UserFile();
        userFile.setRecoveryTime(timestamp);
        userFile.setDelFlag(FileCode.DEL_YES);
        // 删除后的文件直接进入根目录
        userFile.setFilePid(0L);

        List<UserFile> userFiles = userFileMapper.selectList(new LambdaQueryWrapper<UserFile>().eq(UserFile::getUserId, userId).in(UserFile::getId, fileIds));

        ArrayList<Long> longs = new ArrayList<>();
        getFileList(userFiles, longs);

        // 进入回收站
        userFileMapper.update(userFile, new LambdaQueryWrapper<UserFile>()
                .eq(UserFile::getUserId, userId)
                .in(UserFile::getId, longs));

        // 直接清空回收站的文件夹
        userFileMapper.delete(new LambdaQueryWrapper<UserFile>()
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getFolderType, FileCode.TYPE_FOLDER)
                .eq(UserFile::getDelFlag, FileCode.DEL_YES));
    }

    private void getFileList(List<UserFile> userFileList, List<Long> list) {
        for (UserFile userFile : userFileList) {
            if (userFile.getFolderType() == FileCode.TYPE_FOLDER) {
                List<UserFile> pidAllFile = getPidAllFile(userFile.getId());
                if (pidAllFile.size() > 0) {
                    getFileList(pidAllFile, list);
                }
            }
            list.add(userFile.getId());
        }
    }

    private List<UserFile> getPidAllFile(Long pid) {
        return userFileMapper.selectList(new LambdaQueryWrapper<UserFile>().eq(UserFile::getUserId, StpUtil.getLoginIdAsLong()).in(UserFile::getFilePid, pid));
    }

    @Override
    public void rename(Long id, String fileName) {
        // 当前操作人,判断是否是文件主人
        Long userId = UserUtil.getUserId();

        // 设置属性
        UserFile userFile = new UserFile();
        userFile.setId(id);
        userFile.setFileName(fileName);

        userFileMapper.update(userFile, new LambdaUpdateWrapper<UserFile>()
                .set(UserFile::getFileName, fileName)
                .eq(UserFile::getId, id)
                .eq(UserFile::getUserId, userId));
    }

    @Override
    public void deleteFileInfoById(List<Long> list) {
        // 获取当前操作人信息
        long userId = StpUtil.getLoginIdAsLong();
        // 构建查询
        LambdaQueryWrapper<UserFile> queryWrapper = new LambdaQueryWrapper<UserFile>().eq(UserFile::getUserId, userId).in(UserFile::getId, list);

        // 查询文件信息
        List<UserFile> userFiles = userFileMapper.selectList(queryWrapper);

        List<Long> idList = new ArrayList<>();
        getFileList(userFiles, idList);

        List<UserFile> userFileList = userFileMapper.selectList(
                new LambdaQueryWrapper<UserFile>()
                        .eq(UserFile::getUserId, userId)
                        .in(UserFile::getId, idList));

        // 计算空间
        long space = 0;
        for (UserFile userFile : userFileList) {
            if (userFile.getFolderType() == FileCode.TYPE_FILE) {
                space += userFile.getFileSize();
            }
        }

        // 删除信息
        List<Long> ids = userFileList.stream().map(item -> item.getId()).collect(Collectors.toList());
        userFileMapper.deleteBatchIds(ids);

        // 释放空间
        userMapper.setSpace(userId, space * -1);
    }

    @Override
    public List<UploadRecordsDto> uploadInfo() {
        long userId = StpUtil.getLoginIdAsLong();
        List<UploadRecordsDto> list = fileShardMapper.uploadProgress(userId);
        return list;
    }

    @Override
    public void restore(Long[] fileIds) {
        long userId = StpUtil.getLoginIdAsLong();

        UserFile userFile = new UserFile();
        userFile.setRecoveryTime(null);
        userFile.setDelFlag(0);

        List<UserFile> userFiles = userFileMapper.selectList(
                new LambdaQueryWrapper<UserFile>()
                        .eq(UserFile::getUserId, userId)
                        .in(UserFile::getId, fileIds));

        List<Long> idList = new ArrayList<>();
        getFileList(userFiles, idList);

        userFileMapper.restore(userFile, idList);
    }

    @Override
    public void newFolder(Long pid, String folderName) {
        long userId = StpUtil.getLoginIdAsLong();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        UserFile userFile = UserFile.builder()
                .userId(userId)
                .filePid(pid)
                .fileName(folderName)
                .folderType(FileCode.TYPE_FOLDER)
                .delFlag(FileCode.DEL_NO)
                .createTime(timestamp)
                .updateTime(timestamp)
                .build();

        userFileMapper.insert(userFile);
    }

    @Override
    public List<UserFileDto> search(String name) {
        long userId = StpUtil.getLoginIdAsLong();
        List<UserFile> userFiles = userFileMapper.selectList(new LambdaQueryWrapper<UserFile>()
                .eq(UserFile::getUserId, userId)
                .like(UserFile::getFileName, name)
                .eq(UserFile::getFolderType, FileCode.TYPE_FILE));

        List<UserFileDto> collect = userFiles.stream().map(UserFileDto::new).collect(Collectors.toList());

        return collect;
    }

    @Override
    public void move(List<Long> ids, Long pid) {
        long userId = StpUtil.getLoginIdAsLong();

        UserFile userFile = new UserFile();
        userFile.setFilePid(pid);

        userFileMapper.update(userFile, new LambdaQueryWrapper<UserFile>()
                .eq(UserFile::getUserId, userId)
                .in(UserFile::getId, ids));
    }

    @Override
    public List<UserFileDto> folder(Long pid) {
        long userId = StpUtil.getLoginIdAsLong();

        List<UserFile> userFiles = userFileMapper.selectList(new LambdaQueryWrapper<UserFile>()
                .eq(UserFile::getUserId, userId)
                .eq(UserFile::getFilePid, pid)
                .eq(UserFile::getFolderType,FileCode.TYPE_FOLDER));

        List<UserFileDto> collect = userFiles.stream().map(UserFileDto::new).collect(Collectors.toList());

        return collect;
    }

    @Override
    public void deleteShardByMd5(String md5) {
        long userId = StpUtil.getLoginIdAsLong();
        int num = fileShardMapper.delete(new LambdaQueryWrapper<FileShard>()
                .eq(FileShard::getUserId, userId)
                .eq(FileShard::getFileMd5, md5));

        userMapper.setSpace(userId, num * 1024 * 1024 * 5 * -1);
    }

    @Override
    public long shardNum() {
        long userId = StpUtil.getLoginIdAsLong();
        Long num = fileShardMapper.shardCount(userId);
        return num;
    }

    private List<UserFile> selectFolderById(Long id) {
        List<UserFile> userFiles = userFileMapper.selectList(new LambdaQueryWrapper<UserFile>().eq(UserFile::getFilePid, id));
        return userFiles;
    }

}




